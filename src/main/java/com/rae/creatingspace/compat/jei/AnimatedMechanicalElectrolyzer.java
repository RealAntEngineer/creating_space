package com.rae.creatingspace.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class AnimatedMechanicalElectrolyzer extends AnimatedKinetics {

    public AnimatedMechanicalElectrolyzer() {
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 23;

        blockElement(shaft(Direction.Axis.Z))
                .rotateBlock(0, 0, getCurrentAngle())
                .scale(scale)
                .render(graphics);

        blockElement(BlockInit.MECHANICAL_ELECTROLYZER.getDefaultState())
                .scale(scale)
                .render(graphics);

        float animation = (((Mth.cos(AnimationTickHolder.getRenderTime() / 32f) + 1)) / ((float) (2 * 16) / 10.5f));

        blockElement(PartialModelInit.ELECTROLYZER_HEAD)
                .atLocal(0, animation, 0)
                .scale(scale)
                .render(graphics);

        blockElement(AllBlocks.BASIN.getDefaultState())
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(graphics);

        matrixStack.popPose();
    }

}
