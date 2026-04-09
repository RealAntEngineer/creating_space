package com.rae.creatingspace.content.recipes.chemical_synthesis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class CatalystCarrierRenderer extends KineticBlockEntityRenderer<CatalystCarrierBlockEntity> {

    public CatalystCarrierRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull CatalystCarrierBlockEntity be) {
        return true;
    }

    @Override
    protected void renderSafe(CatalystCarrierBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, bufferSource, light, overlay);
        //if (VisualizationManager.supportsVisualization(be.getLevel())) return;

        float renderedHeadOffset =
                be.getRenderedHeadOffset(partialTicks);
        @NotNull ItemStack catalyst   = be.getCatalyst();
        BlockState         blockState = be.getBlockState();

        if (!catalyst.isEmpty()) {
            PartialModel model = ((CatalystItem)catalyst.getItem()).getModel();
            SuperByteBuffer catalystModel = CachedBuffers.partial(PartialModelInit.NICKEL_SULFATE_CATALYST, blockState);

            catalystModel.translate(0, -renderedHeadOffset-1, 0)
                    .rotateCenteredDegrees(180, Direction.Axis.X)
                    .light(light)
                    .renderInto(ms, bufferSource.getBuffer(RenderType.cutout()));
            /*
            ms.pushPose();
            ms.translate(0, -renderedHeadOffset, 0);
            renderCatalystFromTexture(ms,
                    CreatingSpace.resource("textures/block/catalyst_carrier/catalyst/" +
                            catalyst.getItemHolder().unwrapKey().orElseThrow().location().getPath() + ".png"), bufferSource);
            ms.popPose();*/
        }


        SuperByteBuffer headRender = CachedBuffers.partialFacing(PartialModelInit.CATALYST_CARRIER_HEAD, blockState,
                blockState.getValue(HORIZONTAL_FACING));
        headRender.translate(0, -renderedHeadOffset, 0)
                .light(light)
                .renderInto(ms, bufferSource.getBuffer(RenderType.solid()));
    }

    @Override
    protected BlockState getRenderedBlockState(CatalystCarrierBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }

    private void renderCatalystFromTexture(PoseStack stack, ResourceLocation texLocation, MultiBufferSource buffer) {
        ModelPart catalyst = createCatalyst();
        catalyst.render(stack, buffer.getBuffer(RenderType.entitySolid(texLocation)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
    }

    public static ModelPart createCatalyst() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create()
                        .texOffs(0, 1)
                        .addBox(-11.0F, 0.0F, 5.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(16.0F, -9.0F, 0));

        return bone.bake(16, 16);
    }
}
