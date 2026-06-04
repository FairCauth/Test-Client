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
import com.test.mod.transformer.mapping.fabric.FabricClientMapping;
import com.test.mod.transformer.mapping.vanilla.MojangClientMapping;
import com.test.mod.transformer.transformers.MinecraftTransformer;
import com.test.mod.ui.system.SkiaManager;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class Main {
    public enum McEnvironment {
        NON_OBF,
        VANILLA_OBF,
        FORGE_OBF,
        FABRIC_OBF
    }
    public static Main INSTANCE = new Main();
    public SkiaManager skiaManager;
    public ModuleManager moduleManager;
    public TransformerLoader transformerLoader;
    public ExternalGui externalGui;
    public static Gson gson = new Gson();
    public static McEnvironment mcEnvironment;
    public static MojangClientMapping mapping;
    public static FabricClientMapping fabric_mapping;
    public void run() {
        try {
            //"D:\\beifen\\Projects\\Test-Client\\tools\\client.txt"
            Path mojangMappingPath = Path.of(Preloader.MAIN_PATH + "\\vanilla_mapping.txt");
            Path fabricTinyPath = Path.of(Preloader.MAIN_PATH + "\\fabric_mapping.tiny");
            mapping = MojangClientMapping.load(mojangMappingPath);
            fabric_mapping = FabricClientMapping.load(fabricTinyPath, mapping);
            mcEnvironment = detect();
            //            if(mcEnvironment == McEnvironment.VANILLA_OBF) {
//
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("current launch mode " + mcEnvironment);

        prepare();
        Preloader.reconnect("127.0.0.1", 9999);
        moduleManager = new ModuleManager();
        skiaManager = new SkiaManager();
        transformerLoader = new TransformerLoader();
        Preloader.send("current launch mode " + mcEnvironment);


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
    public static McEnvironment detect() {
        Class<?> clazz;
        try {
            clazz = Class.forName("net.minecraft.client.Minecraft");
        } catch (Throwable ignored) {
            try {
                //net.minecraft.class_310
                String result = fabric_mapping.map(
                        "net/minecraft/client/Minecraft"
                ).replace("/", ".");
                clazz = Class.forName(result);
                return McEnvironment.FABRIC_OBF;
            }catch (Throwable ignored1) {
                return McEnvironment.VANILLA_OBF;
            }

        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("getInstance")) {
                return McEnvironment.NON_OBF;
            }
        }
        return McEnvironment.FORGE_OBF;
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
