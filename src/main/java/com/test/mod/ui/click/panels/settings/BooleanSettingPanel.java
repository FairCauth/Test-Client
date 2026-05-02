package com.test.mod.ui.click.panels.settings;

import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.ui.click.AuraSync;
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


    private float BASE_RADIUS = 4f;
    private float BURST_RADIUS = 5.5f;
    private float STIFFNESS = 140f;
    private float DAMPING = 12f;
    private float springPos = BASE_RADIUS;
    private float springVel = 0f;
    private long lastTime = -1;
    @Override
    protected float onRender(CanvasStack canvasStack, float x, float y, float width) {

        FontManager.getFont(8).drawString(canvasStack, getSettingWrapper().getSetting().getName(), getX() + 5, getY() + 2, Color.WHITE.getRGB());
        animation.setDirection(getSettingWrapper().getSetting().getValue() ? Direction.FORWARDS : Direction.BACKWARDS);
        float buttonWidth = 20, buttonHeight = 10;
        //int enabledColor = new Color();
        boolean enabled = getSettingWrapper().getSetting().getValue();
        double progress = animation.getOutput();


        long now = System.currentTimeMillis();
        if (lastTime < 0) lastTime = now;
        float dt = Math.min((now - lastTime) / 1000f, 0.05f);
        lastTime = now;
        // F = -k*(pos - target)-damping*vel
        float force = -STIFFNESS * (springPos - BASE_RADIUS) - DAMPING * springVel;
        springVel += force * dt;
        springPos += springVel * dt;
        if (Math.abs(springPos - BASE_RADIUS) < 0.01f && Math.abs(springVel) < 0.1f) {
            springPos = BASE_RADIUS;
            springVel = 0f;
        }
        float thumbRadius = springPos;


        int auraColor = AuraSync.getAuraColor(index);
        int smoothCircle = lerpColor(new Color(144,144,144,200).getRGB(), Color.WHITE.getRGB() ,(float) progress);
        int smoothCircle2 = lerpColor(new Color(77,77,77,200).getRGB(), auraColor ,(float) progress);
        int smoothCircle3 = lerpColor(new Color(89,98,89,200).getRGB(), auraColor ,(float) progress);

        //outline
        float outline = 1;
        Render2D.drawRect(canvasStack, getX() + getWidth() - buttonWidth - 5 - outline, getY() + 2 -outline, buttonWidth + outline * 2, buttonHeight +outline*2, 10,
                smoothCircle3);


        Render2D.drawRect(canvasStack, getX() + getWidth() - buttonWidth - 5, getY() + 2,
                buttonWidth, buttonHeight, 10,smoothCircle2);
        Render2D.drawCircle(canvasStack,
                getX() + getWidth() - buttonWidth + (float) (progress * 10),
                getY() + 7,
                thumbRadius,smoothCircle
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
                springPos = BURST_RADIUS;
                springVel = 0f;
                getSettingWrapper().getSetting().setValue(!getSettingWrapper().getSetting().getValue());
            }


        }
    }

    @Override
    public void mouseReleased(double p_94722_, double p_94723_, int p_94724_) {

    }
}
