package com.rae.creatingspace.content.rocket.engine;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;

public class RocketEngineBlockRenderer extends KineticBlockEntityRenderer {


	public RocketEngineBlockRenderer(Context dispatcher) {
            super(dispatcher);
        }

        @Override
        protected SuperByteBuffer getRotatedModel(KineticBlockEntity te, BlockState state) {
            return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state);
        }
}
