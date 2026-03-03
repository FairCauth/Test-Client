package com.test.mod.ui.click.panels.settings;

import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.ui.system.utils.CanvasStack;

public class BooleanSettingPanel extends AbsSettingPanel<SettingWrapper<BooleanSetting>>{
    public BooleanSettingPanel(SettingWrapper<BooleanSetting> settingWrapper) {
        super(settingWrapper);
    }

    @Override
    public float onRender(CanvasStack canvasStack, float x, float y, float width, float height) {
        return 0;
    }

    @Override
    public void mouseClicked(double p_94695_, double p_94696_, int p_94697_) {

    }

    @Override
    public void mouseReleased(double p_94722_, double p_94723_, int p_94724_) {

    }
}
