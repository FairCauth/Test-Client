package io.github.humbleui.skija.paragraph;

import org.jetbrains.annotations.ApiStatus;

public enum Alignment {
    LEFT,
    RIGHT,
    CENTER,
    JUSTIFY,
    START,
    END;

    @ApiStatus.Internal public static final Alignment[] _values = values();
}
