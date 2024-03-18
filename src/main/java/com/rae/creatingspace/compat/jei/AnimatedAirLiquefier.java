package com.rae.creatingspace.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

public class AnimatedAirLiquefier extends AnimatedKinetics {
    protected static final int SCALE = 24;

    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
        matrixStack.pushPose();
        matrixStack.translate(56, 33, 0);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-12.5f));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));

        AnimatedKinetics.defaultBlockElement(AllPartialModels.ENCASED_FAN_INNER)
                .rotateBlock(180, 0, AnimatedKinetics.getCurrentAngle() * 16)
                .scale(SCALE)
                .render(matrixStack);

        AnimatedKinetics.defaultBlockElement(BlockInit.AIR_LIQUEFIER.getDefaultState())
                .rotateBlock(0, 180, 0)
                .atLocal(0, 0, 0)
                .scale(SCALE)
                .render(matrixStack);
        matrixStack.popPose();
    }

}
