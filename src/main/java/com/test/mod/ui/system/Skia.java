package com.test.mod.ui.system;

import com.test.mod.ui.system.font.FontManager;
import com.test.mod.ui.system.utils.ImageUtil;
import com.test.mod.ui.system.utils.state.GLStateStack;
import com.test.mod.utils.IMinecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.humbleui.skija.*;

public class Skia implements IMinecraft {
    public BackendRenderTarget renderTarget;
    public DirectContext context;
    public Surface surface;
    public Canvas canvas;

    private int lastWidth = -1;
    private int lastHeight = -1;
    private int lastFbId = -1;

    public void initSkia() {

        if (context == null) {
            context = DirectContext.makeGL();
        }

        createSurface();
    }

    private void createSurface() {
        if (surface != null) {
            surface.close();
        }

        if (renderTarget != null) {
            renderTarget.close();
        }

        int width = mc.getMainRenderTarget().width;
        int height = mc.getMainRenderTarget().height;
        int fbId = mc.getMainRenderTarget().frameBufferId;

        renderTarget = BackendRenderTarget.makeGL(width, height, 0, 8, fbId, FramebufferFormat.GR_GL_RGBA8);
        surface = Surface.wrapBackendRenderTarget(context, renderTarget, SurfaceOrigin.BOTTOM_LEFT, SurfaceColorFormat.RGBA_8888, ColorSpace.getSRGB());
        canvas = surface.getCanvas();

        lastWidth = width;
        lastHeight = height;
        lastFbId = fbId;
    }

    public void beginFrame() {
        GLStateStack.push();

        if (context != null) {
            context.resetGLAll();
        }
    }

    public void endFrame() {
        if (surface != null) {
            surface.flushAndSubmit();
        }

        try {
            GLStateStack.pop();
        } catch (Exception e) {
            GLStateStack.clear();
        }
    }

    public void checkAndUpdateSurface() {
        int newWidth = mc.getMainRenderTarget().width;
        int newHeight = mc.getMainRenderTarget().height;
        int newFbId = mc.getMainRenderTarget().frameBufferId;

        if (lastWidth != newWidth || lastHeight != newHeight || lastFbId != newFbId) {
            createSurface();
        }
    }

    public void reset() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        mc.getMainRenderTarget().bindWrite(true);
    }

    public void cleanup() {
        if (surface != null) {
            surface.close();
        }

        if (renderTarget != null) {
            renderTarget.close();
        }

        if (context != null) {
            context.abandon();
        }

        surface = null;
        renderTarget = null;
        context = null;
        canvas = null;

        ImageUtil.cleanup();
        FontManager.cleanup();
    }
}