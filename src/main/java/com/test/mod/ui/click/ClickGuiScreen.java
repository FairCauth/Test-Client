package com.test.mod.ui.click;

import com.darkmagician6.eventapi.EventManager;

import com.darkmagician6.eventapi.EventTarget;
import com.test.mod.events.RenderSkiaEvent;
import com.test.mod.module.Category;
import com.test.mod.ui.click.panels.CategoryPanel;
import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.utils.IMinecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClickGuiScreen extends Screen implements IMinecraft {
    public static float mouseX, mouseY;
    public static Category activeCategory;
    public static float visibleHeight = 200;
    public CopyOnWriteArrayList<AbstractPanel> abstractPanels = new CopyOnWriteArrayList<>();
    public ClickGuiScreen(Component p_96550_) {
        super(p_96550_);
        EventManager.register(this);

        //初始化鼠标滚轮事件
        MousePoller.installScrollCallback();

        float categoryX = 10, categoryWidth = 120;
        for (Category category : Category.getCategories()) {
            addPanel(new CategoryPanel(category, categoryX, 10, categoryWidth, 28));
            categoryX += categoryWidth + 10;
        }
    }
    public void cleanup() {
        EventManager.unregister(this);
//        Iterator<AbstractPanel> abstractPanelIterator = abstractPanels.iterator();
//        while (abstractPanelIterator.hasNext()) {
//            abstractPanelIterator.remove();
//            abstractPanelIterator.next();
//        }
        activeCategory = null;
    }
    private void sortPanel(int a) {
        int b = abstractPanels.size() - 1;
        while (a != b) {
            Collections.swap(abstractPanels, a, b);
            b--;
        }
    }
    private void addPanel(AbstractPanel... panels) {
        abstractPanels.addAll(List.of(panels));
    }
    public static boolean isHovered(double x, double y, double width, double height) {
        return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
    }



    @EventTarget
    public void onRender(RenderSkiaEvent event) {
        if(mc.screen != this) return;
        MousePoller.update(this);

        CanvasStack canvasStack = event.getCanvasStack();
        for (AbstractPanel abstractPanel : abstractPanels) {
            abstractPanel.onRenderFirst(canvasStack);
        }
    }
//    @Override
//    public void mouseMoved(double p_94758_, double p_94759_) {
//        mouseX = ((float) p_94758_);
//        mouseY = ((float) p_94759_);
//
////        super.mouseMoved(p_94758_, p_94759_);
//    }
    @Override
    protected void init() {
        super.init();
    }


    public void handleMouseClicked(double p_94695_, double p_94696_, int p_94697_) {
        int i = 0;
        for (AbstractPanel abstractPanel : abstractPanels) {
            if(abstractPanel.getCategory().equals(activeCategory)) {
                sortPanel(i);
            }
            ++i;
        }


        for (AbstractPanel abstractPanel : abstractPanels) {
            if(abstractPanel.getCategory().equals(activeCategory))
                abstractPanel.mouseClicked(p_94695_, p_94696_, p_94697_);
        }
    }




    public void handleMouseReleased(double p_94722_, double p_94723_, int p_94724_) {
        for (AbstractPanel abstractPanel : abstractPanels) {
            abstractPanel.mouseReleased(p_94722_, p_94723_, p_94724_);
        }
    }


    public void handleMouseScrolled(double p_94686_, double p_94687_, double p_94688_) {
        for (AbstractPanel abstractPanel : abstractPanels) {
            if(abstractPanel.getCategory().equals(activeCategory))
                abstractPanel.mouseScrolled(p_94686_, p_94687_, p_94688_);
        }

    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        return super.keyPressed(p_96552_, p_96553_, p_96554_);
    }
}
