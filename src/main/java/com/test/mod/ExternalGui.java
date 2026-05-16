package com.test.mod;

import com.fair.preload.Preloader;
import com.google.gson.*;
import com.test.mod.module.AbstractModule;
import com.test.mod.setting.Setting;
import com.test.mod.setting.SettingManager;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.ModeSetting;
import com.test.mod.setting.settings.NumberSetting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ExternalGui {
    private static final Gson gson = new Gson();
    public ExternalGui() {
        Preloader.setMessageHandler(this::onMessage);
        startServer(8888);
    }
    private ServerSocket serverSocket;
    public void stopServer() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void startServer(int port) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Java已启动，监听端口: " + port);
                while (true) {
                    Socket client = serverSocket.accept();
                    System.out.println("客户端连接: "
                            + client.getInetAddress()
                            + ":" + client.getPort());
                    new Thread(() -> handleClient(client)).start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket client) {

        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println("收到消息: " + line);
                onClientMessage(line);
            }
        } catch (Exception e) {
            System.out.println("客户端断开");
        }
    }
    private void onClientMessage(String message) {
        if(message.equals("reconnect_gui")) {
            Preloader.connect("127.0.0.1", 9999);
            registerMain();
        }
    }

    public void onMessage(String message) {
//        System.out.println(message);
        if(!isJson(message)) return;
        JsonObject json = gson.fromJson(message, JsonObject.class);
        String type = json.get("type").getAsString();


        if(type.equals("update_setting")) {

            String module = json.get("module").getAsString();
            AbstractModule abstractModule = Main.INSTANCE.moduleManager.getModule(module);
            if (abstractModule == null) return;

            String update_type = json.get("update_type").getAsString();
            String settingName = json.get("setting").getAsString();
            Setting<?> setting = SettingManager.getSettings(abstractModule).
                    stream().
                    filter(it -> it.getName().equals(settingName)).
                    toList().
                    get(0);
             if(update_type.equals("boolean")) {
                boolean value = json.get("value").getAsBoolean();
                 if(!(setting instanceof BooleanSetting booleanSetting)) return;
                 booleanSetting.setValue(value);
            }
            if(update_type.equals("number")) {
                float value = json.get("value").getAsFloat();
                if(!(setting instanceof NumberSetting numberSetting)) return;
                numberSetting.setValue(value);
            }
            if(update_type.equals("mode")) {
                String value = json.get("value").getAsString();
                if(!(setting instanceof ModeSetting modeSetting)) return;
                modeSetting.setValue(value);
            }
        }

        if (type.equals("update_module")){
            String module = json.get("module").getAsString();
            AbstractModule abstractModule = Main.INSTANCE.moduleManager.getModule(module);
            if (abstractModule == null) return;

            boolean enable = json.get("enable").getAsBoolean();
            abstractModule.setEnable(enable);
        }
    }
    public void registerMain() {
        Preloader.send("init gui");
        for (AbstractModule module : Main.INSTANCE.moduleManager.getModuleList()) {

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
                    String pat = toImGuiFormat(((NumberSetting) setting).getPrecisePattern());
                    jsonSetting.addProperty("precise", pat);
                    jsonSetting.addProperty("value", ((NumberSetting) setting).getValue().floatValue());

                } else if (setting instanceof ModeSetting) {
                    jsonSetting.addProperty("setting_type", "mode");
                    jsonSetting.addProperty("value", ((ModeSetting) setting).getValue());
                    JsonArray array = gson.toJsonTree(((ModeSetting) setting).getModes()).getAsJsonArray();
                    jsonSetting.add("modes", array);
                }
                text = gson.toJson(jsonSetting);
                Preloader.send(text);

            }
        }
    }
    public void updateModules() {
        if (Main.INSTANCE.moduleManager == null)
            return;

        for (AbstractModule module : Main.INSTANCE.moduleManager.getModuleList()) {
            JsonObject jsonModule = new JsonObject();
            jsonModule.addProperty("type", "update_module");
            jsonModule.addProperty("module", module.getName());
            jsonModule.addProperty("key", module.getKey());
            jsonModule.addProperty("enable", module.isEnable());
            String text = gson.toJson(jsonModule);
            Preloader.send(text);
        }
    }
    public void updateSettings() {
        if (Main.INSTANCE.moduleManager == null)
            return;
        for (AbstractModule module : Main.INSTANCE.moduleManager.getModuleList()) {
            for (Setting<?> setting : SettingManager.getSettings(module)) {
                JsonObject jsonSetting = new JsonObject();
                jsonSetting.addProperty("type", "update_setting");
                jsonSetting.addProperty("module", module.getName());
                jsonSetting.addProperty("setting", setting.getName());
                jsonSetting.addProperty("display", setting.isDisplay());
                if(setting instanceof BooleanSetting) {
                    jsonSetting.addProperty("setting_type", "boolean");
                    jsonSetting.addProperty("value", ((BooleanSetting) setting).getValue());

                } else if (setting instanceof NumberSetting) {
                    jsonSetting.addProperty("setting_type", "number");
                    jsonSetting.addProperty("value", ((NumberSetting) setting).getValue().floatValue());

                } else if (setting instanceof ModeSetting) {
                    jsonSetting.addProperty("setting_type", "mode");
                    jsonSetting.addProperty("value", ((ModeSetting) setting).getValue());

                }
                String text = gson.toJson(jsonSetting);
                Preloader.send(text);
            }
        }
    }

    public String toImGuiFormat(String pattern) {
        if (pattern == null || pattern.isEmpty()) return "%.0f";
        int dotIndex = pattern.indexOf('.');
        if (dotIndex == -1) return "%.0f";
        int decimals = pattern.length() - dotIndex - 1;
        return "%." + decimals + "f";
    }
    public boolean isJson(String str) {
        if (str == null || str.isBlank()) return false;
        try {
            JsonElement el = JsonParser.parseString(str);
            return el.isJsonObject() || el.isJsonArray();
        } catch (JsonSyntaxException e) {
            return false;
        }
    }
}
