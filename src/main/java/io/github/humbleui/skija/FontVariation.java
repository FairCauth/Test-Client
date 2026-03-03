package io.github.humbleui.skija;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@EqualsAndHashCode
public class FontVariation {
    public static final FontVariation[] EMPTY = new FontVariation[0];

    public final int _tag;
    @Getter
    public final float _value;

    public FontVariation(String feature, float value) {
        this(FourByteTag.fromString(feature), value);
    }

    public String getTag() {
        return FourByteTag.toString(_tag);
    }

    public String toString() {
        return getTag() + "=" + _value;
    }

    @ApiStatus.Internal
    public static final Pattern _splitPattern = Pattern.compile("\\s+");
    
    @ApiStatus.Internal
    public static final Pattern _variationPattern = Pattern.compile("(?<tag>[a-z0-9]{4})=(?<value>\\d+)");

    public static FontVariation parseOne(String s) {
        Matcher m = _variationPattern.matcher(s);
        if (!m.matches())
            throw new IllegalArgumentException("Can’t parse FontVariation: " + s);
        float value = Float.parseFloat(m.group("value"));
        return new FontVariation(m.group("tag"), value);
    }

    public static FontVariation[] parse(String s) {
        return _splitPattern.splitAsStream(s).map(FontVariation::parseOne).toArray(FontVariation[]::new);
    }
}
