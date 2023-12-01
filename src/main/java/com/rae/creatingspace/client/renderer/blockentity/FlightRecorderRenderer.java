package com.rae.creatingspace.client.renderer.blockentity;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rae.creatingspace.server.blockentities.AirLiquefierBlockEntity;
import com.rae.creatingspace.server.blockentities.FlightRecorderBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class FlightRecorderRenderer extends KineticBlockEntityRenderer<FlightRecorderBlockEntity> {

    public FlightRecorderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(FlightRecorderBlockEntity  be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel())) return;

        Direction direction = be.getBlockState()
                .getValue(FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float speed = be.getSpeed();
        float angle = (time * speed * 3 / 10f) % 360;
        angle = angle / 180f * (float) Math.PI;

        ms.pushPose();
        SuperByteBuffer shaftHalf =
                CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), direction.getOpposite());
        standardKineticRotationTransform(shaftHalf, be, light).renderInto(ms, vb);
        ms.popPose();

        ms.pushPose();
        SuperByteBuffer memoryRoll =
                CachedBufferer.partialFacing((AllPartialModels.ELEVATOR_COIL), be.getBlockState(), direction.getOpposite());
        kineticRotationTransform(memoryRoll, be, direction.getAxis(), angle, light).renderInto(ms, vb);
        ms.popPose();
    }

}
