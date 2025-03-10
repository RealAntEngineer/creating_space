package com.rae.creatingspace.content.fluids.meter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class FlowGaugeBlockRenderer extends SafeBlockEntityRenderer<FlowGaugeBlockEntity> {


    public FlowGaugeBlockRenderer(BlockEntityRendererProvider.Context context) {}


    @Override
    protected void renderSafe(FlowGaugeBlockEntity gaugeBlockEntity, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {

        BlockState blockState = gaugeBlockEntity.getBlockState();
        VertexConsumer vb = bufferSource.getBuffer(RenderType.solid());
        ms.pushPose();
        ms.translate(1 / 2f, 0.5, 1 / 2f);


        float dialPivot = 5.75f / 16;
        float progress = Mth.lerp(partialTicks, gaugeBlockEntity.prevDialState, gaugeBlockEntity.dialState);
        Direction direction = blockState.getValue(FlowGaugeBlock.FACING);

        ms.pushPose();

        CachedBuffers.partial(AllPartialModels.GAUGE_DIAL, blockState)
                .rotateY(((-direction.toYRot() - 90) ))
                .uncenter()
                .translate((double) -1 /16, 0, 0)
                .translate(0, dialPivot, dialPivot)
                .rotateX(-90 * progress)
                .translate(0, -dialPivot, -dialPivot)
                .light(light)
                .renderInto(ms, vb);
        ms.popPose();
        ms.popPose();

    }
}
