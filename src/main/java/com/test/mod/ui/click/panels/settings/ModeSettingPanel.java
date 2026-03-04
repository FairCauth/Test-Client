package com.test.mod.ui.click.panels.settings;

import com.test.mod.setting.settings.ModeSetting;
import com.test.mod.setting.settings.NumberSetting;
import com.test.mod.ui.click.ClickGuiScreen;
import com.test.mod.ui.system.Render2D;
import com.test.mod.ui.system.font.FontManager;
import com.test.mod.ui.system.utils.CanvasStack;

import java.awt.*;

public class ModeSettingPanel extends AbsSettingPanel<SettingWrapper<ModeSetting>>{
    boolean expand = false;
    public ModeSettingPanel(SettingWrapper<ModeSetting> settingWrapper) {
        super(settingWrapper, 25);
    }

    @Override
    protected float onRender(CanvasStack canvasStack, float x, float y, float width) {

        FontManager.getFont(6).drawString(canvasStack, getSettingWrapper().getSetting().getName(), getX() + 5, getY() + 2, Color.WHITE.getRGB());

        //value
        Render2D.drawRect(canvasStack, getX() + 7, getY() + 10, getWidth() - 14, 15 + (expand ? getSettingWrapper().getSetting().getModes().size() * 14 : 0),4, new Color(122,122,122,160).getRGB());
        FontManager.getFont(9).drawString(canvasStack, getSettingWrapper().getSetting().getValue(), getX() + 10, getY() + 11, Color.WHITE.getRGB());

        float valuesHeight = 0;
        if(expand) {
            float valuesY = getY() + getHeight();
            for (String mode : getSettingWrapper().getSetting().getModes()) {
                Render2D.drawRect(canvasStack, getX(), valuesY, getWidth(), 14, 0, new Color(22,22,22,150).getRGB());
                FontManager.getFont(9).drawString(canvasStack,mode, getX() + 13, valuesY, Color.WHITE.getRGB());
                valuesHeight += 14;
                valuesY += 14;
            }

        }

        return valuesHeight;
    }

    @Override
    public void mouseClicked(double p_94695_, double p_94696_, int p_94697_) {
        if(ClickGuiScreen.isHovered(getX(), getY(), getWidth(), getHeight())) {
            if(p_94697_ == 0)
                expand = !expand;


        }
        if(expand) {
            float valuesY = getY() + getHeight();
            for (String mode : getSettingWrapper().getSetting().getModes()) {
                if(ClickGuiScreen.isHovered(getX(), valuesY, getWidth(), 14)) {
                    getSettingWrapper().getSetting().setValue(mode);
                    expand = false;
                }
                valuesY += 14;
            }
        }
    }

    @Override
    public void mouseReleased(double p_94722_, double p_94723_, int p_94724_) {

    }
}
