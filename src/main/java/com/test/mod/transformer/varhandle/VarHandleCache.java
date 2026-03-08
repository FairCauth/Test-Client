package com.test.mod.transformer.varhandle;

import java.lang.invoke.VarHandle;
import java.util.HashMap;
import java.util.Map;

public class VarHandleCache {
    private static final Map<String, VarHandle> varHandleMap = new HashMap<>();
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
    public static void putCache(String c, VarHandle v) {
        varHandleMap.put(c, v);
    }
    public static VarHandle getCache(String fieldName) {
        return varHandleMap.get(fieldName);
    }
}
