package com.test.mod;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.ModuleManager;
import com.test.mod.natives.CoreNative;
import com.fair.preload.Preloader;
import com.test.mod.setting.Setting;
import com.test.mod.setting.SettingManager;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.ModeSetting;
import com.test.mod.setting.settings.NumberSetting;
import com.test.mod.transformer.TransformerLoader;
import com.test.mod.transformer.transformers.MinecraftTransformer;
import com.test.mod.ui.system.SkiaManager;
import net.minecraft.client.Minecraft;

import java.net.ServerSocket;

public class Main {
    public static Main INSTANCE = new Main();
    public SkiaManager skiaManager;
    public ModuleManager moduleManager;
    public TransformerLoader transformerLoader;
    public ExternalGui externalGui;
    public static Gson gson = new Gson();
    public void run() {
        prepare();
        Preloader.connect("127.0.0.1", 9999);
        moduleManager = new ModuleManager();
        skiaManager = new SkiaManager();
        transformerLoader = new TransformerLoader();


        try {
            Thread.sleep(1000);
        }catch (Exception e) {

        }
//        MinecraftTransformer.isLocalServer(Minecraft.);
        Preloader.send("init ok");
        externalGui = new ExternalGui();
        externalGui.registerMain();
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

    public static void detach() {
        INSTANCE.moduleManager.cleanup();
        INSTANCE.moduleManager = null;

        INSTANCE.transformerLoader.cleanup();
        INSTANCE.transformerLoader = null;

        INSTANCE.skiaManager.cleanup();
        INSTANCE.skiaManager = null;



       // EventManager.cleanMap(false);
        INSTANCE = null;
        System.gc();
        System.runFinalization();
        System.gc();
        System.gc();

    }
}
