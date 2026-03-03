package com.test.mod.ui.system.utils.state;

import org.lwjgl.opengl.*;

public class GLState {
    private static final int GL_VERSION;

    private final int[] lastActiveTexture = new int[1];
    private final int[] lastProgram = new int[1];
    private final int[] lastTexture = new int[1];
    private final int[] lastSampler = new int[1];
    private final int[] lastArrayBuffer = new int[1];
    private final int[] lastVertexArrayObject = new int[1];
    private final int[] lastPolygonMode = new int[1];
    private final int[] lastViewport = new int[4];
    private final int[] lastScissorBox = new int[4];
    private final int[] lastBlendSrcRgb = new int[1];
    private final int[] lastBlendDstRgb = new int[1];
    private final int[] lastBlendSrcAlpha = new int[1];
    private final int[] lastBlendDstAlpha = new int[1];
    private final int[] lastBlendEquationRgb = new int[1];
    private final int[] lastBlendEquationAlpha = new int[1];

    private boolean lastEnableBlend;
    private boolean lastEnableCullFace;
    private boolean lastEnableDepthTest;
    private boolean lastEnableStencilTest;
    private boolean lastEnableScissorTest;
    private boolean lastEnablePrimitiveRestart;
    private boolean lastDepthMask;

    private final int[] lastPixelUnpackBufferBinding = new int[1];
    private final int[] lastPackSwapBytes = new int[1];
    private final int[] lastPackLsbFirst = new int[1];
    private final int[] lastPackRowLength = new int[1];
    private final int[] lastPackSkipPixels = new int[1];
    private final int[] lastPackSkipRows = new int[1];
    private final int[] lastPackAlignment = new int[1];
    private final int[] lastUnpackSwapBytes = new int[1];
    private final int[] lastUnpackLsbFirst = new int[1];
    private final int[] lastUnpackAlignment = new int[1];
    private final int[] lastUnpackRowLength = new int[1];
    private final int[] lastUnpackSkipPixels = new int[1];
    private final int[] lastUnpackSkipRows = new int[1];
    private final int[] lastPackImageHeight = new int[1];
    private final int[] lastPackSkipImages = new int[1];
    private final int[] lastUnpackImageHeight = new int[1];
    private final int[] lastUnpackSkipImages = new int[1];

    static {
        GL_VERSION = GL11.glGetInteger(GL30.GL_MAJOR_VERSION) * 100 + GL11.glGetInteger(GL30.GL_MINOR_VERSION) * 10;
    }

    public void push() {
        lastActiveTexture[0] = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        lastProgram[0] = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        lastTexture[0] = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        if (GL_VERSION >= 330 || GL.getCapabilities().GL_ARB_sampler_objects) {
            lastSampler[0] = GL11.glGetInteger(GL33.GL_SAMPLER_BINDING);
        }

        lastArrayBuffer[0] = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        lastVertexArrayObject[0] = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);

        if (GL_VERSION >= 200) {
            lastPolygonMode[0] = GL11.glGetInteger(GL11.GL_POLYGON_MODE);
        }

