package com.test.mod.setting.settings;

import com.test.mod.setting.Setting;
import com.google.gson.JsonElement;

import java.awt.*;

public class ColorSetting extends Setting<Color> {
    public ColorSetting(Color value) {
        super(value);
    }

    @Override
    public boolean canSaveConfig() {
        return true;
    }

    @Override
    public String getConfigValue() {
        return getValue().getRGB() + "|" + getValue().getAlpha();
    }

    @Override
    public Color getJson(JsonElement jsonElement) {
        String str = jsonElement.getAsString();
        String[] text = str.split("\\|");
        int rgb = Integer.parseInt(text[0].trim());
        int alpha = Integer.parseInt(text[1].trim());
        Color temp = new Color(rgb);
        return new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), alpha);
    }

}
