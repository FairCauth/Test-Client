package com.test.mod.module.modules.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.test.mod.events.EventRenderLiving;
import com.test.mod.events.EventType;
import com.test.mod.language.Language;
import com.test.mod.language.Text;
import com.test.mod.module.AbstractModule;
import com.test.mod.module.Category;
import com.test.mod.module.annotation.ModuleInfo;
import com.test.mod.setting.annotation.SettingInfo;
import com.test.mod.setting.settings.ModeSetting;
import com.test.mod.transformer.transformers.model.CreeperModelTransformer;
import com.test.mod.transformer.transformers.model.ModelPartTransformer;
import com.test.mod.transformer.transformers.model.SpiderModelTransformer;
import com.test.mod.utils.render.ESPUtils;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.*;
import java.util.List;

@ModuleInfo(name = {
        @Text(label = "Skeleton", language = Language.English),
        @Text(label = "Skeleton", language = Language.Chinese)
}, category = Category.RENDER)
public class Skeleton extends AbstractModule {
    @SettingInfo(name = {
            @Text(label = "Mode", language = Language.English),
            @Text(label = "Mode", language = Language.Chinese)
    })
    private final ModeSetting mode = new ModeSetting("Skeleton", Arrays.asList("Skeleton", "Wireframe"));

    @SettingInfo(name = {
            @Text(label = "Line style", language = Language.English),
            @Text(label = "Line style", language = Language.Chinese)
    })
    private final ModeSetting lineStyle = new ModeSetting("DEBUG", Arrays.asList("LINES", "DEBUG"));

    public Skeleton() {
        registerSetting(mode, lineStyle);
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
                boneDataMap.put(part, ESPUtils.translateBone(poseStack, part, cube));
            }
            ESPUtils.BoneData head = boneDataMap.get(playerModel.head);
            ESPUtils.BoneData body = boneDataMap.get(playerModel.body);
            ESPUtils.BoneData lArm = boneDataMap.get(playerModel.leftArm);
            ESPUtils.BoneData rArm = boneDataMap.get(playerModel.rightArm);
            ESPUtils.BoneData lLeg = boneDataMap.get(playerModel.leftLeg);
            ESPUtils.BoneData rLeg = boneDataMap.get(playerModel.rightLeg);

            if(mode.getValue().equals("Skeleton")) {
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

        }


        if(entityModel instanceof CreeperModel<?> creeperModel) {
            ModelPart root = CreeperModelTransformer.getRoot(creeperModel);
            List<ModelPart> parts = new ArrayList<>(root.getAllParts().toList());

            for(ModelPart part : parts) {
                List<ModelPart.Cube> cubes = ModelPartTransformer.getCubes(part);
                if(cubes == null) continue;
                if(cubes.size() == 0)
                    continue;
                ModelPart.Cube cube = cubes.get(0);
                boneDataMap.put(part, ESPUtils.translateBone(poseStack, part, cube));
            }

        }


        if(entityModel instanceof SpiderModel<?> spiderModel) {
            ModelPart root = SpiderModelTransformer.getRoot(spiderModel);
            List<ModelPart> parts = new ArrayList<>(root.getAllParts().toList());

            for(ModelPart part : parts) {
                List<ModelPart.Cube> cubes = ModelPartTransformer.getCubes(part);
                if(cubes == null) continue;
                if(cubes.size() == 0)
                    continue;
                ModelPart.Cube cube = cubes.get(0);
                boneDataMap.put(part, ESPUtils.translateBone(poseStack, part, cube));
            }
            //12
            ESPUtils.BoneData a = boneDataMap.get(parts.get(1));//head
            ESPUtils.BoneData b = boneDataMap.get(parts.get(2));
            ESPUtils.BoneData c = boneDataMap.get(parts.get(3));
            ESPUtils.BoneData d = boneDataMap.get(parts.get(4));
            ESPUtils.BoneData e = boneDataMap.get(parts.get(7));
            ESPUtils.BoneData f = boneDataMap.get(parts.get(8));
            ESPUtils.BoneData g = boneDataMap.get(parts.get(9));
            ESPUtils.BoneData h = boneDataMap.get(parts.get(10));
            ESPUtils.BoneData i = boneDataMap.get(parts.get(11));

            ESPUtils.BoneData k = boneDataMap.get(parts.get(5));
            ESPUtils.BoneData l = boneDataMap.get(parts.get(6));

            if(mode.getValue().equals("Skeleton")) {
                bones.add(new ESPUtils.Bone(a.center(), a.backCenter()));
                bones.add(new ESPUtils.Bone(k.frontCenter(), k.backCenter()));
                bones.add(new ESPUtils.Bone(l.frontCenter(), l.backCenter()));

                bones.add(new ESPUtils.Bone(k.frontCenter(), l.backCenter()));

                bones.add(new ESPUtils.Bone(b.leftCenter(), b.rightCenter()));
                bones.add(new ESPUtils.Bone(c.leftCenter(), c.rightCenter()));
                bones.add(new ESPUtils.Bone(d.leftCenter(), d.rightCenter()));
                bones.add(new ESPUtils.Bone(e.leftCenter(), e.rightCenter()));
                bones.add(new ESPUtils.Bone(f.leftCenter(), f.rightCenter()));
                bones.add(new ESPUtils.Bone(g.leftCenter(), g.rightCenter()));
                bones.add(new ESPUtils.Bone(h.leftCenter(), h.rightCenter()));
                bones.add(new ESPUtils.Bone(i.leftCenter(), i.rightCenter()));
            }
        }

        if(mode.getValue().equals("Wireframe")) {
            for (ModelPart part : boneDataMap.keySet()) {
                ESPUtils.BoneData bd = boneDataMap.get(part);
                bones.add(new ESPUtils.Bone(bd.v0(), bd.v1()));
                bones.add(new ESPUtils.Bone(bd.v1(), bd.v2()));
                bones.add(new ESPUtils.Bone(bd.v2(), bd.v3()));
                bones.add(new ESPUtils.Bone(bd.v3(), bd.v0()));
                bones.add(new ESPUtils.Bone(bd.v4(), bd.v5()));
                bones.add(new ESPUtils.Bone(bd.v5(), bd.v6()));
                bones.add(new ESPUtils.Bone(bd.v6(), bd.v7()));
                bones.add(new ESPUtils.Bone(bd.v7(), bd.v4()));
                bones.add(new ESPUtils.Bone(bd.v0(), bd.v4()));
                bones.add(new ESPUtils.Bone(bd.v1(), bd.v5()));
                bones.add(new ESPUtils.Bone(bd.v2(), bd.v6()));
                bones.add(new ESPUtils.Bone(bd.v3(), bd.v7()));
            }
        }
        renderSkeleton(poseStack,evt.getMultiBufferSource(), evt.getPackedLight(),0, bones, auraColor);
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
