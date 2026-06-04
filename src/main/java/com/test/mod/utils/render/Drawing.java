package com.test.mod.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public class Drawing {

    private final BufferBuilder bufferBuilder;
    private final Tesselator tessellator;
    private final Matrix4f matrix;

    public Drawing(BufferBuilder bufferBuilder, Tesselator tessellator, Matrix4f matrix) {
        this.bufferBuilder = bufferBuilder;
        this.tessellator = tessellator;
        this.matrix = matrix;
    }

    public BufferBuilder getBufferBuilder() {
        return bufferBuilder;
    }

    /**
     * 关键：vertex/color/endVertex 要通过 VertexConsumer 调用，
     * 不要直接用 BufferBuilder 调用。
     */
    public VertexConsumer getVertexConsumer() {
        return bufferBuilder;
    }

    public Tesselator getTessellator() {
        return tessellator;
    }

    public Matrix4f getMatrix() {
        return matrix;
    }

    public static Drawing startDrawRect(PoseStack poseStack) {
        // 建议复制一份矩阵，避免后续 PoseStack 变化影响绘制
        Matrix4f matrix = new Matrix4f(poseStack.last().pose());

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.disableDepthTest();

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        return new Drawing(bufferBuilder, tessellator, matrix);
    }

    public static void stopDrawingRect(Drawing drawing) {
        try {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawing.getTessellator().end();
        } finally {
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }
    }

    public static void drawing(Drawing drawing,
                               double left,
                               double top,
                               double right,
                               double bottom,
                               int color) {
        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;

        Matrix4f matrix = drawing.getMatrix();

        // 关键：这里必须用 VertexConsumer 类型
        VertexConsumer buffer = drawing.getVertexConsumer();

        buffer.vertex(matrix, (float) left,  (float) bottom, 0.0F)
                .color(r, g, b, a)
                .endVertex();

        buffer.vertex(matrix, (float) right, (float) bottom, 0.0F)
                .color(r, g, b, a)
                .endVertex();

        buffer.vertex(matrix, (float) right, (float) top, 0.0F)
                .color(r, g, b, a)
                .endVertex();

        buffer.vertex(matrix, (float) left,  (float) top, 0.0F)
                .color(r, g, b, a)
                .endVertex();
    }
}