package com.test.mod.ui.system.font.base;

import io.github.humbleui.skija.Font;
import lombok.experimental.UtilityClass;
import net.minecraft.ChatFormatting;

import java.util.*;
import java.util.regex.Pattern;

@UtilityClass
public class ColorProcessor {
    private final Pattern colorPattern = Pattern.compile("§.");

    private final Map<String, List<TextPart>> parseCache = Collections.synchronizedMap(new LinkedHashMap<>(128, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, List<TextPart>> eldest) {
            return size() > 829;
        }
    });

    public List<TextPart> parse(String text, int defaultColor, Font font) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        String cacheKey = text + defaultColor + (font != null ? font.getSize() : "");
        List<TextPart> cached = parseCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<TextPart> parts = new ArrayList<>();
        int alpha = (defaultColor >> 24) & 0xFF;
        int currentColor = defaultColor;

        String[] segments = text.split("(?=§)");
        for (String segment : segments) {
            if (segment.startsWith("§") && segment.length() >= 2) {
                ChatFormatting formatting = ChatFormatting.getByCode(segment.charAt(1));
                if (formatting != null) {
                    if (formatting == ChatFormatting.RESET) {
                        currentColor = defaultColor;
                    } else if (formatting.getColor() != null) {
                        currentColor = (alpha << 24) | formatting.getColor();
                    }
                    segment = segment.substring(2);
                }
            }
            if (!segment.isEmpty()) {
                float width = font != null ? font.measureTextWidth(segment) : 0;
                parts.add(new TextPart(segment, currentColor, width));
            }
        }

        List<TextPart> result = List.copyOf(parts);
        parseCache.put(cacheKey, result);
        return result;
    }

    public String strip(String text) {
        return text == null ? "" : colorPattern.matcher(text).replaceAll("");
    }

    public record TextPart(String content, int color, float width) {}
}