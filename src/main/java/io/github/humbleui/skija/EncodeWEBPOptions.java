package io.github.humbleui.skija;

import lombok.With;
import org.jetbrains.annotations.ApiStatus;

@lombok.Data @With
public class EncodeWEBPOptions {
    public static final EncodeWEBPOptions DEFAULT = new EncodeWEBPOptions(EncodeWEBPCompressionMode.LOSSY, 100f);

    @ApiStatus.Internal 
    public final EncodeWEBPCompressionMode _compressionMode;

    @ApiStatus.Internal
    public final float _quality;

    @ApiStatus.Internal
    public EncodeWEBPOptions(EncodeWEBPCompressionMode compression, float quality) {
        _compressionMode = compression;
        _quality = quality;
    }
}
