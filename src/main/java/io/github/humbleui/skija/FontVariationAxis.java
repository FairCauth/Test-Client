package io.github.humbleui.skija;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

@lombok.Data
@AllArgsConstructor
public class FontVariationAxis {
    @Getter(AccessLevel.NONE)
    @ApiStatus.Internal public final int _tag;
    @ApiStatus.Internal public final float _minValue;
    @ApiStatus.Internal public final float _defaultValue;
    @ApiStatus.Internal public final float _maxValue;
    @ApiStatus.Internal public final boolean _hidden;

    public String getTag() {
        return FourByteTag.toString(_tag);
    }

    public FontVariationAxis(String tag, float min, float def, float max, boolean hidden) {
        this(FourByteTag.fromString(tag), min, def, max, hidden);
    }

    public FontVariationAxis(String tag, float min, float def, float max) {
        this(FourByteTag.fromString(tag), min, def, max, false);
    }
}