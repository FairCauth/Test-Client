package com.test.mod.module.modules.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.test.mod.Main;
import com.test.mod.events.EventRenderLiving;
import com.test.mod.events.EventType;
import com.test.mod.events.RenderSkiaEvent;
import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.attribute.SettingAttribute;
import com.test.mod.setting.settings.ModeSetting;
import com.test.mod.transformer.transformers.ModelPartTransformer;
import com.test.mod.ui.system.utils.CanvasStack;
import com.test.mod.utils.render.ESPUtils;
import io.github.humbleui.skija.PaintMode;
import io.github.humbleui.skija.PaintStrokeCap;
import net.minecraft.client.Camera;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.*;
import java.util.List;

@ModuleInfo(name = {
        @Text(label = "Skeleton", language = Language.English),
        @Text(label = "Skeleton", language = Language.Chinese)
}, category = Category.RENDER)
public class Skeleton extends AbstractModule {
    @SettingInfo(name = {
            @Text(label = "Line style", language = Language.English),
            @Text(label = "Line style", language = Language.Chinese)
    })
    private final ModeSetting lineStyle = new ModeSetting("LINES", Arrays.asList("LINES", "DEBUG"));

    public Skeleton() {
        registerSetting(lineStyle);
    }
    @EventTarget
    public void onRenderLiving(EventRenderLiving evt) {

        if(evt.getEventType() != EventType.MIDDLE) return;
        LivingEntity livingEntity = evt.getEntity();
        if(livingEntity instanceof ArmorStand) return;

        EntityModel<?> entityModel = evt.getRenderer().getModel();

        PoseStack poseStack = evt.getPoseStack();
        int auraColor = Color.WHITE.getRGB();
        List<ESPUtils.Bone> bones = new ArrayList<>();
        Map<ModelPart, ESPUtils.BoneData> boneDataMap = new HashMap<>();

        if(entityModel instanceof HumanoidModel<?> playerModel) {
            List<ModelPart> parts = Arrays.asList(
                    playerModel.head,
                    playerModel.body,
                    playerModel.leftArm,
                    playerModel.rightArm,
                    playerModel.leftLeg,
                    playerModel.rightLeg
            );
            for(ModelPart part : parts) {
                List<ModelPart.Cube> cubes = ModelPartTransformer.getCubes(part);
                if(cubes == null) continue;
                ModelPart.Cube cube = cubes.get(0);
                boneDataMap.put(part, ESPUtils.translateBone(part, cube));
            }
            ESPUtils.BoneData head = boneDataMap.get(playerModel.head);
            ESPUtils.BoneData body = boneDataMap.get(playerModel.body);
            ESPUtils.BoneData lArm = boneDataMap.get(playerModel.leftArm);
            ESPUtils.BoneData rArm = boneDataMap.get(playerModel.rightArm);
            ESPUtils.BoneData lLeg = boneDataMap.get(playerModel.leftLeg);
            ESPUtils.BoneData rLeg = boneDataMap.get(playerModel.rightLeg);


            bones.add(new ESPUtils.Bone(head.center(), body.bottomCenter()));
            bones.add(new ESPUtils.Bone(body.bottomCenter(), body.topCenter()));

            bones.add(new ESPUtils.Bone(body.bottomCenter(), lArm.bottomCenter()));
            bones.add(new ESPUtils.Bone(lArm.bottomCenter(), lArm.topCenter()));

            bones.add(new ESPUtils.Bone(body.bottomCenter(), rArm.bottomCenter()));
            bones.add(new ESPUtils.Bone(rArm.bottomCenter(), rArm.topCenter()));

            bones.add(new ESPUtils.Bone(body.topCenter(), rLeg.bottomCenter()));
            bones.add(new ESPUtils.Bone(rLeg.bottomCenter(), rLeg.topCenter()));

            bones.add(new ESPUtils.Bone(body.topCenter(), lLeg.bottomCenter()));
            bones.add(new ESPUtils.Bone(lLeg.bottomCenter(), lLeg.topCenter()));
        }
        renderSkeleton(poseStack,evt.getMultiBufferSource(), evt.getPackedLight(),0, bones, auraColor);
        //renderSkiaLines(poseStack, bones, auraColor);

    }


    public void renderSkeleton(PoseStack poseStack, MultiBufferSource bufferSource,
                                      int packedLight, int packedOverlay, List<ESPUtils.Bone> bones, int color) {

        VertexConsumer vertexConsumer = bufferSource.getBuffer(lineStyle.getValue().equals("LINES") ? ESPUtils.LINES : ESPUtils.DEBUG_LINES);
        Matrix4f matrix = poseStack.last().pose();
        for (ESPUtils.Bone bone : bones) {
            Vec3 start = bone.start();
            Vec3 end = bone.end();
            if(lineStyle.getValue().equals("LINES")) {
                Vec3 dir = end.subtract(start).normalize();

                vertexConsumer.vertex(matrix, (float)start.x, (float)start.y, (float)start.z)
                        .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF)
                        .normal((float)dir.x, (float)dir.y, (float)dir.z)
                        .endVertex();

                vertexConsumer.vertex(matrix, (float)end.x, (float)end.y, (float)end.z)
                        .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF)
                        .normal((float)dir.x, (float)dir.y, (float)dir.z)
                        .endVertex();

            }else {

                vertexConsumer.vertex(matrix, (float) start.x, (float) start.y, (float) start.z)
                        .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF)
                        .uv(0, 0)
                        .overlayCoords(packedOverlay)
                        .uv2(packedLight)
                        .normal(0.0f, 0, 0.0f)
                        .endVertex();


                vertexConsumer.vertex(matrix, (float) end.x, (float) end.y, (float) end.z)
                        .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF)
                        .uv(0, 0)
                        .overlayCoords(packedOverlay)
                        .uv2(packedLight)
                        .normal(0.0f, 0, 0.0f)
                        .endVertex();
            }

        }
    }


}
