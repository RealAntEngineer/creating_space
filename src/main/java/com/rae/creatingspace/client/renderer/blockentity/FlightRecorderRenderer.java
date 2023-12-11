package com.rae.creatingspace.client.renderer.blockentity;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.rae.creatingspace.server.blockentities.FlightRecorderBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class FlightRecorderRenderer extends KineticBlockEntityRenderer<FlightRecorderBlockEntity> {

    public FlightRecorderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(FlightRecorderBlockEntity  be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel())) return;

        //super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        Direction direction = be.getBlockState()
                .getValue(FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());


        ms.pushPose();
        SuperByteBuffer shaftHalf =
                CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), direction.getOpposite());
        standardKineticRotationTransform(shaftHalf, be, light).renderInto(ms, vb);
        ms.popPose();

        ms.pushPose();
        SuperByteBuffer memoryRoll =
                CachedBufferer.partialFacing(PartialModelInit.MEMORY_ROLL, be.getBlockState(), direction.getOpposite());
        standardKineticRotationTransform(memoryRoll, be, light).renderInto(ms, vb);
        ms.popPose();
    }


}
