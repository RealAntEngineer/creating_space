package com.rae.creatingspace.content.planets.hologram;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.rendering.PlanetsRendering;
import com.rae.creatingspace.content.planets.PlanetsPosition;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.system.NonnullDefault;

import java.util.Objects;

import static com.rae.creatingspace.content.planets.PlanetsPosition.getSkyPos;
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
        PlanetsPosition.SkyPos sunPos = getSkyPos(CreatingSpace.resource("earth"), CreatingSpace.resource("sun"), Objects.requireNonNull(be.getLevel()).getGameTime()+ partialTicks);

        PlanetsPosition.SkyPos earthPos = getSkyPos(CreatingSpace.resource("earth"), CreatingSpace.resource("earth"), Objects.requireNonNull(be.getLevel()).getGameTime()+ partialTicks);
        PlanetsPosition.SkyPos moonPos = getSkyPos(CreatingSpace.resource("earth"), CreatingSpace.resource("moon"), Objects.requireNonNull(be.getLevel()).getGameTime()+ partialTicks);
        ms.pushPose();
        ms.translate(0.5,4,0.5);

        PlanetsRendering.renderPlanet(CreatingSpace.resource("textures/environment/sun.png"), bufferSource, ms, LightTexture.FULL_BRIGHT,3,
                sunPos.getRadius(), (float) (sunPos.getTheta()*180/Math.PI), (float) (sunPos.getPhi()*180/Math.PI),0);
        ms.popPose();
        ms.pushPose();
        ms.translate(0.5,4,0.5);

        PlanetsRendering.renderPlanet(CreatingSpace.resource("textures/environment/earth.png"), bufferSource, ms, LightTexture.FULL_BRIGHT, 1F,
                earthPos.getRadius(), (float) (earthPos.getTheta()*180/Math.PI), (float) (earthPos.getPhi()*180/Math.PI),0);
        ms.popPose();
        ms.pushPose();
        ms.translate(0.5,4,0.5);

        PlanetsRendering.renderAtmosphere(bufferSource, ms,  new Color(0.1f, 0.2f, 0.6f, 0.3f), LightTexture.FULL_BRIGHT,1.1f,
                earthPos.getRadius(), (float) (earthPos.getTheta() *180/Math.PI), (float) (earthPos.getPhi()*180/Math.PI),0);
        ms.popPose();
        ms.pushPose();
        ms.translate(0.5,4,0.5);

        PlanetsRendering.renderPlanet(CreatingSpace.resource("textures/environment/moon.png"), bufferSource, ms, LightTexture.FULL_BRIGHT,0.8f,
                moonPos.getRadius(), (float) (moonPos.getTheta()*180/Math.PI), (float) (moonPos.getPhi()*180/Math.PI),0);


        ms.popPose();
    }
}
