package com.test.mod;

import com.fair.preload.Preloader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.ModuleManager;
import com.test.mod.setting.Setting;
import com.test.mod.setting.SettingManager;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.ModeSetting;
import com.test.mod.setting.settings.NumberSetting;
import com.test.mod.ui.click.panels.settings.BooleanSettingPanel;
import com.test.mod.ui.click.panels.settings.ModeSettingPanel;
import com.test.mod.ui.click.panels.settings.NumberSettingPanel;
import com.test.mod.ui.click.panels.settings.SettingWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {


    public static Gson gson = new Gson();


    // 测试
    public static void main(String[] args) throws Exception {
        Preloader.connect("127.0.0.1", 9999);
        //String res = Preloader.sendAndWait("run!");
        //System.out.println(res);
        //Preloader.disconnect();
        new ExternalGui();
        ModuleManager moduleManager = new ModuleManager();
        for (AbstractModule module : moduleManager.getModuleList()) {

            JsonObject jsonModule = new JsonObject();
            jsonModule.addProperty("type", "register_module");
            jsonModule.addProperty("module", module.getName());
            jsonModule.addProperty("key", module.getKey());
            jsonModule.addProperty("enable", module.isEnable());
            jsonModule.addProperty("category", module.getCategory().name());
            String text = gson.toJson(jsonModule);
            System.out.println(text);
            Preloader.send(text);
            //setting
            for (Setting<?> setting : SettingManager.getSettings(module)) {
                JsonObject jsonSetting = new JsonObject();
                jsonSetting.addProperty("type", "register_setting");
                jsonSetting.addProperty("module", module.getName());
                jsonSetting.addProperty("setting", setting.getName());
                jsonSetting.addProperty("level", setting.getLevel());
                jsonSetting.addProperty("display", setting.isDisplay());
                if(setting instanceof BooleanSetting) {
                    jsonSetting.addProperty("setting_type", "boolean");
                    jsonSetting.addProperty("value", ((BooleanSetting) setting).getValue());

                } else if (setting instanceof NumberSetting) {
                    jsonSetting.addProperty("setting_type", "number");
                    jsonSetting.addProperty("min", ((NumberSetting) setting).getMin());
                    jsonSetting.addProperty("max", ((NumberSetting) setting).getMax());

                    String str = "%." + ((NumberSetting) setting).getPrecisePattern().chars().filter(t -> t == '0').count() + "f";

                    jsonSetting.addProperty("precise",
                            ((NumberSetting) setting).getPrecisePattern().equals("#") ? "%.0f" : str);
                    jsonSetting.addProperty("value", ((NumberSetting) setting).getValue().floatValue());

                } else if (setting instanceof ModeSetting) {
                    jsonSetting.addProperty("setting_type", "mode");
                    jsonSetting.addProperty("value", ((ModeSetting) setting).getValue());

                    JsonArray array = gson.toJsonTree(((ModeSetting) setting).getModes()).getAsJsonArray();
                    jsonSetting.add("modes", array);
                }
                text = gson.toJson(jsonSetting);
                System.out.println(text);
                Preloader.send(text);

            }
        }
        while (true){

        }
    }
}