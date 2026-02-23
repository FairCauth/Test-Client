package com.test.mod;

import com.test.mod.natives.CoreNative;
import com.fair.preload.Preloader;
import com.test.mod.transformer.TransformerLoader;

public class Main {
    public static Main INSTANCE = new Main();

    public TransformerLoader transformerLoader;
    public void run() {
        prepare();
        System.out.println("1111111111111111111");
        transformerLoader = new TransformerLoader();

    }
    private void prepare() {
        CoreNative.init();
        Preloader.registerNatives("Lcom/test/mod/natives/CoreNative;");
        CoreNative.startup();
    }
}
