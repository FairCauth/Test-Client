package io.github.humbleui.skija.skottie;

import io.github.humbleui.skija.impl.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * <p>A Logger subclass can be used to receive
 * {@link AnimationBuilder} parsing errors and warnings.</p>
 */
public abstract class Logger extends RefCnt {
    static { Library.staticLoad(); }

    public Logger() {
        super(_nMake());
        Stats.onNativeCall();
        Stats.onNativeCall();
        _nInit(_ptr);
    }

    @ApiStatus.OverrideOnly
    public abstract void log(LogLevel level, String message, @Nullable String json);

    @ApiStatus.Internal public static native long _nMake();
    @ApiStatus.Internal public        native void _nInit(long ptr);
}