package com.test.mod.ui.click.panels.settings;

import com.test.mod.ui.system.utils.CanvasStack;
import lombok.Getter;

public abstract class AbsSettingPanel<T extends SettingWrapper<?>> {
    @Getter
    private final T settingWrapper;
    public AbsSettingPanel(T settingWrapper) {
        this.settingWrapper = settingWrapper;

    }

    public abstract float onRender(CanvasStack canvasStack, float x, float y, float width, float height);
    public abstract void mouseClicked(double p_94695_, double p_94696_, int p_94697_);
    public abstract void mouseReleased(double p_94722_, double p_94723_, int p_94724_);
}
