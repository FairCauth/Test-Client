package io.github.humbleui.skija.paragraph;

import org.jetbrains.annotations.ApiStatus;

public enum Affinity {
    UPSTREAM,
    DOWNSTREAM;

    @ApiStatus.Internal public static final Affinity[] _values = values();
}
