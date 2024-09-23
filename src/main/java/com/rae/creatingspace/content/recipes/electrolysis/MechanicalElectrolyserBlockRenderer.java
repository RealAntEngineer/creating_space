package com.rae.creatingspace.content.recipes.electrolysis;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class MechanicalElectrolyserBlockRenderer extends KineticBlockEntityRenderer<MechanicalElectrolyzerBlockEntity> {

	public MechanicalElectrolyserBlockRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(MechanicalElectrolyzerBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(MechanicalElectrolyzerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
							  int light, int overlay) {
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
		float renderedHeadOffset =
				be.getRenderedHeadOffset(partialTicks);
		ItemStack electrode = be.getElectrode();
		if (electrode != null) {
			if (!electrode.isEmpty()) {
				ms.pushPose();
				ms.translate(0, -renderedHeadOffset, 0);
				renderElectrodesFromTexture(ms,
						CreatingSpace.resource("textures/block/mechanical_electrolyzer/electrodes/" +
								electrode.getItemHolder().unwrapKey().orElseThrow().location().getPath() + ".png"), buffer);
				ms.popPose();
			}
		}

		if (Backend.canUseInstancing(be.getLevel()))
			return;
		BlockState blockState = be.getBlockState();
		SuperByteBuffer headRender = CachedBufferer.partialFacing(PartialModelInit.ELECTROLYZER_HEAD, blockState,
				blockState.getValue(HORIZONTAL_FACING));
		headRender.translate(0, -renderedHeadOffset, 0)
				.light(light)
				.renderInto(ms, buffer.getBuffer(RenderType.solid()));

	}

	@Override
	protected BlockState getRenderedBlockState(MechanicalElectrolyzerBlockEntity be) {
		return shaft(getRotationAxisOf(be));
	}

	private void renderElectrodesFromTexture(PoseStack stack, ResourceLocation texLocation, MultiBufferSource buffer) {
		ModelPart electrodes = createElectrodes();
		electrodes.render(stack, buffer.getBuffer(RenderType.entitySolid(texLocation)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
	}

	public static ModelPart createElectrodes() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create()
						.texOffs(0, 1)
						.addBox(-11.0F, 0.0F, 5.0F, 6.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 1)
						.addBox(-11.0F, 0.0F, 10.0F, 6.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(16.0F, -9.0F, 0));

		return bone.bake(16, 16);
	}
}
