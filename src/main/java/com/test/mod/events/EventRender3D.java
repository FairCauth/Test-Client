package com.test.mod.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
@Getter
public class EventRender3D extends EventCancellable {
    private PoseStack poseStack;
    private Matrix4f matrix4f;
    private Camera camera;
    private GameRenderer gameRenderer;
    private LightTexture lightTexture;
    private float partialTicks;
    private GuiGraphics guiGraphics;
    public EventRender3D(GuiGraphics guiGraphics,PoseStack poseStack, Matrix4f matrix4f, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, float partialTicks) {
        this.poseStack = poseStack;
        this.guiGraphics =guiGraphics;
        this.matrix4f = matrix4f;
        this.camera = camera;
        this.gameRenderer = gameRenderer;
        this.lightTexture = lightTexture;
        this.partialTicks = partialTicks;
    }
}
