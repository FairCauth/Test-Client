package com.test.mod.ui.click.panels;

import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.setting.Setting;
import com.test.mod.setting.SettingManager;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.setting.settings.NumberSetting;
import com.test.mod.ui.click.ClickGuiScreen;
import com.test.mod.ui.click.panels.settings.AbsSettingPanel;
import com.test.mod.ui.click.panels.settings.BooleanSettingPanel;
import com.test.mod.ui.click.panels.settings.NumberSettingPanel;
import com.test.mod.ui.click.panels.settings.SettingWrapper;
import com.test.mod.ui.system.Render2D;
import com.test.mod.ui.system.font.FontManager;
import com.test.mod.ui.system.utils.CanvasStack;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;

public class ModulePanel {
    @Getter
    private final AbstractModule module;

    @Getter
    private final Category category;
    @Getter
    private float x, y, width, height;
    private boolean settingPanelOpened = false;
    private ArrayList<AbsSettingPanel<?>> settingPanels = new ArrayList<>();

    public ModulePanel(AbstractModule module, Category category) {
        this.module = module;
        this.category = category;
        for (Setting<?> setting : SettingManager.getSettings(module)) {
            if(setting instanceof BooleanSetting) {
                settingPanels.add(new BooleanSettingPanel(new SettingWrapper<>(((BooleanSetting) setting))));
            } else if (setting instanceof NumberSetting) {
                settingPanels.add(new NumberSettingPanel(new SettingWrapper<>(((NumberSetting) setting))));
            }
        }
    }
    public float onRender(CanvasStack canvasStack, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        int enabledColor = Color.BLUE.getRGB();
        Render2D.drawRect(canvasStack,x,y,width,height,0, module.isEnable() ? enabledColor : new Color(44,44,44).getRGB());
        FontManager.getFont(10).drawString(canvasStack,module.getName(), x + 3,y + 2, Color.WHITE.getRGB());

        float offsetHeight = 0;
        if(settingPanelOpened) {
            float settingY = getY() + getHeight();
            for (AbsSettingPanel<?> settingPanel : settingPanels) {
                float off = settingPanel.onRenderFirst(canvasStack, getX(), settingY, getWidth());
                offsetHeight += off;
                settingY += off;
            }
        }

        return offsetHeight;
    }

    public void mouseClicked(double p_94695_, double p_94696_, int p_94697_) {
        if(ClickGuiScreen.isHovered(x,y,width,height)) {
            if(p_94697_ == 0) {
                module.toggle();
            } else if (p_94697_ == 1) {

                settingPanelOpened = !settingPanelOpened;
            }
        }
        for (AbsSettingPanel<?> settingPanel : settingPanels) {
            settingPanel.mouseClicked(p_94695_,p_94696_,p_94697_);
        }
    }


    public void mouseReleased(double p_94722_, double p_94723_, int p_94724_) {
        for (AbsSettingPanel<?> settingPanel : settingPanels) {
            settingPanel.mouseReleased(p_94722_,p_94723_,p_94724_);
        }
    }


    public void mouseScrolled(double p_94686_, double p_94687_, double p_94688_) {

    }

}
