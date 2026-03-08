package com.test.mod.ui.click.panels.settings;

import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.ui.click.ClickGuiScreen;
import com.test.mod.ui.system.Render2D;
import com.test.mod.ui.system.font.FontManager;
import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.utils.animation.Direction;
import com.test.mod.utils.animation.impl.DecelerateAnimation;

import java.awt.*;

public class BooleanSettingPanel extends AbsSettingPanel<SettingWrapper<BooleanSetting>>{
    public BooleanSettingPanel(SettingWrapper<BooleanSetting> settingWrapper) {
        super(settingWrapper, 15);
    }
    public DecelerateAnimation animation = new DecelerateAnimation(200, 1);
    @Override
    protected float onRender(CanvasStack canvasStack, float x, float y, float width) {
        FontManager.getFont(8).drawString(canvasStack, getSettingWrapper().getSetting().getName(), getX() + 5, getY() + 2, Color.WHITE.getRGB());
        animation.setDirection(getSettingWrapper().getSetting().getValue() ? Direction.FORWARDS : Direction.BACKWARDS);
        float buttonWidth = 20, buttonHeight = 10;
        //int enabledColor = new Color();
        boolean enabled = getSettingWrapper().getSetting().getValue();

        //outline
        float outline = 1;
        double progress = animation.getOutput();
        Render2D.drawRect(canvasStack, getX() + getWidth() - buttonWidth - 5 - outline, getY() + 2 -outline, buttonWidth + outline * 2, buttonHeight +outline*2, 10,
                new Color(89,98,89,200).getRGB());
        int smoothCircle = lerpColor(new Color(144,144,144,200).getRGB(), Color.WHITE.getRGB() ,(float) progress);
        Render2D.drawRect(canvasStack, getX() + getWidth() - buttonWidth - 5, getY() + 2, buttonWidth, buttonHeight, 10,new Color(77,77,77,200).getRGB());
        Render2D.drawCircle(canvasStack,
                getX() + getWidth() - buttonWidth + (float) (progress * 10),
                getY() + 7,
                4,smoothCircle
        );
        return 0;
    }
    public static int lerpColor(int color1, int color2, float t) {
        t = Math.max(0, Math.min(1, t));

        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = (color1) & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = (color2) & 0xFF;

        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    @Override
    public void mouseClicked(double p_94695_, double p_94696_, int p_94697_) {
        if(ClickGuiScreen.isHovered(getX(), getY(), getWidth(), getHeight())) {
            if(p_94697_ == 0)  {
                getSettingWrapper().getSetting().setValue(!getSettingWrapper().getSetting().getValue());
            }


        }
    }

    @Override
    public void mouseReleased(double p_94722_, double p_94723_, int p_94724_) {

    }
}
