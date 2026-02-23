package com.test.mod.transformer.utils;

import com.test.mod.asm.Opcodes;
import com.test.mod.asm.Type;
import com.test.mod.asm.tree.AbstractInsnNode;
import com.test.mod.asm.tree.FieldInsnNode;
import com.test.mod.asm.tree.MethodInsnNode;
import com.test.mod.asm.tree.MethodNode;
import com.test.mod.transformer.annotation.At;
import com.test.mod.transformer.mapping.Mapping;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PointFinder {

    public List<AbstractInsnNode> find(MethodNode method, At at) {
        List<AbstractInsnNode> points = new ArrayList<>();
        String type = at.value().toUpperCase();
        String targetStr = at.target();

        TargetInfo targetInfo = null;
        if (!targetStr.isEmpty() && (type.equals("INVOKE") || type.equals("FIELD"))) {
            targetInfo = parseAndMap(targetStr);
        }

        switch (type) {
            case "HEAD" -> {
                if (method.name.equals("<init>")) {
                    points.add(findPostSuper(method));
                } else {
                    points.add(skipLabels(method.instructions.getFirst()));
                }
            }

            case "RETURN", "TAIL" -> {
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn.getOpcode() >= Opcodes.IRETURN && insn.getOpcode() <= Opcodes.RETURN) {
                        points.add(insn);
                    }
                }
            }

            case "INVOKE" -> {
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof MethodInsnNode min) {
                        if (fastMatch(min.owner, min.name, min.desc, targetInfo)) {
                            points.add(insn);
                        }
                    }
                }
            }

            case "FIELD" -> {
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof FieldInsnNode fin) {
                        if (fastMatch(fin.owner, fin.name, fin.desc, targetInfo)) {
                            points.add(insn);
                        }
                    }
                }
            }
        }
        return points;
    }

    private boolean fastMatch(String owner, String name, String desc, TargetInfo target) {
        if (target == null) return true;

        boolean ownerMatch = target.owner == null || owner.equals(target.owner);
        boolean nameMatch = name.equals(target.name);
        boolean descMatch = target.desc == null || desc.equals(target.desc);

        return ownerMatch && nameMatch && descMatch;
    }

    private TargetInfo parseAndMap(String target) {
        String tOwner = null;
        String tName = target;
        String tDesc = null;

        if (target.contains("(") || target.contains(":")) {
            String normalized = target.replace(".", "/");
            if (normalized.startsWith("L") && normalized.contains(";")) {
                int semicolon = normalized.indexOf(";");
                tOwner = normalized.substring(1, semicolon);
                String rest = normalized.substring(semicolon + 1);

                if (rest.contains(":")) {
                    String[] parts = rest.split(":");
                    tName = parts[0];
                    tDesc = parts[1];
                } else {
                    int paren = rest.indexOf("(");
                    if (paren != -1) {
                        tName = rest.substring(0, paren);
                        tDesc = rest.substring(paren);
                    }
                }
            } else if (normalized.contains(":")) {
                String[] parts = normalized.split(":");
                tName = parts[0];
                tDesc = parts[1];
            }
        }

        String mappedOwner = null;
        String mappedName = tName;
        String mappedDesc = null;

        if (tOwner != null) {
            mappedOwner = Mapping.getInternalName(tOwner);
            try {
                Class<?> clazz = Class.forName(tOwner.replace("/", "."));
                mappedName = Mapping.get(clazz, tName, tDesc);
            } catch (ClassNotFoundException ignored) {
                // ?
            }
        }

        if (tDesc != null) {
            mappedDesc = mapDesc(tDesc);
        }

        return new TargetInfo(mappedOwner, mappedName, mappedDesc);
    }

    private AbstractInsnNode skipLabels(AbstractInsnNode insn) {
        while (insn != null && (insn.getOpcode() == -1 || insn.getOpcode() == Opcodes.NOP))
            insn = insn.getNext();
        return insn;
    }
    public String mapDesc(String desc) {
        if (desc == null || desc.isEmpty()) return desc;

        if (desc.contains("(")) {
            Type returnType = Type.getReturnType(desc);
            Type[] argumentTypes = Type.getArgumentTypes(desc);

            StringBuilder sb = new StringBuilder();
            sb.append('(');
            for (Type arg : argumentTypes) {
                sb.append(mapType(arg));
            }
            sb.append(')');
            sb.append(mapType(returnType));
            return sb.toString();
        } else {
            return mapType(Type.getType(desc));
        }
    }

    private String mapType(Type type) {
        switch (type.getSort()) {
            case Type.OBJECT -> {
                String internalName = type.getInternalName();
                String mappedName = Mapping.getInternalName(internalName);
                return "L" + mappedName + ";";
            }
            case Type.ARRAY -> {
                String descriptor = type.getDescriptor();
                return descriptor.substring(0, descriptor.lastIndexOf('[') + 1) + mapType(type.getElementType());
            }
            default -> {
                return type.getDescriptor();
            }
        }
    }
    private AbstractInsnNode findPostSuper(MethodNode constructor) {
        for (AbstractInsnNode insn : constructor.instructions) {
            if (insn.getOpcode() == Opcodes.INVOKESPECIAL && ((MethodInsnNode) insn).name.equals("<init>")) {
                return insn.getNext();
            }
        }
        return constructor.instructions.getFirst();
    }

    private record TargetInfo(String owner, String name, String desc) { }
}