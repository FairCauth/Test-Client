package com.test.mod.ui.click;

import com.test.mod.module.Category;
import com.test.mod.ui.system.utils.CanvasStack;
import lombok.Getter;

public abstract class AbstractPanel {
    public float visibleHeight = 200;
    @Getter
    private final Category category;
    @Getter
    private float x, y, width, height;
    protected double tempX, tempY;
    protected boolean dragging = false, open = true;
    public AbstractPanel(Category category, float x, float y, float width, float height) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public void onRenderFirst(CanvasStack canvasStack) {
        if(dragging) {
            x = (float) tempX + ClickGuiScreen.mouseX;
            y = (float) tempY + ClickGuiScreen.mouseY;
        }



        float offsetHeight = 0;
        offsetHeight += onRender(canvasStack);

        if(ClickGuiScreen.isHovered(getX(), getY(), getWidth(), getHeight() + offsetHeight)) {
            ClickGuiScreen.activeCategory = getCategory();
        }
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
