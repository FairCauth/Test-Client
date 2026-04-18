package com.test.mod.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
@Getter
@AllArgsConstructor
public class EventWorld extends EventCancellable {
    private final ClientLevel clientLevel;

}
