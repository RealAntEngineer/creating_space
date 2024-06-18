package com.rae.creatingspace.server.contraption.behaviour.movement;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.server.particle.RocketPlumeParticleData;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EngineMovementBehaviour implements MovementBehaviour {

    static RandomSource r = RandomSource.create();

    @Override
    public boolean isActive(MovementContext context) {
        return false;//MovementBehaviour.super.isActive(context) && (context.contraption.entity instanceof RocketContraptionEntity rocketEntity) && context.motion.length() != 0;
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
        spawnParticles(world,
                pos.add(0, -1.3, 0),
                Direction.DOWN,
                amount, particle,
                contraptionMotion.y >= 0 ? -3f : 40,
                radius, contraptionMotion);
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {

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
