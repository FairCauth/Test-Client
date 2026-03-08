package com.test.mod.ui.click.panels;

import com.test.mod.Main;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.ui.click.AbstractPanel;
import com.test.mod.ui.click.ClickGuiScreen;
import com.test.mod.ui.system.Render2D;
import com.test.mod.ui.system.font.FontManager;
import com.test.mod.ui.system.utils.CanvasStack;
import io.github.humbleui.skija.ClipMode;

import java.awt.*;
import java.util.ArrayList;

public class CategoryPanel extends AbstractPanel {


    public ArrayList<ModulePanel> modulePanels = new ArrayList<>();
    public ScrollBar scrollBar = new ScrollBar();
    public CategoryPanel(Category category, float x, float y, float width, float height) {
        super(category, x, y, width, height);
        for (AbstractModule abstractModule : Main.INSTANCE.moduleManager.getModulesByCategory(category)) {
            modulePanels.add(new ModulePanel(abstractModule, category));
        }
    }

    @Override
    protected float onRender(CanvasStack canvasStack) {
        Render2D.drawRect(canvasStack, getX(), getY(), getWidth(), getHeight(), 5, new Color(33,33,33).getRGB());
        FontManager.getFont(16).drawString(canvasStack, getCategory().name(), getX() + 8, getY() + 4, Color.WHITE.getRGB());

        float moduleY = getY() + getHeight() + scrollBar.getAnimatedScroll();
        float moduleHeight = 18;
        float totalHeight = 0;

        canvasStack.push();
        Render2D.scissorRect(canvasStack, getX(),getY() + getHeight(), getWidth(), visibleHeight, ClipMode.INTERSECT);

        for (ModulePanel modulePanel : modulePanels) {
            float offsetHeight = modulePanel.onRender(canvasStack, getX(), moduleY, getWidth(), moduleHeight);
            moduleY += moduleHeight + offsetHeight;
            totalHeight += moduleHeight + offsetHeight;

        }
        canvasStack.pop();
        scrollBar.onTick(totalHeight, visibleHeight);
        scrollBar.drawScroll(canvasStack, getX() + getWidth() - 2, getY() + getHeight(), 2);
        return totalHeight;
    }

    @Override
    public void mouseClicked(double p_94695_, double p_94696_, int p_94697_) {
        super.mouseClicked(p_94695_,p_94696_,p_94697_);
        if(ClickGuiScreen.isHovered(getX(), getY() + getHeight(), getWidth(), visibleHeight)) {
            scrollBar.mouseClicked(getX() + getWidth() - 2, getY() + getHeight(), 2);

        }
        if(ClickGuiScreen.isHovered(getX(), getY() + getHeight(), getWidth() - 3, visibleHeight)) {
            if(ClickGuiScreen.isHovered(getX(), getY() + getHeight(), getWidth(), visibleHeight)) {
                for (ModulePanel modulePanel : modulePanels) {
                    modulePanel.mouseClicked(p_94695_,p_94696_,p_94697_);
                }
            }
        }

    }

    @Override
    public void mouseReleased(double p_94722_, double p_94723_, int p_94724_) {
        super.mouseReleased(p_94722_,p_94723_,p_94724_);
        scrollBar.mouseRelease();
        if(ClickGuiScreen.isHovered(getX(), getY() + getHeight(), getWidth(), visibleHeight)) {
            for (ModulePanel modulePanel : modulePanels) {
                modulePanel.mouseReleased(p_94722_,p_94723_,p_94724_);
            }
        }

    }

    @Override
    public void mouseScrolled(double p_94686_, double p_94687_, double p_94688_) {
        super.mouseScrolled(p_94686_,p_94687_,p_94688_);
        if(ClickGuiScreen.isHovered(getX(),getY() + getHeight(),getWidth(),visibleHeight)) {
            scrollBar.mouseScrolled(p_94688_);
            for (ModulePanel modulePanel : modulePanels) {
                modulePanel.mouseScrolled(p_94686_,p_94687_,p_94688_);
            }
        }

    }
}
