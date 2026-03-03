package com.test.mod.setting.settings;


import com.test.mod.setting.Setting;

public abstract class ButtonSetting extends Setting<String> {
    public ButtonSetting() {
        super("");
    }
    public abstract void onClickedButton();
}
