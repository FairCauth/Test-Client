package com.test.mod.ui.system.impl;

import com.test.mod.ui.system.utils.CanvasStack;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class GlowUtil {
    private final List<GlowTask> glowTasks = new ArrayList<>();
    private final Paint paint = new Paint();

    public void draw(CanvasStack stack, float x, float y, float width, float height, float radius, float glowRadius, int color) {
        if (((color >> 24) & 0xFF) <= 0 || glowRadius <= 0) return;

        try (Path path = new Path();
             MaskFilter blur = MaskFilter.makeBlur(FilterBlurMode.NORMAL, glowRadius, true)) {
            render(stack.canvas, path, x, y, width, height, radius, color, blur);
        }
    }

    public void draw(float x, float y, float width, float height, float radius, int color, float glowRadius) {
        if (((color >> 24) & 0xFF) <= 0) return;
        glowTasks.add(new GlowTask(x, y, width, height, radius, color, glowRadius));
    }

    public void flush(CanvasStack stack) {
        if (glowTasks.isEmpty()) return;

        for (GlowTask task : glowTasks) {
            draw(stack, task.x, task.y, task.width, task.height, task.radius, task.glowRadius, task.color);
        }
        glowTasks.clear();
    }

    private void render(Canvas canvas, Path path, float x, float y, float width, float height, float radius, int color, MaskFilter filter) {
        if (radius > 0) {
            path.addRRect(RRect.makeXYWH(x, y, width, height, radius));
            paint.setAntiAlias(true);
        } else {
            path.addRect(Rect.makeXYWH(x, y, width, height));
            paint.setAntiAlias(false);
        }

        paint.setMaskFilter(filter);
        paint.setColor(color);

        canvas.save();
        canvas.clipPath(path, ClipMode.DIFFERENCE, radius > 0);
        canvas.drawPath(path, paint);
        canvas.restore();

        paint.setMaskFilter(null);
    }

    @AllArgsConstructor
    private static class GlowTask {
        float x, y, width, height, radius;
        int color;
        float glowRadius;
    }
}