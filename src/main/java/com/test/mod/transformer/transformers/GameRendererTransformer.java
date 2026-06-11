package com.test.mod.transformer.transformers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.test.mod.Main;
import com.test.mod.asm.Type;
import com.test.mod.asm.tree.*;
import com.test.mod.module.modules.combat.Reach;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.*;

import java.util.ListIterator;

@ClassNameTransformer("net.minecraft.client.renderer.GameRenderer")
@ClassTransformer(GameRenderer.class)

//net.minecraft.class_757
public class GameRendererTransformer implements ITransformer {
//    @Inject(methodName = "render", desc = "(FJZ)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;F)V",
//                    shift = At.Shift.AFTER))
    @Inject(methodName = "render", desc = "(FJZ)V", at = @At("TAIL"))
    public static void onPostRender2D(float partialTick, long p_109095_, boolean p_109096_) {
        Main.INSTANCE.skiaManager.render();
    }

    public static double getPickRange() {
        Reach reach = ((Reach) Main.INSTANCE.moduleManager.getModule("Reach"));
        if(reach != null && reach.isEnable())
            return reach.range.getValue().doubleValue();
        return mc.gameMode.getPickRange();
    }
    public static double getEntityReach() {
        Reach reach = ((Reach) Main.INSTANCE.moduleManager.getModule("Reach"));
        if(reach != null && reach.isEnable())
            return reach.range.getValue().doubleValue();
        return mc.player.getEntityReach();
    }
    @Overwrite(methodName = "pick", desc = "(F)V")
    public void pick(float var1) {


        Entity var2 = mc.getCameraEntity();
        if (var2 != null && mc.level != null) {
            mc.getProfiler().push("pick");
            mc.crosshairPickEntity = null;
            double var3 = getPickRange();
            double var5 = getEntityReach();
            mc.hitResult = var2.pick(Math.max(var3, var5), var1, false);
            Vec3 var7 = var2.getEyePosition(var1);
            boolean var8 = false;
            boolean var9 = true;
            if (var3 > 3.0) {
                var8 = true;
            }

            double var10;
            var3 = var10 = Math.max(var3, var5);
            var10 *= var10;
            if (mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS) {
                var10 = mc.hitResult.getLocation().distanceToSqr(var7);
                double var12 = mc.player.getBlockReach();
                if (var10 > var12 * var12) {
                    Vec3 var14 = mc.hitResult.getLocation();
                    mc.hitResult = BlockHitResult.miss(var14, Direction.getNearest(var7.x, var7.y, var7.z), BlockPos.containing(var14));
                }
            }

            Vec3 var21 = var2.getViewVector(1.0F);
            Vec3 var13 = var7.add(var21.x * var3, var21.y * var3, var21.z * var3);
            float var22 = 1.0F;
            AABB var15 = var2.getBoundingBox().expandTowards(var21.scale(var3)).inflate(1.0, 1.0, 1.0);
            EntityHitResult var16 = ProjectileUtil.getEntityHitResult(var2, var7, var13, var15, GameRendererTransformer::canPick, var10);
            if (var16 != null) {
                Entity var17 = var16.getEntity();
                Vec3 var18 = var16.getLocation();
                double var19 = var7.distanceToSqr(var18);
                if (!(var19 > var10) && !(var19 > var5 * var5)) {
                    if (var19 < var10 || mc.hitResult == null) {
                        mc.hitResult = var16;
                        if (var17 instanceof LivingEntity || var17 instanceof ItemFrame) {
                            mc.crosshairPickEntity = var17;
                        }
                    }
                } else {
                    mc.hitResult = BlockHitResult.miss(var18, Direction.getNearest(var21.x, var21.y, var21.z), BlockPos.containing(var18));
                }
            }

            mc.getProfiler().pop();
        }

    }
    public static boolean canPick(Entity e) {
        return !e.isSpectator() && e.isPickable();
    }
}
