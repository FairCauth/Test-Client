package com.test.mod.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
@Getter
public class EventRender2D extends EventCancellable {
    private PoseStack poseStack;
    private float partialTicks;
    private int screenHeight;
    private int screenWidth;
    private GuiGraphics guiGraphics;

    public EventRender2D(GuiGraphics guiGraphics,PoseStack poseStack, float partialTicks, int screenHeight, int screenWidth) {
        this.poseStack = poseStack;
        this.guiGraphics = guiGraphics;
        this.partialTicks = partialTicks;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }
    public EventRender2D(GuiGraphics guiGraphics, PoseStack poseStack, int screenHeight, int screenWidth) {
        this.poseStack = poseStack;
        this.guiGraphics = guiGraphics;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

}
