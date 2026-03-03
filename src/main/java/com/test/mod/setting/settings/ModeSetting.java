package com.test.mod.setting.settings;

import com.test.mod.setting.Setting;
import com.test.mod.setting.attribute.SettingAttribute;
import com.google.gson.JsonElement;

import java.util.List;

public class ModeSetting extends Setting<String> {
    private List<String> modes;
    public boolean open = false;

    public ModeSetting(String value, List<String> modes) {
        super(value);
        this.modes = modes;
    }

    @Override
    public String getJson(JsonElement jsonElement) {
        return jsonElement.getAsString();
    }
    public boolean is(String va) {
        return va.equals(value);
    }
    @Override
    public boolean canSaveConfig() {
        return true;
    }

    @SafeVarargs
    public ModeSetting(String value, List<String> modes, SettingAttribute<String>... settingAttributes) {
        super(value, settingAttributes);
        this.modes = modes;
    }

    public List<String> getModes() {
        return modes;
    }

    public void setModes(List<String> modes) {
        this.modes = modes;
    }
}
