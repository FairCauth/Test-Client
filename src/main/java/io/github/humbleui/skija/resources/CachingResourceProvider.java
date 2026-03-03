package io.github.humbleui.skija.resources;

import io.github.humbleui.skija.impl.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class CachingResourceProvider extends ResourceProvider {
    static { Library.staticLoad(); }

    @ApiStatus.Internal
    public CachingResourceProvider(long ptr) {
        super(ptr);
    }

    @NotNull @Contract("_ -> new")
    public static CachingResourceProvider make(@NotNull ResourceProvider resourceProvider) {
        assert resourceProvider != null : "Can’t CachingResourceProvider::make with resourceProvider == null";
        Stats.onNativeCall();
        return new CachingResourceProvider(_nMake(getPtr(resourceProvider)));
    }

    @ApiStatus.Internal public static native long _nMake(long resourceProviderPtr);
}