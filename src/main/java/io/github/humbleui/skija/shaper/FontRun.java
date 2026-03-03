package io.github.humbleui.skija.shaper;

import io.github.humbleui.skija.*;
import io.github.humbleui.skija.impl.*;
import lombok.Data;
import org.jetbrains.annotations.ApiStatus;

@Data
public class FontRun {
    @ApiStatus.Internal public final int _end;
    @ApiStatus.Internal public final Font _font;

    @ApiStatus.Internal 
    public long _getFontPtr() {
        try {
            return Native.getPtr(_font);
        } finally {
            ReferenceUtil.reachabilityFence(_font);
        }
    }
}
