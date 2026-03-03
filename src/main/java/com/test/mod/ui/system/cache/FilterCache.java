package com.test.mod.ui.system.cache;

import io.github.humbleui.skija.FilterBlurMode;
import io.github.humbleui.skija.FilterTileMode;
import io.github.humbleui.skija.ImageFilter;
import io.github.humbleui.skija.MaskFilter;
import lombok.experimental.UtilityClass;

import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class FilterCache {
    private final int cacheSize = 64;

    private final Map<Float, ImageFilter> imageFilters = new LinkedHashMap<>(cacheSize, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Float, ImageFilter> eldest) {
            if (size() > cacheSize) {
                if (eldest.getValue() != null) eldest.getValue().close();
                return true;
            }
            return false;
        }
    };

    private final Map<Float, MaskFilter> maskFilters = new LinkedHashMap<>(cacheSize, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Float, MaskFilter> eldest) {
            if (size() > cacheSize) {
                if (eldest.getValue() != null) eldest.getValue().close();
                return true;
            }
            return false;
        }
    };

    public ImageFilter getImageBlur(float radius) {
        float key = (float) (Math.round(radius * 100.0) / 100.0);
        return imageFilters.computeIfAbsent(key, k -> ImageFilter.makeBlur(k, k, FilterTileMode.CLAMP));
    }

    public MaskFilter getMaskBlur(float radius) {
        float key = (float) (Math.round(radius * 100.0) / 100.0);
        return maskFilters.computeIfAbsent(key, k -> MaskFilter.makeBlur(FilterBlurMode.NORMAL, k, true));
    }

    public void clear() {
        imageFilters.values().forEach(f -> { if(f != null) f.close(); });
        imageFilters.clear();

        maskFilters.values().forEach(f -> { if(f != null) f.close(); });
        maskFilters.clear();
    }
}