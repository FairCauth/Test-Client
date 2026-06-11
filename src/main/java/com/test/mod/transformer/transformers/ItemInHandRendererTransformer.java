package com.test.mod.transformer.transformers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.test.mod.Main;
import com.test.mod.module.modules.render.BlockAnimation;
import com.test.mod.transformer.ITransformer;
import com.test.mod.transformer.annotation.ClassNameTransformer;
import com.test.mod.transformer.annotation.Overwrite;
import com.test.mod.transformer.annotation.Shadow;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.joml.Quaternionf;

@ClassNameTransformer("net.minecraft.client.renderer.ItemInHandRenderer")
public abstract class ItemInHandRendererTransformer implements ITransformer {
    @Shadow("offHandItem")
    public ItemStack offHandItem;

    @Shadow(value = "renderTwoHandedMap", desc = "(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFFF)V")
    public abstract void renderTwoHandedMap(PoseStack p_109340_, MultiBufferSource p_109341_, int p_109342_, float p_109343_, float p_109344_, float p_109345_);

    @Shadow(value = "renderPlayerArm", desc = "(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFFLnet/minecraft/world/entity/HumanoidArm;)V")
    public abstract void renderPlayerArm(PoseStack p_109347_, MultiBufferSource p_109348_, int p_109349_, float p_109350_, float p_109351_, HumanoidArm p_109352_);

    @Shadow(value = "renderOneHandedMap", desc = "(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFLnet/minecraft/world/entity/HumanoidArm;FLnet/minecraft/world/item/ItemStack;)V")
    public abstract void renderOneHandedMap(PoseStack p_109354_, MultiBufferSource p_109355_, int p_109356_, float p_109357_, HumanoidArm p_109358_, float p_109359_, ItemStack p_109360_);

    @Shadow(value = "applyItemArmTransform", desc = "(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V")
    public abstract void applyItemArmTransform(PoseStack p_109383_, HumanoidArm p_109384_, float p_109385_);

    @Shadow(value = "applyBrushTransform", desc = "(Lcom/mojang/blaze3d/vertex/PoseStack;FLnet/minecraft/world/entity/HumanoidArm;Lnet/minecraft/world/item/ItemStack;F)V")
    public abstract void applyBrushTransform(PoseStack p_273513_, float p_273245_, HumanoidArm p_273726_, ItemStack p_272809_, float p_273333_);

    @Shadow(value = "applyItemArmAttackTransform", desc = "(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V")
    public abstract void applyItemArmAttackTransform(PoseStack p_109336_, HumanoidArm p_109337_, float p_109338_);

