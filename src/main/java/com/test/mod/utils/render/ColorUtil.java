package com.test.mod.utils.render;

import com.test.mod.utils.IMinecraft;
import lombok.experimental.UtilityClass;

import java.awt.*;

public class ColorUtil implements IMinecraft {
    public static int rainbow(int delay, int offset, int index) {
        double timeValue = Math.ceil(System.currentTimeMillis() + ((long) delay * index)) / offset;
        double hueDegrees = timeValue % 360.0;
        float hueFraction = (float) (hueDegrees / 360.0);
        hueFraction = hueFraction < 0.5 ? -hueFraction : hueFraction;
        return Color.getHSBColor(hueFraction, 0.5F, 1.0F).getRGB();

    }
    public static int applyAlpha(int color, int alpha) {
        alpha = Math.max(0, Math.min(255, alpha));
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    public static Color applyAlpha(Color color, float alpha) {
        alpha = Math.min(1.0f, Math.max(0.0f, alpha));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.round(color.getAlpha() * alpha));
    }

    public static int darken(int color) {
        int a = (color >> 24) & 0xFF;
        int r = ((color >> 16) & 0xFF) / 4;
        int g = ((color >> 8) & 0xFF) / 4;
        int b = (color & 0xFF) / 4;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int interpolate(int startColor, int endColor, float ratio) {
        int a = (int) ((startColor >> 24 & 0xFF) + ((endColor >> 24 & 0xFF) - (startColor >> 24 & 0xFF)) * ratio);
        int r = (int) ((startColor >> 16 & 0xFF) + ((endColor >> 16 & 0xFF) - (startColor >> 16 & 0xFF)) * ratio);
        int g = (int) ((startColor >> 8 & 0xFF) + ((endColor >> 8 & 0xFF) - (startColor >> 8 & 0xFF)) * ratio);
        int b = (int) ((startColor & 0xFF) + ((endColor & 0xFF) - (startColor & 0xFF)) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }


}