package com.rae.creatingspace.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;

public class AnimatedMechanicalElectrolyzer extends AnimatedKinetics {

    public AnimatedMechanicalElectrolyzer() {
    }

    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
        int scale = 23;

        blockElement(shaft(Axis.Z))
                .rotateBlock(0, 0, getCurrentAngle())
                .scale(scale)
                .render(matrixStack);

        blockElement(BlockInit.MECHANICAL_ELECTROLYZER.getDefaultState())
                .scale(scale)
                .render(matrixStack);

        float animation = (((Mth.cos(AnimationTickHolder.getRenderTime() / 32f) + 1)) / ((float) (2 * 16) / 10.5f));

        blockElement(PartialModelInit.ELECTROLYZER_HEAD)
                .atLocal(0, animation, 0)
                .scale(scale)
                .render(matrixStack);

        blockElement(AllBlocks.BASIN.getDefaultState())
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(matrixStack);

        matrixStack.popPose();
    }

}