    @Shadow(value = "renderItem", desc = "(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    public abstract void renderItem(LivingEntity p_270072_, ItemStack p_270793_, ItemDisplayContext p_270837_, boolean p_270203_, PoseStack p_270974_, MultiBufferSource p_270686_, int p_270103_);

    @Shadow(value = "applyEatTransform", desc = "(Lcom/mojang/blaze3d/vertex/PoseStack;FLnet/minecraft/world/entity/HumanoidArm;Lnet/minecraft/world/item/ItemStack;)V")
    public abstract void applyEatTransform(PoseStack p_109331_, float p_109332_, HumanoidArm p_109333_, ItemStack p_109334_);


    @Overwrite(methodName = "renderArmWithItem", desc = "(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    public void renderArmWithItem$1(AbstractClientPlayer p_109372_, float p_109373_, float p_109374_, InteractionHand p_109375_, float p_109376_, ItemStack p_109377_, float p_109378_, PoseStack p_109379_, MultiBufferSource p_109380_, int p_109381_) {
        if (!p_109372_.isScoping()) {
            boolean flag = p_109375_ == InteractionHand.MAIN_HAND;
            HumanoidArm humanoidarm = flag ? p_109372_.getMainArm() : p_109372_.getMainArm().getOpposite();
            p_109379_.pushPose();
            if (p_109377_.isEmpty()) {
                if (flag && !p_109372_.isInvisible()) {
                    this.renderPlayerArm(p_109379_, p_109380_, p_109381_, p_109378_, p_109376_, humanoidarm);
                }
            } else if (p_109377_.is(Items.FILLED_MAP)) {
                if (flag && this.offHandItem.isEmpty()) {
                    this.renderTwoHandedMap(p_109379_, p_109380_, p_109381_, p_109374_, p_109378_, p_109376_);
                } else {
                    this.renderOneHandedMap(p_109379_, p_109380_, p_109381_, p_109378_, humanoidarm, p_109376_, p_109377_);
                }
            } else if (p_109377_.getItem() instanceof CrossbowItem) {
                boolean flag1 = CrossbowItem.isCharged(p_109377_);
                boolean flag2 = humanoidarm == HumanoidArm.RIGHT;
                int i = flag2 ? 1 : -1;
                if (p_109372_.isUsingItem() && p_109372_.getUseItemRemainingTicks() > 0 && p_109372_.getUsedItemHand() == p_109375_) {
                    this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                    p_109379_.translate((float) i * -0.4785682F, -0.094387F, 0.05731531F);
                    p_109379_.mulPose(Axis.XP.rotationDegrees(-11.935F));
                    p_109379_.mulPose(Axis.YP.rotationDegrees((float) i * 65.3F));
                    p_109379_.mulPose(Axis.ZP.rotationDegrees((float) i * -9.785F));
                    float f9 = (float) p_109377_.getUseDuration() - ((float) mc.player.getUseItemRemainingTicks() - p_109373_ + 1.0F);
                    float f13 = f9 / (float) CrossbowItem.getChargeDuration(p_109377_);
                    if (f13 > 1.0F) {
                        f13 = 1.0F;
                    }

                    if (f13 > 0.1F) {
                        float f16 = Mth.sin((f9 - 0.1F) * 1.3F);
                        float f3 = f13 - 0.1F;
                        float f4 = f16 * f3;
                        p_109379_.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                    }

                    p_109379_.translate(f13 * 0.0F, f13 * 0.0F, f13 * 0.04F);
                    p_109379_.scale(1.0F, 1.0F, 1.0F + f13 * 0.2F);
                    p_109379_.mulPose(Axis.YN.rotationDegrees((float) i * 45.0F));
                } else {
                    float f = -0.4F * Mth.sin(Mth.sqrt(p_109376_) * (float) Math.PI);
                    float f1 = 0.2F * Mth.sin(Mth.sqrt(p_109376_) * ((float) Math.PI * 2F));
                    float f2 = -0.2F * Mth.sin(p_109376_ * (float) Math.PI);
                    p_109379_.translate((float) i * f, f1, f2);
                    this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                    this.applyItemArmAttackTransform(p_109379_, humanoidarm, p_109376_);
                    if (flag1 && p_109376_ < 0.001F && flag) {
                        p_109379_.translate((float) i * -0.641864F, 0.0F, 0.0F);
                        p_109379_.mulPose(Axis.YP.rotationDegrees((float) i * 10.0F));
                    }
                }

                this.renderItem(p_109372_, p_109377_, flag2 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !flag2, p_109379_, p_109380_, p_109381_);
            } else {
                boolean flag3 = humanoidarm == HumanoidArm.RIGHT;
//                if (!net.minecraftforge.client.extensions.common.IClientItemExtensions.of(p_109377_).applyForgeHandTransform(p_109379_, mc.player, humanoidarm, p_109377_, p_109373_, p_109378_, p_109376_)) // FORGE: Allow items to define custom arm animation

                BlockAnimation blockAnimation = ((BlockAnimation) Main.INSTANCE.moduleManager.getModule("BlockAnimation"));
                if (blockAnimation.isEnable() && mc.options.keyUse.isDown() && (p_109377_.getItem() instanceof SwordItem)) {
                    switch (BlockAnimation.mode.getValue()) {
                        case "1.7" -> {
                            animation1_7(p_109379_, p_109378_ ,p_109376_);
                        }
                        case "Sigma" -> {
                            animationSigma(p_109379_, p_109378_ ,p_109376_);
                        }
                    }
                }
                else if (p_109372_.isUsingItem() && p_109372_.getUseItemRemainingTicks() > 0 && p_109372_.getUsedItemHand() == p_109375_) {
                    int k = flag3 ? 1 : -1;
                    switch (p_109377_.getUseAnimation()) {
                        case NONE:
                            this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                            break;
                        case EAT:
                        case DRINK:
                            this.applyEatTransform(p_109379_, p_109373_, humanoidarm, p_109377_);
                            this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                            break;
                        case BLOCK:
                            this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                            break;
                        case BOW:
                            this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                            p_109379_.translate((float) k * -0.2785682F, 0.18344387F, 0.15731531F);
                            p_109379_.mulPose(Axis.XP.rotationDegrees(-13.935F));
                            p_109379_.mulPose(Axis.YP.rotationDegrees((float) k * 35.3F));
                            p_109379_.mulPose(Axis.ZP.rotationDegrees((float) k * -9.785F));
                            float f8 = (float) p_109377_.getUseDuration() - ((float) mc.player.getUseItemRemainingTicks() - p_109373_ + 1.0F);
                            float f12 = f8 / 20.0F;
                            f12 = (f12 * f12 + f12 * 2.0F) / 3.0F;
                            if (f12 > 1.0F) {
                                f12 = 1.0F;
                            }

                            if (f12 > 0.1F) {
                                float f15 = Mth.sin((f8 - 0.1F) * 1.3F);
                                float f18 = f12 - 0.1F;
                                float f20 = f15 * f18;
                                p_109379_.translate(f20 * 0.0F, f20 * 0.004F, f20 * 0.0F);
                            }

                            p_109379_.translate(f12 * 0.0F, f12 * 0.0F, f12 * 0.04F);
                            p_109379_.scale(1.0F, 1.0F, 1.0F + f12 * 0.2F);
                            p_109379_.mulPose(Axis.YN.rotationDegrees((float) k * 45.0F));
                            break;
                        case SPEAR:
                            this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                            p_109379_.translate((float) k * -0.5F, 0.7F, 0.1F);
                            p_109379_.mulPose(Axis.XP.rotationDegrees(-55.0F));
                            p_109379_.mulPose(Axis.YP.rotationDegrees((float) k * 35.3F));
                            p_109379_.mulPose(Axis.ZP.rotationDegrees((float) k * -9.785F));
                            float f7 = (float) p_109377_.getUseDuration() - ((float) mc.player.getUseItemRemainingTicks() - p_109373_ + 1.0F);
                            float f11 = f7 / 10.0F;
                            if (f11 > 1.0F) {
                                f11 = 1.0F;
                            }

                            if (f11 > 0.1F) {
                                float f14 = Mth.sin((f7 - 0.1F) * 1.3F);
                                float f17 = f11 - 0.1F;
                                float f19 = f14 * f17;
                                p_109379_.translate(f19 * 0.0F, f19 * 0.004F, f19 * 0.0F);
                            }

                            p_109379_.translate(0.0F, 0.0F, f11 * 0.2F);
                            p_109379_.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
                            p_109379_.mulPose(Axis.YN.rotationDegrees((float) k * 45.0F));
                            break;
                        case BRUSH:
                            this.applyBrushTransform(p_109379_, p_109373_, humanoidarm, p_109377_, p_109378_);
                    }
                } else if (p_109372_.isAutoSpinAttack()) {
                    this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                    int j = flag3 ? 1 : -1;
                    p_109379_.translate((float) j * -0.4F, 0.8F, 0.3F);
                    p_109379_.mulPose(Axis.YP.rotationDegrees((float) j * 65.0F));
                    p_109379_.mulPose(Axis.ZP.rotationDegrees((float) j * -85.0F));
                } else {
                    float f5 = -0.4F * Mth.sin(Mth.sqrt(p_109376_) * (float) Math.PI);
                    float f6 = 0.2F * Mth.sin(Mth.sqrt(p_109376_) * ((float) Math.PI * 2F));
                    float f10 = -0.2F * Mth.sin(p_109376_ * (float) Math.PI);
                    int l = flag3 ? 1 : -1;
                    p_109379_.translate((float) l * f5, f6, f10);
                    this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                    this.applyItemArmAttackTransform(p_109379_, humanoidarm, p_109376_);
                }

                this.renderItem(p_109372_, p_109377_, flag3 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !flag3, p_109379_, p_109380_, p_109381_);
            }

            p_109379_.popPose();
        }


    }
    public static void animation1_7(PoseStack poseStack, float equippedProg, float swingProgress) {
        poseStack.translate(0.56F, -0.52F + equippedProg * -0.6F, -0.71999997F);
        float f = Mth.sin(swingProgress * swingProgress * (float) Math.PI);
        float f1 = Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(45.0F + f * -20.0F)));
        poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(f1 * -20.0F)));
        poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(f1 * -80.0F)));
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(-45.0F)));
        poseStack.translate(-0.2F, 0.126F, 0.2F);
        poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-102.25F)));
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(15.0F)));
        poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(80.0F)));
    }
    public static void animationSigma(PoseStack poseStack, float equippedProg, float swingProgress) {
        poseStack.translate(0.56, -0.52, -0.72);
        poseStack.translate(-0.1414214, 0.08, 0.1414214);
        poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-102.25F)));
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(7.365F)));
        poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(78.050003F)));
        double f1 = Math.sin(Math.sqrt(swingProgress) * Math.PI);
        poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians((float) (f1 * -10.0))));
        poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians((float) (f1 * 30.0))));
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians((float) (f1 * -13.0))));
    }


}
