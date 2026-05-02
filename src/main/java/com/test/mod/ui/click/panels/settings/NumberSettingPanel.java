package com.test.mod.ui.click.panels.settings;

import com.test.mod.setting.settings.NumberSetting;
import com.test.mod.ui.click.AuraSync;
import com.test.mod.ui.click.ClickGuiScreen;
import com.test.mod.ui.system.Render2D;
import com.test.mod.ui.system.font.FontManager;
import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.utils.animation.Direction;
import com.test.mod.utils.animation.impl.DecelerateAnimation;

import java.awt.*;
import java.text.DecimalFormat;

public class NumberSettingPanel extends AbsSettingPanel<SettingWrapper<NumberSetting>>{
    public NumberSettingPanel(SettingWrapper<NumberSetting> settingWrapper) {
        super(settingWrapper, 25);
    }
    private boolean dragging = false;
    private double targetProgress = -1;
    private double animProgress = -1;
    private final DecelerateAnimation dragAnim = new DecelerateAnimation(120, 1);
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
        dragAnim.setDirection(dragging ? Direction.FORWARDS : Direction.BACKWARDS);
        if(dragging) {
            double rawProgress = clamp_float(
                    (float) ((ClickGuiScreen.mouseX - (getX() + 6)) / (double) (getWidth() - 12)),
                    0, 1
            );
            targetProgress = rawProgress;

            double range = getSettingWrapper().getSetting().getMax() - getSettingWrapper().getSetting().getMin();
            double val = getSettingWrapper().getSetting().getMin() + rawProgress * range;
            DecimalFormat df = new DecimalFormat(getSettingWrapper().getSetting().getPrecisePattern());
            String str = df.format(val);
            getSettingWrapper().getSetting().setValue(Double.parseDouble(str));
        }
        if (targetProgress < 0) {
            targetProgress = (getSettingWrapper().getSetting().getValue().doubleValue()
                    - getSettingWrapper().getSetting().getMin())
                    / (getSettingWrapper().getSetting().getMax() - getSettingWrapper().getSetting().getMin());
        }
        animProgress += (targetProgress - animProgress) * 0.18; // 约 150ms 感知延迟
        float trackWidth  = getWidth() - 12;
        float filledWidth = (float) (trackWidth * animProgress);

        double dragP      = dragAnim.getOutput();          // 0→1
        float  thumbRadius = (float) (4 + (6 - 4) * dragP);


//        float progress = (float) ((getWidth() - 12) *
//                (getSettingWrapper().getSetting().getValue().doubleValue() - getSettingWrapper().getSetting().getMin())
//                / (getSettingWrapper().getSetting().getMax() - getSettingWrapper().getSetting().getMin()));
        Render2D.drawRect(canvasStack, getX() + 6, getY() + 14, getWidth() - 12, 5,4,new Color(99, 99, 99,200).getRGB());
        Render2D.drawRect(canvasStack, getX() + 6, getY() + 14, filledWidth, 5,4, AuraSync.getAuraColor(index));


        Render2D.drawCircle(canvasStack,
                getX() + 5 + filledWidth + (4 / 2f) - 1.5f,
                getY() + 16.5f,
                thumbRadius,
                Color.WHITE.getRGB());

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
