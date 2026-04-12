package com.rae.creatingspace.content.recipes.electrolysis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class MechanicalElectrolyzerBlockRenderer extends KineticBlockEntityRenderer<MechanicalElectrolyzerBlockEntity> {

	public MechanicalElectrolyzerBlockRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(MechanicalElectrolyzerBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(MechanicalElectrolyzerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource,
							  int light, int overlay) {
		super.renderSafe(be, partialTicks, ms, bufferSource, light, overlay);
		float renderedHeadOffset =
				be.getRenderedHeadOffset(partialTicks);

		if (VisualizationManager.supportsVisualization(be.getLevel())) return;

		BlockState blockState = be.getBlockState();
		KineticBlockEntityRenderer.renderRotatingKineticBlock(be, getRenderedBlockState(be), ms, bufferSource.getBuffer(RenderType.solid()), light);

		SuperByteBuffer headRender = CachedBuffers.partialFacing(PartialModelInit.ELECTROLYZER_HEAD, blockState,
				blockState.getValue(HORIZONTAL_FACING));
		headRender.translate(0, -renderedHeadOffset, 0)
				.light(light)
				.renderInto(ms, bufferSource.getBuffer(RenderType.solid()));
	}

	@Override
	protected BlockState getRenderedBlockState(MechanicalElectrolyzerBlockEntity be) {
		return shaft(getRotationAxisOf(be));
	}

}
