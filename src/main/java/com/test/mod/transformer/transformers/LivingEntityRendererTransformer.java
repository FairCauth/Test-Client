package com.test.mod.transformer.transformers;

import com.darkmagician6.eventapi.EventManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.test.mod.events.EventRenderLiving;
import com.test.mod.events.EventType;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.At;
import com.test.mod.transformer.annotation.ClassTransformer;
import com.test.mod.transformer.annotation.Inject;
import com.test.mod.transformer.annotation.Local;
import com.test.mod.transformer.callback.CallbackInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.opengl.GL20;

@ClassTransformer(LivingEntityRenderer.class)
public class LivingEntityRendererTransformer implements ITransformer {
    @Inject(at = @At("HEAD"), methodName = {"render"}, desc = "(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    public void renderPre(LivingEntity entity, float p_115309_, float p_115310_, PoseStack p_115311_, MultiBufferSource p_115312_, int p_115313_,CallbackInfo callBackInfo, @Local(0) LivingEntityRenderer instance) {
        EventRenderLiving eventRenderLiving = new EventRenderLiving(EventType.PRE, entity, instance, p_115310_, p_115311_, p_115312_, p_115313_);
        EventManager.call(eventRenderLiving);
        if(eventRenderLiving.isCancelled())
            callBackInfo.cancel();


    }
    @Inject(
            at = @At(
                    value = "INVOKE",
                    target ="Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    shift = At.Shift.BEFORE
            ),
            methodName = {"render"},
            desc = "(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    public void renderPre2(LivingEntity entity, float p_115309_,
                           float p_115310_, PoseStack p_115311_, MultiBufferSource p_115312_,
                           int p_115313_,
                           @Local(16) boolean flag,
                           @Local(17) boolean flag1,
                           @Local(18) boolean flag2,
                           @Local(0) LivingEntityRenderer instance
    ) {
        EventRenderLiving eventRenderLiving = new EventRenderLiving(EventType.MIDDLE, entity, instance, p_115310_, p_115311_, p_115312_, p_115313_);
        eventRenderLiving.flag =flag;
        eventRenderLiving.flag1 = flag1;
        eventRenderLiving.flag2 = flag2;
        EventManager.call(eventRenderLiving);

    }
    @Inject(at = @At("TAIL"), methodName = {"render"}, desc = "(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    public void renderPost(LivingEntity entity, float p_115309_, float p_115310_, PoseStack p_115311_, MultiBufferSource p_115312_, int p_115313_
    ,@Local(0) LivingEntityRenderer instance) {
        EventRenderLiving eventRenderLiving = new EventRenderLiving(EventType.POST, entity, instance, p_115310_, p_115311_, p_115312_, p_115313_);
        EventManager.call(eventRenderLiving);

    }
}
