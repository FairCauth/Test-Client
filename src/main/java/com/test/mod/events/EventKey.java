package com.test.mod.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Getter;

@Getter
public class EventKey extends EventCancellable {
    private final int key;

    public EventKey(int key) {
        this.key = key;
    }
}
