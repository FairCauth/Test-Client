package com.test.mod.transformer.utils;

import com.test.mod.asm.ClassReader;
import com.test.mod.asm.ClassWriter;
import com.test.mod.asm.Type;
import com.test.mod.asm.tree.*;
import org.apache.commons.compress.utils.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.test.mod.asm.ClassWriter.COMPUTE_FRAMES;
import static com.test.mod.asm.ClassWriter.COMPUTE_MAXS;

public class Tools {
    public static String toDesc(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        for (Class<?> param : method.getParameterTypes()) {
            sb.append(Type.getDescriptor(param));
        }

        sb.append(")");
        sb.append(Type.getDescriptor(method.getReturnType()));

        return sb.toString();
    }
    public static byte[] getClassBytes(Class<?> c) throws IOException {
        String className = c.getName();
        String classAsPath = className.replace('.', '/') + ".class";

        try (InputStream stream = c.getClassLoader().getResourceAsStream(classAsPath)) {
            return stream != null ? IOUtils.toByteArray(stream) : null;
        }
    }



    public static Object invokeMethod(Object instance, Method method, @Nullable Object... values) {
        try {
            method.setAccessible(true);
            return method.invoke(instance, values);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MethodNode getMethod(ClassNode cn, String desc, String name, String obfName) {
        for (MethodNode mn : cn.methods) {
            if ((mn.name.equals(name) || mn.name.equals(obfName)) && mn.desc.equals(desc))
                return mn;
        }
        return null;
    }
    public static FieldNode getField(ClassNode cn, String desc, String name) {
        for (FieldNode mn : cn.fields) {
            if ((mn.name.equals(name)) && mn.desc.equals(desc))
                return mn;
        }
        return null;
    }
    public static MethodNode cloneMethod(MethodNode original) {
        MethodNode copy = new MethodNode(
                original.access,
                original.name,
                original.desc,
                original.signature,
                original.exceptions.toArray(new String[0])
        );

        original.accept(copy); // ASM 自动复制所有内容

        return copy;
    }
    public static MethodNode getMethod(ClassNode cn, String desc, String name) {
        for (MethodNode mn : cn.methods) {
            if ((mn.name.equals(name)) && mn.desc.equals(desc))
                return mn;
        }
        return null;
    }

    public static MethodNode getMethod(ClassNode cn, String name) {
        for (MethodNode mn : cn.methods) {
            if ((mn.name.equals(name)))
                return mn;
        }
        return null;
    }

    public static byte[] rewriteClass(ClassNode node) {
        ClassWriter writer = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        node.accept(writer);
        return writer.toByteArray();
    }

    public static ClassNode getClassNode(byte[] bytes) throws IllegalStateException {
        if (bytes == null || bytes.length < 10) {
            throw new IllegalStateException("Invalid class bytes");
        }

        if (!isValidClassFile(bytes)) {
            throw new IllegalStateException("Invalid class file magic bytes");
        }

        try {
            ClassReader reader = new ClassReader(bytes);
            ClassNode node = new ClassNode();
            reader.accept(node, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

            if (node.name == null || node.name.isEmpty()) {
                throw new IllegalStateException("Class name is Null/Empty");
            }

            return node;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse class file", e);
        }
    }

    private static boolean isValidClassFile(byte[] bytes) {
        return bytes[0] == (byte)0xCA && bytes[1] == (byte)0xFE &&
                bytes[2] == (byte)0xBA && bytes[3] == (byte)0xBE;
    }


    public static AbstractInsnNode getPoint(MethodNode methodNode, int index, String desc, String... funcName) {
        int i = 0;
        for (AbstractInsnNode instruction : methodNode.instructions) {
            if (!(instruction instanceof MethodInsnNode min))
                continue;

            for (String name : funcName) {
                if (min.name.equals(name)) {
                    ++i;
                    if (i == index && (desc.isEmpty() || min.desc.equals(desc)))
                        return min.getPrevious();
                }

            }
        }
        return null;
    }

    public static AbstractInsnNode getPoint(MethodNode methodNode, String str) {
        int i = 0;
        for (AbstractInsnNode instruction : methodNode.instructions) {
            if (instruction instanceof LdcInsnNode) {
                LdcInsnNode ldcInsnNode = (LdcInsnNode) instruction;
                if (ldcInsnNode.cst instanceof String && ldcInsnNode.cst.equals(str)) {
                    return instruction;
                }
            }

        }
        return null;
    }

    public static AbstractInsnNode getPoint(MethodNode methodNode, String... funcName) {
        for (AbstractInsnNode instruction : methodNode.instructions) {
            if (!(instruction instanceof MethodInsnNode))
                continue;
            MethodInsnNode min = (MethodInsnNode) instruction;
            for (String name : funcName) {
                if (min.name.equals(name))
                    return min.getPrevious();
            }
        }
        return null;
    }

    public static int castToInteger(Object object) {
        return Integer.parseInt(String.valueOf(object));
    }

    public static float castToFloat(Object object) {
        return Float.parseFloat(String.valueOf(object));
    }

    public static double castToDouble(Object object) {
        return Double.parseDouble(String.valueOf(object));
    }

    public static boolean castToBoolean(Object object) {
        return Boolean.parseBoolean(String.valueOf(object));
    }

    public static long castToLong(Object object) {
        return Long.parseLong(String.valueOf(object));
    }

    public static String castToString(Object object) {
        return String.valueOf(object);
    }


}
