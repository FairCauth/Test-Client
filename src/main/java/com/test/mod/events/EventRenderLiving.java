package com.test.mod.events;


import com.darkmagician6.eventapi.events.callables.EventCancellable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

public class EventRenderLiving extends EventCancellable {
    private final LivingEntity entity;
    private final EventType eventType;
    private final LivingEntityRenderer renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;
    public boolean flag,flag1,flag2;
    public EventRenderLiving(EventType eventType, LivingEntity entity, LivingEntityRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        this.entity = entity;
        this.eventType = eventType;
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    public EventType getEventType() {
        return eventType;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public LivingEntityRenderer getRenderer() {
        return renderer;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    public int getPackedLight() {
        return packedLight;
    }
}
