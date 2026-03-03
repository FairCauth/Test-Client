package io.github.humbleui.skija.svg;

import io.github.humbleui.skija.impl.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class SVGNode extends RefCnt {
    static { Library.staticLoad(); }

    @ApiStatus.Internal
    public SVGNode(long ptr) {
        super(ptr);
    }

    @NotNull
    public SVGTag getTag() {
        try {
            Stats.onNativeCall();
            return SVGTag._values[_nGetTag(_ptr)];
        } finally {
            ReferenceUtil.reachabilityFence(this);
        }
    }

    @ApiStatus.Internal public static native int _nGetTag(long ptr);
}