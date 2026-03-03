package com.test.mod.ui.click.panels.settings;

import com.test.mod.setting.Setting;
import lombok.Getter;

@Getter
public class SettingWrapper <T extends Setting<?>>{
    private final T setting;
    private boolean display;
    public SettingWrapper(T setting) {
        this.setting = setting;
        this.display = setting.isDisplay();
    }
}
