package com.test.mod.natives;

import com.fair.preload.Preloader;
import com.test.mod.Main;

public class CoreNative {

    public static void init() {
        System.load(Preloader.MAIN_PATH + "\\" + Preloader.CORE_DLL);
        //无任何东西 只是为了让modloader自动加载此class
    }
    public static void on_jni_call(int type) {

    }
    public static native boolean startup();
    public static native int redefineClasses(Class<?> targetClass, byte[] newClassBytes);
    public static native byte[] getClassBytes(Class<?> clazz);
}
