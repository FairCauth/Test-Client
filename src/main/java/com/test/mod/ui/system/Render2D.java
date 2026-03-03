package com.test.mod.ui.system;


import com.test.mod.ui.system.impl.BlurUtil;
import com.test.mod.ui.system.impl.GlowUtil;
import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.ui.system.utils.ImageUtil;

import com.test.mod.utils.IMinecraft;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;

import java.util.function.Consumer;

@UtilityClass
public class Render2D implements IMinecraft {
    private final Paint paint = new Paint();

    public void drawRectBlur(CanvasStack stack, float x, float y, float width, float height, float radius, float blurRadius, int alpha) {

        BlurUtil.draw(stack, x, y, width, height, radius, blurRadius, alpha);
    }

    public void drawRectBlur(CanvasStack stack, float x, float y, float width, float height, float radius, float blurRadius) {
        drawRectBlur(stack, x, y, width, height, radius,blurRadius,  255);
    }

    public void drawRectGlow(float x, float y, float width, float height, float radius,float glowRadius, int color) {

        GlowUtil.draw(x, y, width, height, radius, color, glowRadius);
    }

    public void drawRectGlow(CanvasStack stack, float x, float y, float width, float height, float radius,float glowRadius, int color) {
        GlowUtil.draw(stack, x, y, width, height, radius, glowRadius, color);
    }

    public void drawRect(CanvasStack stack, float x, float y, float width, float height, float radius, int color) {
        if (width <= 0 || height <= 0 || (color >> 24 & 0xFF) == 0) return;

        paint.reset();
        paint.setColor(color);
        paint.setAntiAlias(radius > 0);

        if (radius > 0) {
            stack.canvas.drawRRect(RRect.makeXYWH(x, y, width, height, radius), paint);
        } else {
            stack.canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
        }
    }

    public void drawGradientRect(CanvasStack stack, float x, float y, float width, float height, float radius, int startColor, int endColor, boolean vertical) {
        float endX = vertical ? x : x + width;
        float endY = vertical ? y + height : y;

        try (Shader shader = Shader.makeLinearGradient(x, y, endX, endY, new int[]{startColor, endColor}, new float[]{0, 1})) {
            paint.reset();
            paint.setShader(shader);
            paint.setAntiAlias(radius > 0);

            if (radius > 0) {
                stack.canvas.drawRRect(RRect.makeXYWH(x, y, width, height, radius), paint);
            } else {
                stack.canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
            }

            paint.setShader(null);
        }
    }

    public void drawPlayerHead(CanvasStack canvasStack, AbstractClientPlayer player, float x, float y, float width, float height, float radius, float alpha) {
        if (player == null || canvasStack.canvas == null || alpha <= 0.01f) return;

        Image fullSkin = ImageUtil.getSkinImage(canvasStack.context, player.getSkinTextureLocation());
        if (fullSkin == null) return;

        canvasStack.push();

        if (radius > 0) {
            try (Path path = new Path().addRRect(RRect.makeXYWH(x, y, width, height, radius))) {
                canvasStack.canvas.clipPath(path, ClipMode.INTERSECT, true);
            }
        }

        paint.reset();
        paint.setAntiAlias(true);
        paint.setAlpha((int) (255 * alpha));

        canvasStack.canvas.drawImageRect(fullSkin, Rect.makeXYWH(8, 8, 8, 8), Rect.makeXYWH(x, y, width, height), SamplingMode.DEFAULT, paint, true);
        canvasStack.canvas.drawImageRect(fullSkin, Rect.makeXYWH(40, 8, 8, 8), Rect.makeXYWH(x, y, width, height), SamplingMode.DEFAULT, paint, true);

        canvasStack.pop();
    }
}