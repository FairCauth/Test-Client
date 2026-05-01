package com.test.mod;

import com.test.mod.module.ModuleManager;
import com.test.mod.natives.CoreNative;
import com.fair.preload.Preloader;
import com.test.mod.transformer.TransformerLoader;
import com.test.mod.transformer.transformers.MinecraftTransformer;
import com.test.mod.ui.system.SkiaManager;
import net.minecraft.client.Minecraft;

public class Main {
    public static Main INSTANCE = new Main();

    public SkiaManager skiaManager;
    public ModuleManager moduleManager;
    public TransformerLoader transformerLoader;
    public Client client;

    public void run() {
        prepare();

        moduleManager = new ModuleManager();
        skiaManager = new SkiaManager();
        transformerLoader = new TransformerLoader();


        try {
            Thread.sleep(1000);
        }catch (Exception e) {

        }
//        MinecraftTransformer.isLocalServer(Minecraft.);
        Preloader.send("init ok");

    }
    public static void attach() {
        Main.INSTANCE.run();
    }
    private void prepare() {
        System.setProperty("skija.library.path", Preloader.MAIN_PATH);
        CoreNative.init();
        Preloader.registerNatives("Lcom/test/mod/natives/CoreNative;");
        CoreNative.startup();
    }
}
