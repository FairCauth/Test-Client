package com.test.mod.transformer.transformers;

import com.darkmagician6.eventapi.EventManager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.test.mod.Main;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.At;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.Inject;

@ClassTransformer(RenderSystem.class)
public class RenderSystemTransformer implements ITransformer {
    @Inject(methodName = {"flipFrame"}, desc = "(J)V", at =
        @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSwapBuffers(J)V")
    )
    public static void onBeforeSwap() {
//        Main.INSTANCE.skiaManager.render();
    }
}