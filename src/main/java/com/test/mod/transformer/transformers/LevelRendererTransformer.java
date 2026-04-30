package com.test.mod.transformer.transformers;

import com.darkmagician6.eventapi.EventManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.test.mod.events.EventRender3D;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.At;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.Inject;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

@ClassTransformer(LevelRenderer.class)
public class LevelRendererTransformer implements ITransformer {
    @Inject(methodName = {"renderLevel", "m_109599_"},
            desc = "(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
            at = @At(value = "TAIL")
    )
    public void renderLevel(PoseStack p_109600_, float p_109601_, long p_109602_, boolean p_109603_, Camera p_109604_, GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_109607_) {
        EventRender3D eventRender3D = new EventRender3D(null, p_109600_, p_109607_, p_109604_, p_109605_, p_109606_, p_109601_);
        EventManager.call(eventRender3D);
    }
}
