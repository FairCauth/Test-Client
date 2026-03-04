package com.test.mod;

import com.test.mod.module.ModuleManager;
import com.test.mod.natives.CoreNative;
import com.fair.preload.Preloader;
import com.test.mod.transformer.TransformerLoader;
import com.test.mod.ui.system.SkiaManager;

public class Main {
    public static Main INSTANCE = new Main();

    public SkiaManager skiaManager;
    public ModuleManager moduleManager;
    public TransformerLoader transformerLoader;
    public void run() {

        prepare();

        moduleManager = new ModuleManager();
        skiaManager = new SkiaManager();
        transformerLoader = new TransformerLoader();

    }
    private void prepare() {
        System.setProperty("skija.library.path", "C:\\Test\\lib");
        CoreNative.init();
        Preloader.registerNatives("Lcom/test/mod/natives/CoreNative;");
        CoreNative.startup();
    }
}
