package com.rae.creatingspace.content.rocket.contraption.behaviour.movement;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rae.creatingspace.api.rendering.GeometryRendering;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class EngineMovementBehaviour implements MovementBehaviour {
    //TODO make a render type for the plume to avoid the default texture
    static RandomSource r = RandomSource.create();
    static float baseRadius = 0.45f;
    static int segments = 4; // Number of segments for the base circle
    static int N = 50;
    static float maxDistance = 10f;
    static float step = (float) 1 / N;


    @Override
    public boolean isActive(MovementContext context) {
        if ((context.contraption.entity instanceof RocketContraptionEntity rocketEntity)) {
            boolean flag = rocketEntity.isInPropulsionPhase();
            return MovementBehaviour.super.isActive(context) && flag;
        }
        return false;
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world == null || !context.world.isClientSide || context.position == null
                || context.disabled)
            return;
        if (context.state.is(BlockInit.BIG_ROCKET_ENGINE.get())) {
            baseRadius = 0.7f;
        } else {
            baseRadius = 0.5f;
        }
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (isActive(context)) {
            VertexConsumer vertexBuilder = buffer.getBuffer(RenderTypes.getGlowingTranslucent(AllSpecialTextures.BLANK.getLocation()));
            PoseStack matrixStack = matrices.getViewProjection();

            matrixStack.pushPose();
            // Translate and rotate the cone to the entity's position and orientation
            // Radius of the cone base
            Vec3 firstOffset = Vec3.atBottomCenterOf(context.localPos.below());
            matrixStack.translate(firstOffset.x, firstOffset.y, firstOffset.z);
            matrixStack.mulPose(Axis.YP.rotationDegrees(-45.10F));
            // just for debug mode
            int overlay = LightTexture.FULL_BRIGHT;
            float z = 0;
            float w = baseRadius;
            for (float t = 0; t < 1f; t += step) {
                z += d_z(t) * step;
                float prev_w = w;
                w += d_w(t, CSDimensionUtil.hasO2Atmosphere(renderWorld.getBiome(new BlockPos(new Vec3i(
                        (int) context.position.x(), (int) context.position.y(), (int) context.position.z()))))) * step;
                GeometryRendering.renderCylinder(vertexBuilder, matrixStack, new Vec3(0, -z, 0), getColorBell(t), overlay, w, prev_w, d_z(t) * step, segments, d_z(t) > 0);
                GeometryRendering.renderCylinder(vertexBuilder, matrixStack, new Vec3(0, -z, 0), getColorBell(t), overlay, w, prev_w, d_z(t) * step, segments, d_z(t) <= 0);

            }

            matrixStack.popPose();
        }

    }

    private static float d_z(float t) {
        return maxDistance;
    }

    private static float d_w(float t, boolean atmospheric) {
        return atmospheric ? -baseRadius : 2f * baseRadius;//(float) (-Math.sin(t * Math.PI / 2) * baseRadius * Math.PI / 2) : (float) ((3f) * Math.exp(1 - t));
    }

    private static Color getColorBell(float t) {
        return new Color(0x77f0b00f).mixWith(new Color(0x00ec972d), Mth.clamp(t * 1.5f, 0, 1));
    }
}