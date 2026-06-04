package com.test.mod;

import com.fair.preload.Preloader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.test.mod.asm.Type;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.ModuleManager;
import com.test.mod.setting.Setting;
import com.test.mod.setting.SettingManager;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.ModeSetting;
import com.test.mod.setting.settings.NumberSetting;
import com.test.mod.transformer.mapping.fabric.FabricClientMapping;
import com.test.mod.transformer.mapping.vanilla.MojangClientMapping;
import com.test.mod.ui.click.panels.settings.BooleanSettingPanel;
import com.test.mod.ui.click.panels.settings.ModeSettingPanel;
import com.test.mod.ui.click.panels.settings.NumberSettingPanel;
import com.test.mod.ui.click.panels.settings.SettingWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.renderer.RenderType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Arrays;

public class Client {


    public static Gson gson = new Gson();


    // 测试
    public static void main(String[] args) throws Exception {
//        System.load(Preloader.MAIN_PATH + "\\" + Preloader.CORE_DLL);
//        byte[][] classes = Preloader.getClassByte();
//        ClassLoader classLoader = Preloader.getClassLoader();
//
//
//        System.out.println(Arrays.deepToString(classes));
//        Minecraft
        Path mojangMappingPath = Path.of("D:\\beifen\\Projects\\Test-Client\\tools\\client.txt");
        Path fabricTinyPath = Path.of("D:\\beifen\\Projects\\Test-Client\\tools\\mappings.tiny");

        MojangClientMapping vanilla = MojangClientMapping.load(mojangMappingPath);

        FabricClientMapping fabric = FabricClientMapping.load(
                fabricTinyPath,
                vanilla
        );
        MojangClientMapping mapping =
                MojangClientMapping.load(mojangMappingPath);

        String result = fabric.map(
                "net/minecraft/client/Minecraft"
        ).replace("/", ".");
        String s = fabric.mapMethodName("net/minecraft/client/Minecraft", "getInstance", "()Lnet/minecraft/client/Minecraft;");
        System.out.println(result + " " + s);
//        String obfClassName = mapping.map("net/minecraft/world/entity/LivingEntity");
//        System.out.println(obfClassName);
//
//        String fieldName = "isLocalServer";
//        String o = Type.getInternalName(Minecraft.class);
//        System.out.println(mapping.mapFieldName(o, fieldName));
//        String desc =
//                "(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";
//
//        String vanillaDesc = mapping.mapMethodDesc(desc);
//execute(Ljava/lang/Runnable;)V
//        System.out.println(vanillaDesc);

//         String owner = Type.getInternalName(RenderSystem.class);
//        String name = "disableBlend";
//        String desc2 = "()V";//mouseReleased mouseScrolled DDD)Z
//        String obfName = mapping.mapMethodName(owner, name, desc2);

        //mouseClicked a (DDI)Z
        //mouseReleased b (DDI)Z
        //mouseScrolled a (DDD)Z
        //mouseMoved
//        System.out.println(obfName);



//        boolean connected = Preloader.connect("127.0.0.1", 9999);
//        if(connected) {
//            Preloader.MAIN_PATH = Preloader.sendAndWait("run!");
//            Preloader.CORE_DLL = Preloader.sendAndWait("ask_dll_name");
//            System.out.println("DIRS " + Preloader.MAIN_PATH + " " + Preloader.CORE_DLL);
//            Preloader.startListening();
//            Preloader.send("start transformer 10");
//        }


//        try {
//            Thread.sleep(3000);
//            System.out.println("1111");
//            Preloader.reconnect("127.0.0.1", 9999);
//            Preloader.send("start transformer 10");
//            Preloader.send("afsfa1111111111111111");
//        }catch (Exception e) {
//
//        }
        while (true){

        }
        //String res = Preloader.sendAndWait("run!");
        //System.out.println(res);
        //Preloader.disconnect();
//        new ExternalGui();
//        ModuleManager moduleManager = new ModuleManager();
//        for (AbstractModule module : moduleManager.getModuleList()) {
//
//            JsonObject jsonModule = new JsonObject();
//            jsonModule.addProperty("type", "register_module");
//            jsonModule.addProperty("module", module.getName());
//            jsonModule.addProperty("key", module.getKey());
//            jsonModule.addProperty("enable", module.isEnable());
//            jsonModule.addProperty("category", module.getCategory().name());
//            String text = gson.toJson(jsonModule);
//            System.out.println(text);
//            Preloader.send(text);
//            //setting
//            for (Setting<?> setting : SettingManager.getSettings(module)) {
//                JsonObject jsonSetting = new JsonObject();
//                jsonSetting.addProperty("type", "register_setting");
//                jsonSetting.addProperty("module", module.getName());
//                jsonSetting.addProperty("setting", setting.getName());
//                jsonSetting.addProperty("level", setting.getLevel());
//                jsonSetting.addProperty("display", setting.isDisplay());
//                if(setting instanceof BooleanSetting) {
//                    jsonSetting.addProperty("setting_type", "boolean");
//                    jsonSetting.addProperty("value", ((BooleanSetting) setting).getValue());
//
//                } else if (setting instanceof NumberSetting) {
//                    jsonSetting.addProperty("setting_type", "number");
//                    jsonSetting.addProperty("min", ((NumberSetting) setting).getMin());
//                    jsonSetting.addProperty("max", ((NumberSetting) setting).getMax());
//
//                    String str = "%." + ((NumberSetting) setting).getPrecisePattern().chars().filter(t -> t == '0').count() + "f";
//
//                    jsonSetting.addProperty("precise",
//                            ((NumberSetting) setting).getPrecisePattern().equals("#") ? "%.0f" : str);
//                    jsonSetting.addProperty("value", ((NumberSetting) setting).getValue().floatValue());
//
//                } else if (setting instanceof ModeSetting) {
//                    jsonSetting.addProperty("setting_type", "mode");
//                    jsonSetting.addProperty("value", ((ModeSetting) setting).getValue());
//
//                    JsonArray array = gson.toJsonTree(((ModeSetting) setting).getModes()).getAsJsonArray();
//                    jsonSetting.add("modes", array);
//                }
//                text = gson.toJson(jsonSetting);
//                System.out.println(text);
//                Preloader.send(text);
//
//            }
//        }

    }
}