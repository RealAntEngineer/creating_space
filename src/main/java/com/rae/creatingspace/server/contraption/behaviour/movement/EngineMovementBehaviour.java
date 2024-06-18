package com.rae.creatingspace.server.contraption.behaviour.movement;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.rendering.GeometryRendering;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.server.particle.RocketPlumeParticleData;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EngineMovementBehaviour implements MovementBehaviour {
    //TODO make a render type for the plume to avoid the default texture
    static RandomSource r = RandomSource.create();
    static float baseRadius = 0.5f;
    static float minRadius = 0.2f;
    static float A = (baseRadius - minRadius);
    static int segments = 16; // Number of segments for the base circle
    static int N = 50;
    static float maxDistance = 5f;
    static float step = maxDistance / N;


    @Override
    public boolean isActive(MovementContext context) {
        return true;//MovementBehaviour.super.isActive(context) && (context.contraption.entity instanceof RocketContraptionEntity rocketEntity) && context.motion.length() != 0;
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world == null || !context.world.isClientSide || context.position == null
                || context.disabled)
            return;

        Level world = context.world;
        Vec3 pos = context.position;
        ParticleOptions particle = new RocketPlumeParticleData(0.03f);
        Vec3 contraptionMotion = context.motion;
        float radius;
        int amount;
        if (context.state.is(BlockInit.BIG_ROCKET_ENGINE.get())){
            radius = 0.65f;
            amount = 50;
        } else {
            radius = 0.1f;
            amount = 3;
        }
        /*spawnParticles(world,
                pos.add(0, -1.3, 0),
                Direction.DOWN,
                amount, particle,
                contraptionMotion.y >= 0 ? -3f : 40,
                radius, contraptionMotion);*/
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderTypes.getGlowingTranslucent(CreatingSpace.resource("textures/block/moon_regolith.png")));
        PoseStack matrixStack = matrices.getViewProjection();
        matrixStack.pushPose();
        // Translate and rotate the cone to the entity's position and orientation
        // Radius of the cone base
        Vec3 firstOffset = Vec3.atBottomCenterOf(context.localPos.below());
        // Render the cone
        segments = 16;
        N = 100;
        maxDistance = 20f;
        step = maxDistance / N;
        int overlay = LightTexture.FULL_BRIGHT;
        for (float x = 0; x < maxDistance; x += step) {
            GeometryRendering.renderCylinder(vertexBuilder, matrixStack, new Vec3(0, -x, 0).add(firstOffset), getColorBell(x), overlay, flamme_width(x + step), flamme_width(x), step, segments, true);
        }
        GeometryRendering.renderCylinder(vertexBuilder, matrixStack, new Vec3(0, -maxDistance, 0).add(firstOffset), getColorBell(maxDistance), overlay, 0, flamme_width(maxDistance), step, segments, true);

        /*for (float x = 0; x < maxDistance; x += step) {
            GeometryRendering.renderCylinder(vertexBuilder, matrixStack, new Vec3(0, -x, 0).add(firstOffset), overlay, flamme_width(x + step), flamme_width(x), step, segments, getColorExterior(x));
        }

        GeometryRendering.renderCylinder(vertexBuilder, matrixStack, new Vec3(0, -maxDistance, 0).add(firstOffset), overlay, 0, flamme_width(maxDistance), step, segments, getColorExterior(maxDistance));
        for (float x = 0; x < maxDistance; x += step) {
            GeometryRendering.renderCylinder(vertexBuilder, matrixStack, new Vec3(0, -x / 3, 0).add(firstOffset), overlay, flamme_width(x + step) / 1.5f, flamme_width(x) / 1.5f, step / 3, segments, getColorInterior(x));
        }
        GeometryRendering.renderCylinder(vertexBuilder, matrixStack, new Vec3(0, -maxDistance / 3, 0).add(firstOffset), overlay, 0, flamme_width(maxDistance) / 1.5f, step, segments, getColorInterior(maxDistance));
        */
        matrixStack.popPose();

    }

    private static float flamme_width(float x) {
        return (float) (baseRadius + 3 * Math.sin(x / (maxDistance + 0.2f) * Math.PI / 2));
    }

    private static Color getColorBell(float x) {
        return new Color(0xfff0b00f).mixWith(new Color(0x00ec972d), Mth.clamp(x / maxDistance * 1.5f, 0, 1));
    }
    private static Color getColorExterior(float x) {
        return new Color(0xfff0b00f).mixWith(new Color(0x000fe9f0), Mth.clamp(1 - x / maxDistance * 1.5f, 0, 1));
    }

    private static Color getColorInterior(float x) {
        return new Color(0xff4fd2ff).mixWith(new Color(0x00e86642), Mth.clamp(x / maxDistance / 2, 0, 1));
    }

    //copied from Create's FluidFX
    public static void spawnParticles(Level world, Vec3 pos, Direction side, int amount, ParticleOptions particle,
                                      float angleDegree,float radius,Vec3 contraptionMotion) {
        Vec3 directionVec = Vec3.atLowerCornerOf(side.getNormal());

        //make a
        /*for (float tetha = 0; tetha < Math.PI*2; tetha += 0.1F) {

        }*/
        for (int i = 0; i < amount; i++) {
            Vec3 vec = VecHelper.offsetRandomly(Vec3.ZERO, r, radius)
                    .normalize();
            Vec3 posVec = VecHelper.clampComponentWise(vec,radius);
            //posVec = posVec.multiply(1,0,1);
            Vec3 motion = vec.scale(Math.asin(angleDegree*Math.PI/180)).add(directionVec.scale(Math.acos(angleDegree*Math.PI/180)));
            motion.add(contraptionMotion);
            posVec = posVec.add(pos).add(0, -0.2 * i, 0);
            world.addAlwaysVisibleParticle(particle, posVec.x, posVec.y, posVec.z, motion.x, motion.y, motion.z);
        }
    }
}
