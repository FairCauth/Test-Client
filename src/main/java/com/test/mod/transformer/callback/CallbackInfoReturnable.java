package com.test.mod.transformer.callback;

import lombok.Getter;

@Getter
public class CallbackInfoReturnable<R> extends CallbackInfo {
    private R returnValue;

    public CallbackInfoReturnable(String name, boolean cancellable) {
        super(name, cancellable);
    }

    public void setReturnValue(R returnValue) {
        if (!isCancellable()) {
            throw new IllegalStateException("Event is not cancellable");
        }

        this.returnValue = returnValue;
        super.cancel();
    }
}