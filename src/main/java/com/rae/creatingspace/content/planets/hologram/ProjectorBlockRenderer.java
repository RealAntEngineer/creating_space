package com.rae.creatingspace.content.planets.hologram;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.content.planets.PlanetsPositionsHandler;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.system.NonnullDefault;

import java.util.Objects;


@NonnullDefault
public class ProjectorBlockRenderer extends SafeBlockEntityRenderer<ProjectorBlockEntity> {
    public ProjectorBlockRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public boolean shouldRender(ProjectorBlockEntity be, Vec3 vec3) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(ProjectorBlockEntity be) {
        return true;
    }

    @Override
    protected void renderSafe(ProjectorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        ms.pushPose();
        ms.translate(0.5,4,0.5);
        PlanetsPositionsHandler.renderForAll( (Objects.requireNonNull(be.getLevel()).getDayTime()+ partialTicks)/240,ms,bufferSource, be.getLevel().dimension().location(), true, Color.WHITE,false);
        ms.popPose();

    }
}
