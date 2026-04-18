package com.test.mod.transformer.varhandle;

import com.test.mod.asm.tree.MethodNode;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.HashMap;
import java.util.Map;

public class VarHandleCache {
    private static final Map<String, VarHandle> varHandleMap = new HashMap<>();
    private static final Map<String, MethodHandle> methodHandleMap = new HashMap<>();
    public static Object getField(String fieldName, Object instance) {
        try {
            return varHandleMap.get(fieldName).get(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void setField(String fieldName, Object instance, Object value) {
        try {
            varHandleMap.get(fieldName).set(instance, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static Object invokeMethod(String key, Object instance, Object... args) {
        try {

            return methodHandleMap.get(key).invoke(instance, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    public static void putFieldCache(String c, VarHandle v) {
        varHandleMap.put(c, v);
    }
    public static void putMethodCache(String c, MethodHandle v) {
        methodHandleMap.put(c, v);
    }
    public static VarHandle getCache(String fieldName) {
        return varHandleMap.get(fieldName);
    }
}
