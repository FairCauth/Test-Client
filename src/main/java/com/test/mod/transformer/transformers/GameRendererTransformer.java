package com.test.mod.transformer.transformers;

import com.test.mod.Main;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.At;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.Inject;
import net.minecraft.client.renderer.GameRenderer;

@ClassTransformer(GameRenderer.class)
public class GameRendererTransformer implements ITransformer {
    @Inject(methodName = "render", desc = "(FJZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;F)V",
                    shift = At.Shift.AFTER))
    public static void onPostRender2D(float partialTick, long p_109095_, boolean p_109096_) {
        Main.INSTANCE.skiaManager.render();

    }
}
