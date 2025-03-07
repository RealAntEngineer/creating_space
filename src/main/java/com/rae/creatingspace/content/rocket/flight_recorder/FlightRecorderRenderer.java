package com.rae.creatingspace.content.rocket.flight_recorder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class FlightRecorderRenderer extends KineticBlockEntityRenderer<FlightRecorderBlockEntity> {

    public FlightRecorderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(FlightRecorderBlockEntity  be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel())) return;

        //super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockState state = be.getBlockState();

        Direction direction =  Direction.fromAxisAndDirection(((FlightRecorderBlock)state.getBlock()).getRotationAxis(state), Direction.AxisDirection.POSITIVE);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        ms.pushPose();
        SuperByteBuffer memoryRoll =
                CachedBuffers.partialFacing(PartialModelInit.MEMORY_ROLL, be.getBlockState(), direction.getOpposite());
        standardKineticRotationTransform(memoryRoll, be, light).renderInto(ms, vb);
        ms.popPose();
    }


}
