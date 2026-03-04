package com.test.mod.ui.click.panels.settings;

import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.ui.click.ClickGuiScreen;
import com.test.mod.ui.system.Render2D;
import com.test.mod.ui.system.font.FontManager;
import com.test.mod.ui.system.utils.CanvasStack;

import java.awt.*;

public class BooleanSettingPanel extends AbsSettingPanel<SettingWrapper<BooleanSetting>>{
    public BooleanSettingPanel(SettingWrapper<BooleanSetting> settingWrapper) {
        super(settingWrapper, 15);
    }

    @Override
    protected float onRender(CanvasStack canvasStack, float x, float y, float width) {
        FontManager.getFont(8).drawString(canvasStack, getSettingWrapper().getSetting().getName(), getX() + 5, getY() + 2, Color.WHITE.getRGB());

        float buttonWidth = 20, buttonHeight = 10;
        //int enabledColor = new Color();
        boolean enabled = getSettingWrapper().getSetting().getValue();

        //outline
        float outline = 1;
        Render2D.drawRect(canvasStack, getX() + getWidth() - buttonWidth - 5 - outline, getY() + 2 -outline, buttonWidth + outline * 2, buttonHeight +outline*2, 10,
                new Color(89,98,89,200).getRGB());

        Render2D.drawRect(canvasStack, getX() + getWidth() - buttonWidth - 5, getY() + 2, buttonWidth, buttonHeight, 10,new Color(77,77,77,200).getRGB());
        Render2D.drawCircle(canvasStack,
                getX() + getWidth() - buttonWidth + (enabled ? 10 : 0),
                getY() + 7,
                4,enabled ? Color.WHITE.getRGB() : new Color(144,144,144,200).getRGB()
        );
        return 0;
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
