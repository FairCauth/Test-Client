package com.test.mod.ui.click.panels.settings;

import com.test.mod.setting.settings.NumberSetting;
import com.test.mod.ui.click.ClickGuiScreen;
import com.test.mod.ui.system.Render2D;
import com.test.mod.ui.system.font.FontManager;
import com.test.mod.ui.system.utils.CanvasStack;

import java.awt.*;
import java.text.DecimalFormat;

public class NumberSettingPanel extends AbsSettingPanel<SettingWrapper<NumberSetting>>{
    public NumberSettingPanel(SettingWrapper<NumberSetting> settingWrapper) {
        super(settingWrapper, 25);
    }
    private boolean dragging = false;
    @Override
    protected float onRender(CanvasStack canvasStack, float x, float y, float width) {
        FontManager.getFont(6).drawString(canvasStack, getSettingWrapper().getSetting().getName(), getX() + 5, getY() + 2, Color.WHITE.getRGB());

        String renderStr = String.valueOf(getSettingWrapper().getSetting().getValue());
        float strWidth = FontManager.getFont(6).getWidth(renderStr);
        FontManager.getFont(6).drawString(canvasStack, renderStr,
                getX() + getWidth() - strWidth - 5,
                getY() + 2,
                Color.WHITE.getRGB()
        );
        if(dragging) {
            double value = getSettingWrapper().getSetting().getMax() - getSettingWrapper().getSetting().getMin();
            double val = getSettingWrapper().getSetting().getMin() + (clamp_float((float) ((ClickGuiScreen.mouseX - (getX() + 6)) / (double) (getWidth() - 12)), 0, 1)) * value;
            double tmp;
            DecimalFormat df = new DecimalFormat(getSettingWrapper().getSetting().getPrecisePattern());
            String str = df.format(val);
            tmp = Double.parseDouble(str);
            getSettingWrapper().getSetting().setValue(tmp);
        }
        //一个很小的bug [笑哭] 括号
        float progress = (float) ((getWidth() - 12) *
                (getSettingWrapper().getSetting().getValue().doubleValue() - getSettingWrapper().getSetting().getMin())
                / (getSettingWrapper().getSetting().getMax() - getSettingWrapper().getSetting().getMin()));
        Render2D.drawRect(canvasStack, getX() + 6, getY() + 14, getWidth() - 12, 5,4,new Color(99, 99, 99,200).getRGB());
        Render2D.drawRect(canvasStack, getX() + 6, getY() + 14, progress, 5,4,new Color(183, 183, 183,200).getRGB());
        Render2D.drawCircle(canvasStack, getX() + 6 + progress + (4 / 2f) - 1.5f, getY() + 16.5f, 4, Color.WHITE.getRGB());

        return 0;
    }
    public float clamp_float(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }
    @Override
    public void mouseClicked(double p_94695_, double p_94696_, int p_94697_) {
        if(p_94697_ == 0 && ClickGuiScreen.isHovered(getX(), getY(), getWidth(), getHeight())) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(double p_94722_, double p_94723_, int p_94724_) {
        dragging = false;
    }
}
