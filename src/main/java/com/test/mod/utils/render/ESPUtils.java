package com.test.mod.utils.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.test.mod.utils.IMinecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.OptionalDouble;

public class ESPUtils implements IMinecraft {
    public record Bone(Vec3 start, Vec3 end) {
    }
    public record BoneData(ModelPart modelPart, Vec3 center, Vec3 frontCenter, Vec3 backCenter, Vec3 leftCenter,
                           Vec3 rightCenter, Vec3 topCenter, Vec3 bottomCenter, Vec3 v0, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, Vec3 v5, Vec3 v6, Vec3 v7) {
    }
    public static final RenderStateShard.DepthTestStateShard NO_DEPTH_TEST = new RenderStateShard.DepthTestStateShard("always", 519);
    public static final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);

    public static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", RenderSystem::disableBlend, () -> {});
    public static final RenderStateShard.ShaderStateShard POSITION_COLOR_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader);

    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_LINES_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLinesShader);
    public static RenderType createLineRenderType() {
        try {
            Class<?> lineClass = Class.forName(
                    "net.minecraft.client.renderer.RenderStateShard$LineStateShard"
            );
            Constructor<?> ctor = lineClass.getDeclaredConstructor(OptionalDouble.class);
            ctor.setAccessible(true);
            Object lineInstance = ctor.newInstance(OptionalDouble.of(3.0));

            RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setDepthTestState(NO_DEPTH_TEST);

            Method setLineState = builder.getClass()
                    .getDeclaredMethod("setLineState", lineClass);
            setLineState.setAccessible(true);
            setLineState.invoke(builder, lineInstance);

            return RenderType.create(
                    "no_depth_lines",
                    DefaultVertexFormat.POSITION_COLOR_NORMAL,
                    VertexFormat.Mode.LINES,
                    256, false, false,
                    builder.createCompositeState(false)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static RenderType LINES = createLineRenderType();
    public static RenderType DEBUG_LINES = RenderType.create(
            "no_depth_lines",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.DEBUG_LINES,
            256,
            false,false,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(NO_TRANSPARENCY)

                    .setCullState(NO_CULL)
                    .setDepthTestState(NO_DEPTH_TEST)

                    .createCompositeState(false)
    );
    public static Vec3 applyMatrixTransform(Matrix4f matrix, Vec3 vec, Vector4f tmp) {
        tmp.set((float) vec.x, (float) vec.y, (float) vec.z, 1.0f);
        matrix.transform(tmp);
        return new Vec3(tmp.x(), tmp.y(), tmp.z());
    }
    public static BoneData translateBone(PoseStack originalStack, ModelPart part, ModelPart.Cube cube) {

        Vec3 v0 = new Vec3(cube.minX, cube.minY, cube.minZ);
        Vec3 v1 = new Vec3(cube.maxX, cube.minY, cube.minZ);
        Vec3 v2 = new Vec3(cube.maxX, cube.maxY, cube.minZ);
        Vec3 v3 = new Vec3(cube.minX, cube.maxY, cube.minZ);
        Vec3 v4 = new Vec3(cube.minX, cube.minY, cube.maxZ);
        Vec3 v5 = new Vec3(cube.maxX, cube.minY, cube.maxZ);
        Vec3 v6 = new Vec3(cube.maxX, cube.maxY, cube.maxZ);
        Vec3 v7 = new Vec3(cube.minX, cube.maxY, cube.maxZ);

        Vec3 center = new Vec3((cube.minX + cube.maxX) / 2.0, (cube.minY + cube.maxY) / 2.0, (cube.minZ + cube.maxZ) / 2.0);
        Vec3 frontCenter = new Vec3((v0.x + v1.x + v2.x + v3.x) / 4, (v0.y + v1.y + v2.y + v3.y) / 4, cube.minZ);
        Vec3 backCenter = new Vec3((v4.x + v5.x + v6.x + v7.x) / 4, (v4.y + v5.y + v6.y + v7.y) / 4, cube.maxZ);
        Vec3 leftCenter = new Vec3(cube.minX, (v0.y + v3.y + v4.y + v7.y) / 4, (v0.z + v3.z + v4.z + v7.z) / 4);
        Vec3 rightCenter = new Vec3(cube.maxX, (v1.y + v2.y + v5.y + v6.y) / 4, (v1.z + v2.z + v5.z + v6.z) / 4);
        Vec3 topCenter = new Vec3((v2.x + v3.x + v6.x + v7.x) / 4, cube.maxY, (v2.z + v3.z + v6.z + v7.z) / 4);
        Vec3 bottomCenter = new Vec3((v0.x + v1.x + v4.x + v5.x) / 4, cube.minY, (v0.z + v1.z + v4.z + v5.z) / 4);

        PoseStack stack = new PoseStack();
        stack.pushPose();
        stack.scale(0.0625f, 0.0625f, 0.0625f);
        stack.translate(part.x, part.y, part.z);
        part.translateAndRotate(stack);
        Matrix4f matrix = stack.last().pose();


        Vec3[] points = {
                center, topCenter, bottomCenter, rightCenter,
                leftCenter, backCenter, frontCenter,
                v0, v1, v2, v3, v4, v5, v6, v7
        };
        Vector4f tmp = new Vector4f();
        for (int i = 0; i < points.length; i++) {
            tmp.set((float) points[i].x, (float) points[i].y, (float) points[i].z, 1.0f);
            matrix.transform(tmp);
            points[i] = new Vec3(tmp.x(), tmp.y(), tmp.z());
        }
//        center = applyMatrixTransform(matrix, center);
//        topCenter = applyMatrixTransform(matrix, topCenter);
//        bottomCenter = applyMatrixTransform(matrix, bottomCenter);
//        rightCenter = applyMatrixTransform(matrix, rightCenter);
//        leftCenter = applyMatrixTransform(matrix, leftCenter);
//        backCenter = applyMatrixTransform(matrix, backCenter);
//        frontCenter = applyMatrixTransform(matrix, frontCenter);
//        v0 = applyMatrixTransform(matrix, v0);
//        v1 = applyMatrixTransform(matrix, v1);
//        v2 = applyMatrixTransform(matrix, v2);
//        v3 = applyMatrixTransform(matrix, v3);
//        v4 = applyMatrixTransform(matrix, v4);
//        v5 = applyMatrixTransform(matrix, v5);
//        v6 = applyMatrixTransform(matrix, v6);
//        v7 = applyMatrixTransform(matrix, v7);
        stack.popPose();
        return new BoneData(part,
                points[0],  // center
                points[6],  // frontCenter
                points[5],  // backCenter
                points[4],  //leftCenter
                points[3],  //rightCenter
                points[1],  //topCenter
                points[2],  //bottomCenter
                points[7],  //v0
                points[8],  //v1
                points[9],  //v2
                points[10], //v3
                points[11], //v4
                points[12], //v5
                points[13], //v6
                points[14]  // v7
        );
//        return new BoneData(part, center, frontCenter, backCenter, leftCenter, rightCenter, topCenter, bottomCenter,
//                v0, v1, v2, v3, v4, v5, v6, v7);
    }
}
