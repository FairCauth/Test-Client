package com.test.mod.ui.click.panels.settings;

import com.test.mod.ui.system.Render2D;
import com.test.mod.ui.system.utils.CanvasStack;
import lombok.Getter;

import java.awt.*;

public abstract class AbsSettingPanel<T extends SettingWrapper<?>> {
    @Getter
    private final T settingWrapper;
    @Getter
    protected float x, y, width, height;
    private float baseHeight;
    public AbsSettingPanel(T settingWrapper, float baseHeight) {
        this.settingWrapper = settingWrapper;
        this.baseHeight =baseHeight;
    }
    public float onRenderFirst(CanvasStack canvasStack, float x, float y, float width) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = baseHeight;

        Render2D.drawRect(canvasStack, getX(), getY(), getWidth(), getHeight(), 0, new Color(22,22,22,150).getRGB());
        float off = onRender(canvasStack, x, y, width);
        return height + off;
    }
    protected abstract float onRender(CanvasStack canvasStack, float x, float y, float width);
    public abstract void mouseClicked(double p_94695_, double p_94696_, int p_94697_);
    public abstract void mouseReleased(double p_94722_, double p_94723_, int p_94724_);
}
