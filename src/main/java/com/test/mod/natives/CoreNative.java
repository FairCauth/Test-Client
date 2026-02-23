package com.test.mod.natives;

import com.fair.preload.Preloader;

public class CoreNative {
    static {
        System.load(Preloader.CORE_DLL);
    }
    public static void init() {
        //无任何东西 只是为了让modloader自动加载此class
    }
    public static void on_jni_call(int type) {

    }
    public static native boolean startup();
    public static native int redefineClasses(Class<?> targetClass, byte[] newClassBytes);
    public static native byte[] getClassBytes(Class<?> clazz);
}
