package com.fair.preload;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
//此类只用于加载自身jar class
//注入Core.dll时会先加载此类 此类保存在Core里 在这里改完注入完不生效。

//registerNatives用于注册NativeBridge的所有native方法
public class Preloader extends Thread {
    public static String CORE_DLL = getMainPath() + "\\lib\\Core.dll";
    public static String getMainPath() {
        return "C:\\Test";
    }
    static {
        System.load(CORE_DLL);
    }
    public byte[][] classes;
    public static Class<?> mainClazz = null;
    public ClassLoader classLoader;
    public static void run(byte[][] classes, ClassLoader classLoader) {
        Preloader agentNative = new Preloader();
        agentNative.classLoader = classLoader;
        agentNative.classes = classes;
        agentNative.start();
    }
    public static byte[][] getByteArray(int size) {
        return new byte[size][];
    }

    @Override
    public void run() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);
            Module baseModule = Object.class.getModule();
            Class<?> currentClass = Preloader.class;
            long addr = unsafe.objectFieldOffset(Class.class.getDeclaredField("module"));
            unsafe.getAndSetObject(currentClass, addr, baseModule);
            this.setContextClassLoader(classLoader);

        } catch (Exception e) {
            e.printStackTrace();
        }
        log("[Loader] Thread Run");

        for (byte[] classByte : classes) {
            Class<?> clazz = defineClass(classByte);

            if (clazz == null) {
                log("[Loader] skipped 1 class");
                continue;
            }

            if (clazz.getName().contains("com.test.mod.Main"))
                mainClazz = clazz;
        }
        if (mainClazz == null) {
            log("[Loader] main class null");
            return;
        }
        try {
            log("[Loader] tryInvoke");
            Method method = mainClazz.getDeclaredMethod("attach");
            method.invoke(null);
        } catch (Exception e) {
            log("[Loader] " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static native void log(String string);
    public static native Class<?> defineClass(byte[] clazz);

    public static native void registerNatives(String target);

}
