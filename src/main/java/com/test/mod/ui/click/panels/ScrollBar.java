package com.test.mod.ui.click.panels;

import com.test.mod.ui.click.ClickGuiScreen;
import com.test.mod.ui.system.Render2D;
import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.utils.animation.Animation;
import com.test.mod.utils.animation.Direction;
import com.test.mod.utils.animation.impl.DecelerateAnimation;
import lombok.Getter;

import java.awt.*;

public class ScrollBar {
    @Getter
    private float scrollOffset = 0, maxScroll = 0, animatedScroll = 0;
    private final Animation scrollAnimation = new DecelerateAnimation(200, 1);
    private float visibleHeight, totalHeight;

    private boolean dragging = false;
    private float dragStartY = 0;
    private float dragStartOffset = 0;
    public void onTick(float totalHeight, float visibleHeight) {
        this.totalHeight = totalHeight;
        this.visibleHeight = visibleHeight;
        scrollOffset = Math.min(0, scrollOffset);
        scrollOffset = Math.max(-maxScroll, scrollOffset);

        if(scrollAnimation.finished(Direction.FORWARDS)) {
            animatedScroll = scrollOffset;
        } else {
            float progress = (float) scrollAnimation.getOutput();
            animatedScroll += (scrollOffset - animatedScroll) * progress;
        }

        maxScroll = Math.max(0, totalHeight - visibleHeight);

    }
    public boolean canScroll () { return maxScroll > 0; }
    public void mouseClicked(float x, float y, int w) {
        if(!canScroll()) return;
        float scrollBarHeight= (visibleHeight * visibleHeight) / totalHeight;
        scrollBarHeight = Math.max(20, scrollBarHeight);
        float scrollRange = visibleHeight - scrollBarHeight;
        float scrollProgress = -animatedScroll / maxScroll;
        float scrollbarPos = y + scrollProgress * scrollRange;

        if(ClickGuiScreen.isHovered(x, y, w, scrollBarHeight)) {
            dragging = true;
            dragStartY = ClickGuiScreen.mouseY;
            dragStartOffset = scrollOffset;
        }
    }
    public void mouseDragging() {
        if(!dragging) return;
        float scrollBarHeight= (visibleHeight * visibleHeight) / totalHeight;
        scrollBarHeight = Math.max(20, scrollBarHeight);
        float scrollRange = visibleHeight - scrollBarHeight;

        float deltaY = ClickGuiScreen.mouseY - dragStartY;
        float newProgress = deltaY / scrollRange;
        scrollOffset = dragStartOffset - newProgress * maxScroll;

        scrollOffset = Math.min(0, scrollOffset);
        scrollOffset = Math.max(-maxScroll, scrollOffset);

    }
    public void drawScroll(CanvasStack canvasStack, float x, float y, float w) {
        if(!canScroll()) return;
        mouseDragging();
        float scrollBarHeight= (visibleHeight * visibleHeight) / totalHeight;
        scrollBarHeight = Math.max(20, scrollBarHeight);
        float scrollRange = visibleHeight - scrollBarHeight;
        float scrollProgress = -animatedScroll / maxScroll;
        float scrollbarPos = y + scrollProgress * scrollRange;

        Render2D.drawRect(canvasStack, x, y, w, visibleHeight, 4, new Color(50,50,50,120).getRGB());
        Render2D.drawRect(canvasStack, x, scrollbarPos, w, scrollBarHeight - 2, 4, new Color(200,200,200,180).getRGB());

    }
    public void mouseRelease() {
        dragging = false;
    }
    public void mouseScrolled(double delta) {
        if(!canScroll()) return;
        scrollOffset += delta * 22;
        scrollOffset = Math.min(0, scrollOffset);
        scrollOffset = Math.max(-maxScroll, scrollOffset);

        scrollAnimation.setDirection(Direction.FORWARDS);
        scrollAnimation.reset();
    }



}
