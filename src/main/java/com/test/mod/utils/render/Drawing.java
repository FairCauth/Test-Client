package com.test.mod.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public class Drawing {
    private final BufferBuilder bufferbuilder;
    private final Tesselator tessellator;
    private final Matrix4f matrix;

    public Drawing(BufferBuilder bufferbuilder, Tesselator tessellator, Matrix4f matrix) {
        this.bufferbuilder = bufferbuilder;
        this.tessellator = tessellator;
        this.matrix = matrix;
    }

    public BufferBuilder getBufferbuilder() {
        return bufferbuilder;
    }

    public Tesselator getTessellator() {
        return tessellator;
    }

    public Matrix4f getMatrix() {
        return matrix;
    }
    public static Drawing startDrawRect(PoseStack poseStack) {
        Matrix4f matrix = poseStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        return new Drawing(bufferbuilder, tessellator, matrix);
    }
    public static void stopDrawingRect(Tesselator tessellator) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.end();
        RenderSystem.enableDepthTest();
    }
    public static void drawing(Drawing drawing, double left, double top, double right, double bottom, int color) {
        drawing.getBufferbuilder().vertex(drawing.getMatrix(), (float) left, (float) bottom, 0.0F).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF).endVertex();
        drawing.getBufferbuilder().vertex(drawing.getMatrix(), (float) right, (float) bottom, 0.0F)
                .color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF)
                .endVertex();
        drawing.getBufferbuilder().vertex(drawing.getMatrix(), (float) right, (float) top, 0.0F).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF).endVertex();
        drawing.getBufferbuilder().vertex(drawing.getMatrix(), (float) left, (float) top, 0.0F).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF).endVertex();

    }
}