        GL11.glGetIntegerv(GL11.GL_VIEWPORT, lastViewport);
        GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, lastScissorBox);

        lastBlendSrcRgb[0] = GL11.glGetInteger(GL11.GL_BLEND_SRC);
        lastBlendDstRgb[0] = GL11.glGetInteger(GL11.GL_BLEND_DST);
        lastBlendSrcAlpha[0] = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        lastBlendDstAlpha[0] = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        lastBlendEquationRgb[0] = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
        lastBlendEquationAlpha[0] = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);

        lastEnableBlend = GL11.glIsEnabled(GL11.GL_BLEND);
        lastEnableCullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        lastEnableDepthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        lastEnableStencilTest = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
        lastEnableScissorTest = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

        if (GL_VERSION >= 310) {
            lastEnablePrimitiveRestart = GL11.glIsEnabled(GL31.GL_PRIMITIVE_RESTART);
        }

        lastDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        lastPixelUnpackBufferBinding[0] = GL11.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING);

        lastPackSwapBytes[0] = GL11.glGetInteger(GL11.GL_PACK_SWAP_BYTES);
        lastPackLsbFirst[0] = GL11.glGetInteger(GL11.GL_PACK_LSB_FIRST);
        lastPackRowLength[0] = GL11.glGetInteger(GL11.GL_PACK_ROW_LENGTH);
        lastPackSkipPixels[0] = GL11.glGetInteger(GL11.GL_PACK_SKIP_PIXELS);
        lastPackSkipRows[0] = GL11.glGetInteger(GL11.GL_PACK_SKIP_ROWS);
        lastPackAlignment[0] = GL11.glGetInteger(GL11.GL_PACK_ALIGNMENT);

        lastUnpackSwapBytes[0] = GL11.glGetInteger(GL11.GL_UNPACK_SWAP_BYTES);
        lastUnpackLsbFirst[0] = GL11.glGetInteger(GL11.GL_UNPACK_LSB_FIRST);
        lastUnpackAlignment[0] = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
        lastUnpackRowLength[0] = GL11.glGetInteger(GL11.GL_UNPACK_ROW_LENGTH);
        lastUnpackSkipPixels[0] = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_PIXELS);
        lastUnpackSkipRows[0] = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_ROWS);

        if (GL_VERSION >= 120) {
            lastPackImageHeight[0] = GL11.glGetInteger(GL12.GL_PACK_IMAGE_HEIGHT);
            lastPackSkipImages[0] = GL11.glGetInteger(GL12.GL_PACK_SKIP_IMAGES);
            lastUnpackImageHeight[0] = GL11.glGetInteger(GL12.GL_UNPACK_IMAGE_HEIGHT);
            lastUnpackSkipImages[0] = GL11.glGetInteger(GL12.GL_UNPACK_SKIP_IMAGES);
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
    }

    public void pop() {
        if (GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) != lastProgram[0]) {
            GL20.glUseProgram(lastProgram[0]);
        }

        if (GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D) != lastTexture[0]) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, lastTexture[0]);
        }

        if (GL_VERSION >= 330 || GL.getCapabilities().GL_ARB_sampler_objects) {
            GL33.glBindSampler(0, lastSampler[0]);
        }

        if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) != lastActiveTexture[0]) {
            GL13.glActiveTexture(lastActiveTexture[0]);
        }

        GL30.glBindVertexArray(lastVertexArrayObject[0]);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, lastArrayBuffer[0]);

        GL20.glBlendEquationSeparate(lastBlendEquationRgb[0], lastBlendEquationAlpha[0]);
        GL14.glBlendFuncSeparate(lastBlendSrcRgb[0], lastBlendDstRgb[0], lastBlendSrcAlpha[0], lastBlendDstAlpha[0]);

        if (lastEnableBlend) GL11.glEnable(GL11.GL_BLEND); else GL11.glDisable(GL11.GL_BLEND);
        if (lastEnableCullFace) GL11.glEnable(GL11.GL_CULL_FACE); else GL11.glDisable(GL11.GL_CULL_FACE);
        if (lastEnableDepthTest) GL11.glEnable(GL11.GL_DEPTH_TEST); else GL11.glDisable(GL11.GL_DEPTH_TEST);
        if (lastEnableStencilTest) GL11.glEnable(GL11.GL_STENCIL_TEST); else GL11.glDisable(GL11.GL_STENCIL_TEST);
        if (lastEnableScissorTest) GL11.glEnable(GL11.GL_SCISSOR_TEST); else GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (GL_VERSION >= 310) {
            if (lastEnablePrimitiveRestart) GL11.glEnable(GL31.GL_PRIMITIVE_RESTART);
            else GL11.glDisable(GL31.GL_PRIMITIVE_RESTART);
        }

        if (GL_VERSION >= 200) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, lastPolygonMode[0]);
        }

        GL11.glViewport(lastViewport[0], lastViewport[1], lastViewport[2], lastViewport[3]);
        GL11.glScissor(lastScissorBox[0], lastScissorBox[1], lastScissorBox[2], lastScissorBox[3]);

        GL11.glPixelStorei(GL11.GL_PACK_SWAP_BYTES, lastPackSwapBytes[0]);
        GL11.glPixelStorei(GL11.GL_PACK_LSB_FIRST, lastPackLsbFirst[0]);
        GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH, lastPackRowLength[0]);
        GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, lastPackSkipPixels[0]);
        GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS, lastPackSkipRows[0]);
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, lastPackAlignment[0]);

        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, lastPixelUnpackBufferBinding[0]);
        GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, lastUnpackSwapBytes[0]);
        GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, lastUnpackLsbFirst[0]);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, lastUnpackAlignment[0]);
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, lastUnpackRowLength[0]);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, lastUnpackSkipPixels[0]);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, lastUnpackSkipRows[0]);

        if (GL_VERSION >= 120) {
            GL11.glPixelStorei(GL12.GL_PACK_IMAGE_HEIGHT, lastPackImageHeight[0]);
            GL11.glPixelStorei(GL12.GL_PACK_SKIP_IMAGES, lastPackSkipImages[0]);
            GL11.glPixelStorei(GL12.GL_UNPACK_IMAGE_HEIGHT, lastUnpackImageHeight[0]);
            GL11.glPixelStorei(GL12.GL_UNPACK_SKIP_IMAGES, lastUnpackSkipImages[0]);
        }

        GL11.glDepthMask(lastDepthMask);
    }
}