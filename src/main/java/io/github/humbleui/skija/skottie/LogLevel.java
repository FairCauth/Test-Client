package io.github.humbleui.skija.skottie;

import org.jetbrains.annotations.ApiStatus;

public enum LogLevel {
    WARNING,
    ERROR;

    @ApiStatus.Internal public static final LogLevel[] _values = values();
}