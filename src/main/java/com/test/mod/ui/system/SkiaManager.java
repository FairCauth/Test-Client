package com.test.mod.ui.system;


import com.darkmagician6.eventapi.EventManager;
import com.test.mod.events.RenderSkiaEvent;
import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.utils.IMinecraft;

public class SkiaManager implements IMinecraft {
    public final Skia skia = new Skia();

    public SkiaManager() {
        EventManager.register(this);
    }

    public void render() {
        if (mc.player == null || mc.level == null) return;

        try {
            if (skia.context == null) {
                skia.initSkia();
                System.out.println("skia inited");
            }

            skia.checkAndUpdateSurface();
            skia.beginFrame();

            CanvasStack canvasStack = new CanvasStack(skia);
            canvasStack.push();
            try {
                float scale = (float) mc.getWindow().getGuiScale();
                skia.canvas.scale(scale, scale);
                EventManager.call(new RenderSkiaEvent(canvasStack));
            } finally {
                canvasStack.pop();
            }

            skia.endFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        skia.cleanup();
    }
}