package io.github.humbleui.skija.resources;

import io.github.humbleui.skija.impl.*;
import org.jetbrains.annotations.ApiStatus;

public abstract class ResourceProvider extends RefCnt {
    @ApiStatus.Internal
    public ResourceProvider(long ptr) {
        super(ptr);
    }
}