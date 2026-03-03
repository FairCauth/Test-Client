package io.github.humbleui.skija.shaper;

import lombok.Data;
import org.jetbrains.annotations.ApiStatus;

@Data
public class BidiRun {
    @ApiStatus.Internal public final int _end;

    /** The unicode bidi embedding level (even ltr, odd rtl) */
    @ApiStatus.Internal public final int _level;
}
