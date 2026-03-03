package com.test.mod.ui.system.utils;


import com.test.mod.ui.system.Skia;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.Surface;

public class CanvasStack {
    public final DirectContext context;
    public final Canvas canvas;
    public final Surface surface;

    public CanvasStack(Skia skia) {
        this.canvas = skia.canvas;
        this.context = skia.context;
        this.surface = skia.surface;
    }

    public void push() {
        canvas.save();
    }

    public void pop() {
        canvas.restore();
    }

    public void translate(float x, float y) {
        canvas.translate(x, y);
    }

    public void rotate(float radians) {
        canvas.rotate((float) Math.toDegrees(radians));
    }

    public void scale(float s) {
        canvas.scale(s, s);
    }

    public void scale(float sx, float sy) {
        canvas.scale(sx, sy);
    }
}