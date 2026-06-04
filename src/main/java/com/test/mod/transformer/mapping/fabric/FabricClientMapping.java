package com.test.mod.transformer.mapping.fabric;

import com.test.mod.asm.commons.Remapper;
import com.test.mod.transformer.mapping.vanilla.MojangClientMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FabricClientMapping extends Remapper {
    public record MemberKey(String owner, String name, String desc) {}
    public record FieldNameKey(String owner, String name) {}

    private final MojangClientMapping vanillaMapping;

    // official/obf class -> intermediary class
    private final Map<String, String> classMap = new HashMap<>();

    // official/obf owner + official/obf name + official/obf desc -> intermediary name
    private final Map<MemberKey, String> fieldMap = new HashMap<>();
    private final Map<MemberKey, String> methodMap = new HashMap<>();

    // 有些时候你只知道字段名，不知道 desc，可以用这个
    private final Map<FieldNameKey, String> fieldNameMap = new HashMap<>();

    private FabricClientMapping(MojangClientMapping vanillaMapping) {
        this.vanillaMapping = vanillaMapping;
    }

    public static FabricClientMapping load(
            Path tinyPath,
            MojangClientMapping vanillaMapping
    ) throws IOException {
        return load(tinyPath, vanillaMapping, "official", "intermediary");
    }

    public static FabricClientMapping load(
            Path tinyPath,
            MojangClientMapping vanillaMapping,
            String sourceNamespace,
            String targetNamespace
    ) throws IOException {
        FabricClientMapping mapping = new FabricClientMapping(vanillaMapping);

        List<String> lines = Files.readAllLines(tinyPath);

        String header = null;

        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }

            String trimmed = line.trim();

            if (trimmed.startsWith("#")) {
                continue;
            }

            header = trimmed;
            break;
        }

        if (header == null) {
            return mapping;
        }

        if (header.startsWith("tiny\t2\t")) {
            parseTinyV2(
                    mapping,
                    lines,
                    header,
                    sourceNamespace,
                    targetNamespace
            );
        } else if (header.startsWith("v1\t")) {
            parseTinyV1(
                    mapping,
                    lines,
                    header,
                    sourceNamespace,
                    targetNamespace
            );
        } else {
            throw new IOException("Unsupported tiny mapping format: " + header);
        }

        return mapping;
    }
    private static void parseTinyV2(
            FabricClientMapping mapping,
            List<String> lines,
            String header,
            String sourceNamespace,
            String targetNamespace
    ) throws IOException {
        String[] headerParts = header.split("\t");

        if (headerParts.length < 5 || !headerParts[0].equals("tiny")) {
            throw new IOException("Invalid Tiny v2 header: " + header);
        }

        if (!headerParts[1].equals("2")) {
            throw new IOException("Only Tiny v2 is supported: " + header);
        }

        String[] namespaces = Arrays.copyOfRange(headerParts, 3, headerParts.length);

        int sourceIndex = indexOf(namespaces, sourceNamespace);
        int targetIndex = indexOf(namespaces, targetNamespace);

        if (sourceIndex == -1) {
            throw new IOException("Source namespace not found: " + sourceNamespace);
        }

        if (targetIndex == -1) {
            throw new IOException("Target namespace not found: " + targetNamespace);
        }

        String currentSourceOwner = null;

        for (String raw : lines) {
            if (raw == null || raw.isBlank()) {
                continue;
            }

            String trimmed = raw.trim();

            if (trimmed.startsWith("#") || trimmed.startsWith("tiny\t2\t")) {
                continue;
            }

            String[] parts = trimmed.split("\t");

            if (parts.length == 0) {
                continue;
            }

            String type = parts[0];

            // c	officialName	intermediaryName
            if (type.equals("c")) {
                if (parts.length < 1 + namespaces.length) {
                    continue;
                }

                String sourceName = parts[1 + sourceIndex];
                String targetName = parts[1 + targetIndex];

                currentSourceOwner = sourceName;

                if (!sourceName.isEmpty() && !targetName.isEmpty()) {
                    mapping.classMap.put(sourceName, targetName);
                }

                continue;
            }

            if (currentSourceOwner == null) {
                continue;
            }

            // m	desc	officialName	intermediaryName
            if (type.equals("m")) {
                if (parts.length < 2 + namespaces.length) {
                    continue;
                }

                String desc = parts[1];
                String sourceName = parts[2 + sourceIndex];
                String targetName = parts[2 + targetIndex];

                if (!sourceName.isEmpty() && !targetName.isEmpty()) {
                    mapping.methodMap.put(
                            new MemberKey(currentSourceOwner, sourceName, desc),
                            targetName
                    );
                }

                continue;
            }

            // f	desc	officialName	intermediaryName
            if (type.equals("f")) {
                if (parts.length < 2 + namespaces.length) {
                    continue;
                }

                String desc = parts[1];
                String sourceName = parts[2 + sourceIndex];
                String targetName = parts[2 + targetIndex];

                if (!sourceName.isEmpty() && !targetName.isEmpty()) {
                    mapping.fieldMap.put(
                            new MemberKey(currentSourceOwner, sourceName, desc),
                            targetName
                    );

                    mapping.fieldNameMap.put(
                            new FieldNameKey(currentSourceOwner, sourceName),
                            targetName
                    );
                }
            }
        }
    }
    private static void parseTinyV1(
            FabricClientMapping mapping,
            List<String> lines,
            String header,
            String sourceNamespace,
            String targetNamespace
    ) throws IOException {
        // v1	official	intermediary
        String[] headerParts = header.split("\t");

        if (headerParts.length < 3) {
            throw new IOException("Invalid Tiny v1 header: " + header);
        }

        String[] namespaces = Arrays.copyOfRange(headerParts, 1, headerParts.length);

        int sourceIndex = indexOf(namespaces, sourceNamespace);
        int targetIndex = indexOf(namespaces, targetNamespace);

        if (sourceIndex == -1) {
            throw new IOException("Source namespace not found: " + sourceNamespace);
        }

        if (targetIndex == -1) {
            throw new IOException("Target namespace not found: " + targetNamespace);
        }

        // Tiny v1 的 owner/desc 通常是第一个 namespace 的名字。
        // 你的文件是 v1 official intermediary，所以 sourceNamespace=official 正好没问题。
        if (sourceIndex != 0) {
            throw new IOException(
                    "Tiny v1 parser requires source namespace to be the first namespace. source="
                            + sourceNamespace
                            + ", first="
                            + namespaces[0]
            );
        }

        for (String raw : lines) {
            if (raw == null || raw.isBlank()) {
                continue;
            }

            String line = raw.trim();

            if (line.startsWith("#") || line.startsWith("v1\t")) {
                continue;
            }

            String[] parts = line.split("\t");

            if (parts.length == 0) {
                continue;
            }

            String type = parts[0];

            // CLASS	officialClass	intermediaryClass
            if (type.equals("CLASS")) {
                if (parts.length < 1 + namespaces.length) {
                    continue;
                }

                String sourceName = parts[1 + sourceIndex];
                String targetName = parts[1 + targetIndex];

                if (!sourceName.isEmpty() && !targetName.isEmpty()) {
                    mapping.classMap.put(sourceName, targetName);
                }

                continue;
            }

            // FIELD	owner	desc	officialName	intermediaryName
            if (type.equals("FIELD")) {
                if (parts.length < 3 + namespaces.length) {
                    continue;
                }

                String owner = parts[1];
                String desc = parts[2];

                String sourceName = parts[3 + sourceIndex];
                String targetName = parts[3 + targetIndex];

                if (!owner.isEmpty() && !sourceName.isEmpty() && !targetName.isEmpty()) {
                    mapping.fieldMap.put(
                            new MemberKey(owner, sourceName, desc),
                            targetName
                    );

                    mapping.fieldNameMap.put(
                            new FieldNameKey(owner, sourceName),
                            targetName
                    );
                }

                continue;
            }

            // METHOD	owner	desc	officialName	intermediaryName
            if (type.equals("METHOD")) {
                if (parts.length < 3 + namespaces.length) {
                    continue;
                }

                String owner = parts[1];
                String desc = parts[2];

                String sourceName = parts[3 + sourceIndex];
                String targetName = parts[3 + targetIndex];

                if (!owner.isEmpty() && !sourceName.isEmpty() && !targetName.isEmpty()) {
                    mapping.methodMap.put(
                            new MemberKey(owner, sourceName, desc),
                            targetName
                    );
                }
            }
        }
    }
    private static int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], value)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Mojmap internal class name -> Fabric intermediary internal class name
     *
     * 输入:
     * net/minecraft/client/Minecraft
     *
     * 输出:
     * net/minecraft/class_310
     */
    @Override
    public String map(String internalName) {
        if (internalName == null) {
            return null;
        }

        // 第一步：Mojmap -> official
        String officialName = vanillaMapping.map(internalName);

        // 第二步：official -> intermediary
        String intermediaryName = classMap.get(officialName);

        if (intermediaryName != null) {
            return intermediaryName;
        }

        return internalName;
    }

    /**
     * Mojmap field -> Fabric intermediary field
     */
    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        if (owner == null || name == null) {
            return name;
        }

        // owner: Mojmap -> official
        String officialOwner = vanillaMapping.map(owner);

        // desc: Mojmap desc -> official desc
        String officialDesc = descriptor == null
                ? null
                : vanillaMapping.mapFieldDesc(descriptor);

        // field name: Mojmap -> official
        String officialName;

        if (descriptor != null) {
            officialName = vanillaMapping.mapFieldName(owner, name, descriptor);
        } else {
            String mapped = vanillaMapping.mapFieldName(owner, name);
            officialName = mapped == null ? name : mapped;
        }

        String intermediaryName = null;

        if (officialDesc != null) {
            intermediaryName = fieldMap.get(
                    new MemberKey(officialOwner, officialName, officialDesc)
            );
        }

        if (intermediaryName == null) {
            intermediaryName = fieldNameMap.get(
                    new FieldNameKey(officialOwner, officialName)
            );
        }

        if (intermediaryName != null) {
            return intermediaryName;
        }

        return name;
    }

    public String mapFieldName(String owner, String name) {
        return mapFieldName(owner, name, null);
    }

    /**
     * Mojmap method -> Fabric intermediary method
     */
    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        if (name == null) {
            return null;
        }

        if (name.equals("<init>") || name.equals("<clinit>")) {
            return name;
        }

        if (owner == null || descriptor == null) {
            return name;
        }

        // owner: Mojmap -> official
        String officialOwner = vanillaMapping.map(owner);

        // desc: Mojmap desc -> official desc
        String officialDesc = vanillaMapping.mapMethodDescriptor(descriptor);

        // method name: Mojmap -> official
        String officialName = vanillaMapping.mapMethodName(owner, name, descriptor);

        String intermediaryName = methodMap.get(
                new MemberKey(officialOwner, officialName, officialDesc)
        );

        if (intermediaryName != null) {
            return intermediaryName;
        }

        return name;
    }

    public String mapClassName(String internalName) {
        return map(internalName);
    }

    /**
     * 点分类名转换：
     *
     * net.minecraft.client.Minecraft
     * ->
     * net.minecraft.class_310
     */
    public String mapDotClassName(String dotName) {
        if (dotName == null) {
            return null;
        }

        String internal = dotName.replace('.', '/');
        String mapped = map(internal);

        return mapped.replace('/', '.');
    }

    public String mapFieldDesc(String desc) {
        return mapDesc(desc);
    }

    public String mapMethodDescriptor(String desc) {
        return mapMethodDesc(desc);
    }
}
