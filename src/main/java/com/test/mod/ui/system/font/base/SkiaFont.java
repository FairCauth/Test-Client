package com.test.mod.ui.system.font.base;

import com.test.mod.ui.system.cache.FilterCache;
import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.utils.render.ColorUtil;
import io.github.humbleui.skija.*;
import io.github.humbleui.skija.shaper.Shaper;
import lombok.Getter;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class SkiaFont {
    private final Paint textPaint = new Paint().setAntiAlias(true);
    private final Shaper textShaper = Shaper.make();
    private final Typeface fontTypeface;
    private final Font skiaFont;
    private final float fontHeight;
    private final float fontSize;

    private final Map<String, TextBlob> blobCache = new LinkedHashMap<>(256, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, TextBlob> eldest) {
            if (size() > 2000) {
                eldest.getValue().close();
                return true;
            }
            return false;
        }
    };

    private SkiaFont(Typeface typeface, float size) {
        this.fontTypeface = typeface;
        this.fontSize = size;
        this.skiaFont = new Font(typeface, size).setSubpixel(true).setEdging(FontEdging.SUBPIXEL_ANTI_ALIAS);

        FontMetrics metrics = skiaFont.getMetrics();
        this.fontHeight = metrics.getDescent() - metrics.getAscent();
    }

    public static SkiaFont create(String fileName, int size) {
        Path fontPath = Path.of("C:\\Test", "fonts", fileName);
        try {
            Data fontData = Data.makeFromFileName(fontPath.toString());
            Typeface typeface = Typeface.makeFromData(fontData);
            return new SkiaFont(typeface, (float) size);
        } catch (Exception e) {
            return new SkiaFont(Typeface.makeDefault(), (float) size);
        }
    }

    public float getWidth(String text) {
        if (text == null || text.isEmpty()) return 0;
        return skiaFont.measureTextWidth(ColorProcessor.strip(text));
    }

    public float getHeight() {
        return fontHeight;
    }

    public void drawString(CanvasStack canvasStack, String text, float x, float y, int color) {
        render(canvasStack, text, x, y, color, false, 0f, false);
    }

    public void drawShadowString(CanvasStack canvasStack, String text, float x, float y, int color, boolean shadow) {
        render(canvasStack, text, x, y, color, shadow, 0f, false);
    }

    public void drawDynamicString(CanvasStack canvasStack, String text, float x, float y, boolean shadow, float glow) {
        render(canvasStack, text, x, y, -1, shadow, glow, true);
    }

    public void drawGlowString(CanvasStack canvasStack, String text, float x, float y, int color, boolean shadow, float glow) {
        render(canvasStack, text, x, y, color, shadow, glow, false);
    }

    private void render(CanvasStack canvasStack, String text, float x, float y, int color, boolean shadow, float glowRadius, boolean dynamic) {
        if (text == null || text.isEmpty()) return;

        List<ColorProcessor.TextPart> textParts = ColorProcessor.parse(text, color, skiaFont);
        int alpha = (color >> 24 & 0xFF);
        if (alpha == 0 && (color & 0xFFFFFF) != 0) alpha = 255;
        if (alpha <= 1) return;

        canvasStack.push();

        if (glowRadius > 0) {
            textPaint.setMaskFilter(FilterCache.getMaskBlur(glowRadius));
            drawInternal(canvasStack.canvas, textParts, x, y, alpha, dynamic, true);
            textPaint.setMaskFilter(null);
        }

        if (shadow) {
            drawShadowInternal(canvasStack.canvas, textParts, x + 0.5f, y + 0.5f, ColorUtil.applyAlpha(0, (int) (alpha * 0.6f)));
        }

        drawInternal(canvasStack.canvas, textParts, x, y, alpha, dynamic, false);
        canvasStack.pop();
    }

    private void drawInternal(Canvas canvas, List<ColorProcessor.TextPart> textParts, float x, float y, int alpha, boolean isDynamic, boolean isGlow) {
        float currentX = x;
        int charOffset = 0;

        for (ColorProcessor.TextPart part : textParts) {
            String content = part.content();
            TextBlob blob = blobCache.computeIfAbsent(content, k -> textShaper.shape(k, skiaFont));
            float width = part.width();

            if (isDynamic && !isGlow && (part.color() & 0xFFFFFF) == 0xFFFFFF) {
                int c1 = java.awt.Color.WHITE.getRGB();
                int c2 = java.awt.Color.BLACK.getRGB();
                try (Shader shader = Shader.makeLinearGradient(currentX, y, currentX + width, y, new int[]{c1, c2})) {
                    textPaint.setShader(shader);
                    canvas.drawTextBlob(blob, currentX, y, textPaint);
                }
                textPaint.setShader(null);
            } else {
                int c = isGlow ? (part.color() == -1 ? java.awt.Color.WHITE.getRGB() : part.color()) : part.color();
                textPaint.setColor((alpha << 24) | (c & 0xFFFFFF));
                canvas.drawTextBlob(blob, currentX, y, textPaint);
            }

            currentX += width;
            charOffset += content.length();
        }
    }

    private void drawShadowInternal(Canvas canvas, List<ColorProcessor.TextPart> textParts, float x, float y, int color) {
        float currentX = x;
        textPaint.setColor(color);
        for (ColorProcessor.TextPart part : textParts) {
            TextBlob blob = blobCache.get(part.content());
            if (blob != null) canvas.drawTextBlob(blob, currentX, y, textPaint);
            currentX += part.width();
        }
    }

    public void close() {
        blobCache.values().forEach(TextBlob::close);
        blobCache.clear();
        skiaFont.close();
        fontTypeface.close();
        textShaper.close();
        textPaint.close();
    }
}