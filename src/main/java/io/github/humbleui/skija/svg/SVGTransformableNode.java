package io.github.humbleui.skija.svg;

import io.github.humbleui.skija.impl.*;
import org.jetbrains.annotations.ApiStatus;

public abstract class SVGTransformableNode extends SVGNode {
    static { Library.staticLoad(); }

    @ApiStatus.Internal
    public SVGTransformableNode(long ptr) {
        super(ptr);
    }
}