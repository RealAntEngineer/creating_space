package com.rae.creatingspace.server.particle;

import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class PlumeParticle extends SimpleAnimatedParticle {
    protected PlumeParticle(ClientLevel world, double x, double y, double z,
                              SpriteSet sprite) {
        super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
        this.quadSize *= 0.75F;
        this.lifetime = 200;
        hasPhysics = true;
        selectSprite(7);
        Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, world.random, .25f);
        this.setPos(x + offset.x, y + offset.y, z + offset.z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        setAlpha(.25f);
    }
    @Nonnull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {

            Vec3 directionVec = Vec3.atLowerCornerOf(Direction.DOWN.getNormal());
            Vec3 motion = directionVec.scale(1 / 8f);


            morphType(this.age);

            xd = motion.x;
            yd = motion.y;
            zd = motion.z;

            if (this.onGround) {
                this.xd *= 0.7;
                this.zd *= 0.7;
            }
            this.move(this.xd, this.yd, this.zd);

        }

    }

    public void morphType(int time) {


        if (time >40) {
            setColor(Color.mixColors(0x0, 0x555555, level.random.nextFloat()));
            setAlpha(1f);
            selectSprite(level.random.nextInt(3));
            if (level.random.nextFloat() < 1 / 32f)
                level.addParticle(ParticleTypes.SMOKE, x, y, z, xd * .125f, yd * .125f,
                        zd * .125f);
            if (level.random.nextFloat() < 1 / 32f)
                level.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, xd * .125f, yd * .125f,
                        zd * .125f);
        } else  {
            setColor(0xEEEEEE);
            setAlpha(.25f);
            setSize(.2f, .2f);
        }
    }
    public int getLightColor(float partialTick) {
        BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
        return this.level.isLoaded(blockpos) ? LevelRenderer.getLightColor(level, blockpos) : 0;
    }

    private void selectSprite(int index) {
        setSprite(sprites.get(index, 8));
    }

    public static class Factory implements ParticleProvider<RocketPlumeParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        public Particle createParticle(RocketPlumeParticleData data, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new PlumeParticle(worldIn, x, y, z, this.spriteSet);
        }
    }
}
