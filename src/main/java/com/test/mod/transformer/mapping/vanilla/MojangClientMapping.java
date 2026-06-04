package com.test.mod.transformer.mapping.vanilla;


import com.test.mod.asm.commons.Remapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MojangClientMapping extends Remapper {
    public record FieldKey(String owner, String name, String desc) {}
    public record FieldNameKey(String owner, String name) {}
    public record MethodKey(String owner, String name, String desc) {}
    private final Map<String, String> classMap = new HashMap<>();
    private final Map<FieldKey, String> fieldMap = new HashMap<>();
    private final Map<FieldNameKey, String> fieldNameMap = new HashMap<>();
    private final Map<MethodKey, String> methodMap = new HashMap<>();

    public static MojangClientMapping load(Path path) throws IOException {
        MojangClientMapping mapping = new MojangClientMapping();

        List<String> lines = Files.readAllLines(path);

        // 第一遍：先解析所有 class
        for (String raw : lines) {
            if (raw == null || raw.isBlank()) {
                continue;
            }

            if (Character.isWhitespace(raw.charAt(0))) {
                continue;
            }

            String line = raw.trim();

            // com.mojang.blaze3d.Blaze3D -> ega:
            if (line.contains(" -> ") && line.endsWith(":")) {
                String[] parts = line.substring(0, line.length() - 1).split(" -> ");
                if (parts.length == 2) {
                    String deobf = parts[0].trim().replace('.', '/');
                    String obf = parts[1].trim().replace('.', '/');

                    mapping.classMap.put(deobf, obf);
                }
            }
        }

        // 第二遍：解析 field / method
        String currentOwner = null;

        for (String raw : lines) {
            if (raw == null || raw.isBlank()) {
                continue;
            }

            if (!Character.isWhitespace(raw.charAt(0))) {
                String line = raw.trim();

                if (line.contains(" -> ") && line.endsWith(":")) {
                    String[] parts = line.substring(0, line.length() - 1).split(" -> ");
                    if (parts.length == 2) {
                        currentOwner = parts[0].trim().replace('.', '/');
                    }
                }

                continue;
            }

            if (currentOwner == null) {
                continue;
            }

            String line = raw.trim();

            // 例如：
            // 12:13:void process(com.mojang.blaze3d.pipeline.RenderPipeline,float) -> a
            // int someField -> b
            if (!line.contains(" -> ")) {
                continue;
            }

            String[] parts = line.split(" -> ");
            if (parts.length != 2) {
                continue;
            }

            String left = stripLineNumberPrefix(parts[0].trim());
            String obfName = parts[1].trim();

            if (left.contains("(")) {
                parseMethod(mapping, currentOwner, left, obfName);
            } else {
                parseField(mapping, currentOwner, left, obfName);
            }
        }

        return mapping;
    }

    private static void parseMethod(
            MojangClientMapping mapping,
            String owner,
            String left,
            String obfName
    ) {
        int parenStart = left.indexOf('(');
        int parenEnd = left.lastIndexOf(')');

        if (parenStart == -1 || parenEnd == -1) {
            return;
        }

        int space = left.lastIndexOf(' ', parenStart);
        if (space == -1) {
            return;
        }

        String returnType = left.substring(0, space).trim();
        String methodName = left.substring(space + 1, parenStart).trim();
        String argsText = left.substring(parenStart + 1, parenEnd).trim();

        String desc = buildMethodDesc(argsText, returnType);

        mapping.methodMap.put(
                new MethodKey(owner, methodName, desc),
                obfName
        );
    }

    private static void parseField(
            MojangClientMapping mapping,
            String owner,
            String left,
            String obfName
    ) {
        int space = left.lastIndexOf(' ');
        if (space == -1) {
            return;
        }

        String type = left.substring(0, space).trim();
        String fieldName = left.substring(space + 1).trim();

        String desc = toDesc(type);

        mapping.fieldMap.put(
                new FieldKey(owner, fieldName, desc),
                obfName
        );
        mapping.fieldNameMap.put(
                new FieldNameKey(owner, fieldName),
                obfName
        );
    }

    private static String stripLineNumberPrefix(String text) {
        // 处理：
        // 12:13:void render(...) -> a
        // 去掉前面的 12:13:
        while (true) {
            int firstColon = text.indexOf(':');
            if (firstColon == -1) {
                return text;
            }

            int secondColon = text.indexOf(':', firstColon + 1);
            if (secondColon == -1) {
                return text;
            }

            String a = text.substring(0, firstColon);
            String b = text.substring(firstColon + 1, secondColon);

            if (isNumber(a) && isNumber(b)) {
                text = text.substring(secondColon + 1);
            } else {
                return text;
            }
        }
    }

    private static boolean isNumber(String s) {
        if (s.isEmpty()) return false;

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static String buildMethodDesc(String argsText, String returnType) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');

        if (!argsText.isBlank()) {
            String[] args = argsText.split(",");
            for (String arg : args) {
                sb.append(toDesc(arg.trim()));
            }
        }

        sb.append(')');
        sb.append(toDesc(returnType));

        return sb.toString();
    }

    private static String toDesc(String type) {
        type = type.trim();

        int arrayDepth = 0;

        while (type.endsWith("[]")) {
            arrayDepth++;
            type = type.substring(0, type.length() - 2);
        }

        String base;

        switch (type) {
            case "void" -> base = "V";
            case "boolean" -> base = "Z";
            case "byte" -> base = "B";
            case "char" -> base = "C";
            case "short" -> base = "S";
            case "int" -> base = "I";
            case "long" -> base = "J";
            case "float" -> base = "F";
            case "double" -> base = "D";
            default -> base = "L" + type.replace('.', '/') + ";";
        }

        return "[".repeat(arrayDepth) + base;
    }

    @Override
    public String map(String internalName) {
        return classMap.getOrDefault(internalName, internalName);
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        return fieldMap.getOrDefault(
                new FieldKey(owner, name, descriptor),
                name
        );
    }
    public String mapFieldName(String owner, String name) {
        return fieldNameMap.get(new FieldNameKey(owner, name));

    }
    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        if (name.equals("<init>") || name.equals("<clinit>")) {
            return name;
        }

        return methodMap.getOrDefault(
                new MethodKey(owner, name, descriptor),
                name
        );
    }

    public String mapClassName(String internalName) {
        return map(internalName);
    }

    public String mapFieldDesc(String desc) {
        return mapDesc(desc);
    }

    public String mapMethodDescriptor(String desc) {
        return mapMethodDesc(desc);
    }
}