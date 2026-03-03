package com.test.mod.ui.system.utils;


import com.test.mod.utils.IMinecraft;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.humbleui.skija.*;
import io.github.humbleui.skija.Image;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

@UtilityClass
public class ImageUtil implements IMinecraft {
    private final Long2ObjectOpenHashMap<Image> textureCache = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<Image> captureCache = new Long2ObjectOpenHashMap<>();
    private RenderTarget bridgeFbo;

    public Image getSkinImage(DirectContext context, ResourceLocation skinLocation) {
        if (skinLocation == null || !RenderSystem.isOnRenderThread()) {
            return null;
        }

        AbstractTexture texture = mc.getTextureManager().getTexture(skinLocation);
        int textureId = texture.getId();
        if (textureId == -1) return null;

        return getCachedImage(context, textureId, 64, 64, SurfaceOrigin.TOP_LEFT, ColorType.RGBA_8888);
    }

    public Image getTextureImage(DirectContext context) {
        return getCachedImage(context, mc.getMainRenderTarget().getColorTextureId(), mc.getMainRenderTarget().width, mc.getMainRenderTarget().height, SurfaceOrigin.BOTTOM_LEFT, ColorType.RGB_888X);
    }

    private Image getCachedImage(DirectContext context, int textureId, int width, int height, SurfaceOrigin origin, ColorType colorType) {
        long key = ((long) textureId << 32) | (long) (width & 0xFFFF) << 16 | (height & 0xFFFF);

        Image image = textureCache.get(key);

        if (image == null || image.isClosed() || image.getWidth() != width || image.getHeight() != height) {
            if (image != null && !image.isClosed()) {
                image.close();
            }

            try {
                if (context == null) return null;

                image = Image.adoptGLTextureFrom(context, textureId, GL11.GL_TEXTURE_2D, width, height, GL11.GL_RGBA8, origin, colorType);
                textureCache.put(key, image);
            } catch (Exception e) {
                return null;
            }
        }

        return image;
    }

    public Image getCapturedImage(DirectContext context, int width, int height, String cacheKey, java.util.function.Consumer<net.minecraft.client.gui.GuiGraphics> drawAction) {
        long key = cacheKey.hashCode();
        Image existing = captureCache.get(key);

        if (existing != null && !existing.isClosed()) {
             return existing;
        }

        if (bridgeFbo == null || bridgeFbo.width != width || bridgeFbo.height != height) {
            if (bridgeFbo != null) bridgeFbo.destroyBuffers();
            bridgeFbo = new TextureTarget(width, height, true, Minecraft.ON_OSX);
        }

        bridgeFbo.clear(Minecraft.ON_OSX);
        bridgeFbo.bindWrite(true);
        System.out.println("Captured Texture ID: " + bridgeFbo.getColorTextureId());

        RenderSystem.viewport(0, 0, width, height);
        net.minecraft.client.gui.GuiGraphics graphics = new net.minecraft.client.gui.GuiGraphics(mc, mc.renderBuffers().bufferSource());

        drawAction.accept(graphics);
        graphics.flush();
        bridgeFbo.unbindWrite();
        RenderSystem.viewport(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight());
        Image newImage = Image.adoptGLTextureFrom(context, bridgeFbo.getColorTextureId(), GL11.GL_TEXTURE_2D, width, height, GL11.GL_RGBA8, SurfaceOrigin.TOP_LEFT, ColorType.RGBA_8888);
        captureCache.put(key, newImage);
        return newImage;
    }

    public void cleanup() {
        textureCache.values().forEach(img -> {
            if (img != null && !img.isClosed()) {
                img.close();
            }
        });
        textureCache.clear();
    }
}