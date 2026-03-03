package com.test.mod.transformer.transformers;

import com.darkmagician6.eventapi.EventManager;
import com.test.mod.events.EventKey;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.At;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.Inject;
import net.minecraft.client.KeyboardHandler;
import org.lwjgl.glfw.GLFW;

@ClassTransformer(KeyboardHandler.class)
public class KeyboardHandlerTransformer implements ITransformer {
    @Inject(methodName = "keyPress", desc = "(JIIII)V", at = @At("HEAD"))
    public static void onKeyPress(KeyboardHandler self, long window, int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            EventManager.call(new EventKey(key));
        }
    }
}
