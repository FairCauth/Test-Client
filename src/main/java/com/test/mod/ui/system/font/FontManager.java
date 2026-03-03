package com.test.mod.ui.system.font;


import com.test.mod.ui.system.font.base.SkiaFont;
import lombok.experimental.UtilityClass;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class FontManager {
    private final Map<String, SkiaFont> cache = new ConcurrentHashMap<>();

    public SkiaFont getFont(int size) {
        return get("misans.ttf", size);
    }

    public SkiaFont getUIFont(int size) {
        return get("ui.ttf", size);
    }

    public SkiaFont getPacificoFont(int size) {
        return get("pacifico.ttf", size);
    }

    public SkiaFont getIconFont(int size) {
        return get("icon.ttf", size);
    }

    public SkiaFont getProductFont(int size) {
        return get("product.ttf", size);
    }

    public SkiaFont getBorelFont(int size) {
        return get("borel.ttf", size);
    }

    private SkiaFont get(String path, int size) {
        String key = path + ":" + size;
        return cache.computeIfAbsent(key, k -> SkiaFont.create(path, size));
    }

    public void cleanup() {
        cache.values().forEach(SkiaFont::close);
        cache.clear();
    }
}