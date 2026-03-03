package com.test.mod.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

import com.test.mod.ui.system.utils.CanvasStack;
import lombok.Getter;


public class RenderSkiaEvent extends EventCancellable {
    @Getter
    private final CanvasStack canvasStack;
    public RenderSkiaEvent(CanvasStack canvasStack) {
        this.canvasStack = canvasStack;
    }
}