package com.test.mod.transformer.callback;

import lombok.Getter;

@Getter
public class CallbackInfo {
    private final String id;
    private final boolean cancellable;
    private boolean cancelled;

    public CallbackInfo(String id, boolean cancellable) {
        this.id = id;
        this.cancellable = cancellable;
    }

    public void cancel() {
        if (cancellable) {
            this.cancelled = true;
        } else {
            throw new IllegalStateException("Cancel is not allowed! why cancel is called ^ ^ ?");
        }
    }
}