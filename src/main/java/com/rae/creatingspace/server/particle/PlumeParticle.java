package com.rae.creatingspace.server.particle;

import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class PlumeParticle extends SimpleAnimatedParticle {
    private float drag;
    private Vec3 speed;
    public PlumeParticle(ClientLevel world, RocketPlumeParticleData data,
                         double x, double y, double z,
                         double xSpeed, double ySpeed, double zSpeed,
                         SpriteSet sprite)
        {
        super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
        this.quadSize *= 0.75F;
        this.lifetime = 200;
        hasPhysics = false;
        selectSprite(7);

        this.setPos(x, y, z);
        this.drag = data.drag;
        this.xo = x;
        this.yo = y;
        this.zo = z;

        this.speed = new Vec3(xSpeed,ySpeed,zSpeed);

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
            return;
        }
        if (drag!=0) {
            float scaleFactor = drag > 0 ? 1 / (1 + drag) : (1 - drag);

            speed.scale(scaleFactor);
            System.out.println(speed);
        }

        xd = speed.x();
        yd = speed.y();
        zd = speed.z();

        //setSpriteFromAge(sprites);
        morphColor(this.age);
        this.move(this.xd, this.yd, this.zd);
    }
    public void morphColor(int time) {

        setColor(Color.mixColors(0xEE8800, 0xEE2200, (float) Math.cos((double) time /this.lifetime*3.14/2)));
        setAlpha(1f);
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
            return new PlumeParticle(worldIn,data, x, y, z,xSpeed,ySpeed,zSpeed, this.spriteSet);
        }
    }
}
