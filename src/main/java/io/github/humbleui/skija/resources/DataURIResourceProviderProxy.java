package io.github.humbleui.skija.resources;

import io.github.humbleui.skija.impl.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DataURIResourceProviderProxy extends ResourceProvider {
    static { Library.staticLoad(); }

    @ApiStatus.Internal
    public DataURIResourceProviderProxy(long ptr) {
        super(ptr);
    }

    @NotNull @Contract("_ -> new")
    public static DataURIResourceProviderProxy make(@NotNull ResourceProvider resourceProvider) {
        return make(resourceProvider, false);
    }

    @NotNull @Contract("_, _ -> new")
    public static DataURIResourceProviderProxy make(@NotNull ResourceProvider resourceProvider, boolean predecode) {
        assert resourceProvider != null : "Can’t DataURIResourceProviderProxy::make with resourceProvider == null";
        Stats.onNativeCall();
        return new DataURIResourceProviderProxy(_nMake(getPtr(resourceProvider), predecode));
    }

    @ApiStatus.Internal public static native long _nMake(long resourceProviderPtr, boolean predecode);
}