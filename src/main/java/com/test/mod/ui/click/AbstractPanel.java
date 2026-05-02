package com.test.mod.ui.click;

import com.test.mod.module.Category;
import com.test.mod.ui.system.Render2D;
import com.test.mod.ui.system.font.FontManager;
import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.utils.animation.Animation;
import com.test.mod.utils.animation.Direction;
import com.test.mod.utils.animation.impl.DecelerateAnimation;
import io.github.humbleui.skija.ClipMode;
import lombok.Getter;

import java.awt.*;

public abstract class AbstractPanel {

    @Getter
    private final Category category;
    @Getter
    private float x, y, width, height;
    protected double tempX, tempY;
    protected boolean dragging = false, open = true;
    protected final Animation openingAnimation = new DecelerateAnimation(200,1);
    public AbstractPanel(Category category, float x, float y, float width, float height) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public void onRenderFirst(CanvasStack canvasStack) {
        openingAnimation.setDirection(open ? Direction.FORWARDS : Direction.BACKWARDS);
        if(dragging) {
            x = (float) tempX + ClickGuiScreen.mouseX;
            y = (float) tempY + ClickGuiScreen.mouseY;
        }

        float offsetHeight = 0;
        if(!openingAnimation.isDone() || openingAnimation.getDirection() != Direction.BACKWARDS) {
            offsetHeight += onRender(canvasStack);

        }

        float panelHeight = Math.min(offsetHeight, ClickGuiScreen.visibleHeight);
        panelHeight *= (float) openingAnimation.getOutput();
        //category title
        canvasStack.push();
        Render2D.scissorRect(canvasStack, getX(), getY() + getHeight(), getWidth(), panelHeight, ClipMode.DIFFERENCE);
        Render2D.drawRectBlur(canvasStack, getX(), getY(), getWidth(), getHeight()+ panelHeight + 4, 5,10);
        Render2D.drawRect(canvasStack, getX(), getY(), getWidth(), getHeight() + panelHeight + 4, 5
                , new Color(42, 42, 42, 194).getRGB());
        FontManager.getFont(12).drawString(canvasStack, getCategory().name(), getX() + 22, getY() + 6, Color.WHITE.getRGB());

        String icon = "1";
        switch (category) {
            case Combat->
                    icon = "7";
            case Movement->
                    icon = "3";
            case Render->
                    icon = "1";

            case Misc->
                    icon = "C";
        }
        FontManager.getIconFont(16).drawString(canvasStack, icon, getX() + 4, getY() + 6, Color.WHITE.getRGB());
        canvasStack.pop();

        //bottom bar
//        Render2D.drawRoundedBlur(canvasStack, getX(), getY() + getHeight() + panelHeight, getWidth(), 4, 0,0,5,5, 10);
//        Render2D.drawRoundedRect(canvasStack, getX(), getY() + getHeight() + panelHeight, getWidth(), 4, 0,0,5,5,
//                new Color(42, 42, 42, 194).getRGB());

        if(ClickGuiScreen.isHovered(getX(), getY(), getWidth(), getHeight() + offsetHeight)) {
            ClickGuiScreen.activeCategory = getCategory();
        }
    }
    protected void drawBackground(CanvasStack canvasStack, float visibleHeight) {
        int color = new Color(12,12,12, 145).getRGB();
        Render2D.drawRectBlur(canvasStack, x, y + height, width, visibleHeight, 0, 10);
        Render2D.drawRect(canvasStack, x, y+ height, width, visibleHeight, color);
    }
    protected abstract float onRender(CanvasStack canvasStack);
    public void mouseClicked(double p_94695_, double p_94696_, int p_94697_){
        if(ClickGuiScreen.isHovered(getX(),getY(),getWidth(),getHeight())) {

            if(p_94697_ == 0) {
               dragging = true;
                this.tempX = getX() - p_94695_;
                this.tempY = getY() - p_94696_;
            }
            if(p_94697_ == 1) {
                open = !open;
            }
        }
    }

    
    public void mouseReleased(double p_94722_, double p_94723_, int p_94724_){
        dragging = false;
    }
    
    public void mouseScrolled(double p_94686_, double p_94687_, double p_94688_){

    }

    
    public void keyPressed(int p_96552_, int p_96553_, int p_96554_) {

    }
}
