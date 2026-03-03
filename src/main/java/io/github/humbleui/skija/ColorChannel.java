package io.github.humbleui.skija;

import org.jetbrains.annotations.ApiStatus;

public enum ColorChannel {
    R,
    G,
    B,
    A;

    @ApiStatus.Internal public static final ColorChannel[] _values = values();
}
