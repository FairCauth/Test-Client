package com.test.mod.setting.settings;

import com.test.mod.setting.Setting;
import com.google.gson.JsonElement;

public class TextSetting extends Setting<String> {
    public TextSetting(String value) {
        super(value);
    }
    @Override
    public boolean canSaveConfig() {
        return true;
    }
    @Override
    public String getJson(JsonElement jsonElement) {
        return jsonElement.getAsString();
    }
}
