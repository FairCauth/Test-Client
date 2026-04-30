package com.test.mod.module.modules.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.test.mod.events.EventRender2D;
import com.test.mod.events.EventRender3D;
import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.settings.BooleanSetting;
import com.test.mod.utils.Utils;
import com.test.mod.utils.render.Drawing;
import com.test.mod.utils.render.Projection;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleInfo(name = {
        @Text(label = "ESP2D", language = Language.English),
        @Text(label = "ESP2D", language = Language.Chinese)
}, category = Category.RENDER)
public class ESP2D extends AbstractModule {
    public static class ESPU {
        public LivingEntity entity;
        public boolean shouldDraw = true;
        public float leftPoint;
        public float topPoint;
        public float rightPoint;
        public float bottomPoint;

        public ESPU(LivingEntity entity, float leftPoint, float topPoint, float rightPoint, float bottomPoint) {
            this.entity = entity;
            this.leftPoint = leftPoint;
            this.topPoint = topPoint;
            this.rightPoint = rightPoint;
            this.bottomPoint = bottomPoint;
        }
    }
    private final Map<Integer, ESPU> map = new HashMap<>();
    @SettingInfo(name = {
            @Text(label = "Self", language = Language.English),
            @Text(label = "Self", language = Language.Chinese)
    })
    private final BooleanSetting self = new BooleanSetting(true);
    public final Projection projection;
    public ESP2D() {
        projection = new Projection();
        registerSetting(self);
    }

    @EventTarget
    public void onDrawing2d(EventRender2D event) {
        PoseStack poseStack = event.getPoseStack();
        for (Map.Entry<Integer, ESPU> entry : map.entrySet()) {
            if (!entry.getValue().shouldDraw) continue;
            boolean draw = false;
            assert mc.level != null;
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity instanceof LivingEntity) {
                    if (entity == mc.player) {
                        if (mc.options.getCameraType().isFirstPerson()) continue;
                        if (!self.getValue()) continue;
                    }
                    if (entity.getId() == entry.getKey()) {
                        if (!Utils.isValidEntity(((LivingEntity) entity))) {
                            map.remove(entry.getKey());
                            continue;
                        }
                        draw = true;
                        break;
                    }
                }
            }

            if (draw) {
                ESPU espu = entry.getValue();
                LivingEntity livingEntity = espu.entity;
                float posX = espu.leftPoint;
                float posY = espu.topPoint;
                float endPosX = espu.rightPoint;
                float endPosY = espu.bottomPoint;
                Drawing drawing = Drawing.startDrawRect(poseStack);


                int auraColor = Color.WHITE.getRGB();
                Drawing.drawing(drawing,posX - 0.5D, posY, posX + 0.5D - 0.5D, endPosY, auraColor);
                Drawing.drawing(drawing,posX, endPosY - 0.5D, endPosX, endPosY, auraColor);
                Drawing.drawing(drawing,posX - 0.5D, posY, endPosX, posY + 0.5D, auraColor);
                Drawing.drawing(drawing,endPosX - 0.5D, posY, endPosX, endPosY, auraColor);


                Drawing.stopDrawingRect(drawing.getTessellator());

            }
        }
    }
    @EventTarget
    public void onTransform(EventRender3D event) {
        Camera camera = mc.gameRenderer.getMainCamera();
        assert mc.level != null;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity livingEntity) {

                if(entity == mc.player )
                {
                    if(mc.options.getCameraType().isFirstPerson()) continue;
                    if(!self.getValue()) continue;
                }

                if (!Utils.isValidEntity(((LivingEntity) entity))) {
                    continue;
                }
                double x = Mth.lerp(mc.getFrameTime(), entity.xOld, entity.getX());
                double y = Mth.lerp(mc.getFrameTime(), entity.yOld, entity.getY());
                double z = Mth.lerp(mc.getFrameTime(), entity.zOld, entity.getZ());
                double width = (double) entity.getBbWidth() / 1.5D;
                double height = (double) entity.getBbHeight() + (entity.isShiftKeyDown() ? -0.3D : 0.2D);

                AABB aabb = new AABB(x - width, y, z - width, x + width, y + height, z + width);
                List<Vec3> vectors = Arrays.asList(new Vec3(aabb.minX, aabb.minY, aabb.minZ), new Vec3(aabb.minX, aabb.maxY, aabb.minZ), new Vec3(aabb.maxX, aabb.minY, aabb.minZ), new Vec3(aabb.maxX, aabb.maxY, aabb.minZ), new Vec3(aabb.minX, aabb.minY, aabb.maxZ), new Vec3(aabb.minX, aabb.maxY, aabb.maxZ), new Vec3(aabb.maxX, aabb.minY, aabb.maxZ), new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ));
                Vector4d position = null;

                for (Vec3 vector : vectors) {
                    Vec3 vec3 = new Vec3(vector.x - camera.getPosition().x, vector.y - camera.getPosition().y, vector.z - camera.getPosition().z);
                    vector = projection.projectToScreen(vec3);

                    if(vector != null && vector.z >= 0.0D && vector.z < 1.0D) {
                        if (position == null) {
                            position = new Vector4d(vector.x, vector.y, vector.z, 0.0D);
                        }
                        position.x = Math.min(vector.x, position.x);
                        position.y = Math.min(vector.y, position.y);
                        position.z = Math.max(vector.x, position.z);
                        position.w = Math.max(vector.y, position.w);
                    }
                }
                boolean shouldRender = position != null;
                if(map.get(entity.getId()) != null) {
                    map.get(entity.getId()).shouldDraw = shouldRender;
                }
                if (shouldRender) {
                    float posX = (float) position.x;
                    float posY = (float) position.y;
                    float endPosX = (float) position.z;
                    float endPosY = (float) position.w;
                    if(map.get(entity.getId()) == null)
                        map.put(entity.getId(), new ESPU(livingEntity, posX, posY, endPosX, endPosY));
                    else
                    {
                        map.get(entity.getId()).entity = livingEntity;
                        map.get(entity.getId()).leftPoint = posX;
                        map.get(entity.getId()).topPoint = posY;
                        map.get(entity.getId()).rightPoint = endPosX;
                        map.get(entity.getId()).bottomPoint = endPosY;
                    }


                }
            }
        }
    }
}
