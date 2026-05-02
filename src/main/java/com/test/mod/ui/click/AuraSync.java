package com.test.mod.ui.click;

import com.test.mod.utils.render.ColorUtil;

public class AuraSync {
    public static int getAuraColor(int index) {
        return ColorUtil.rainbow(index * 100, 15, 107);
    }
}
