package com.rae.creatingspace.server.particle;

import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.util.Mth.clamp;

public class PlumeParticle extends SimpleAnimatedParticle {
    public static final float SIZE_FACTOR = .6f;
    private float drag;
    private Vec3 speed;
    public PlumeParticle(ClientLevel world, RocketPlumeParticleData data,
                         double x, double y, double z,
                         double xSpeed, double ySpeed, double zSpeed,
                         SpriteSet sprite)
        {
        super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
        this.lifetime = (int) (200*world.random.nextFloat());
        hasPhysics = true;
        selectSprite(0);
        this.setPos(x, y, z);
        this.drag = (float) (data.drag*Math.max(0.8,(world.random.nextFloat()+0.4)));
        this.xo = x;
        this.yo = y;
        this.zo = z;

        this.speed = new Vec3(xSpeed,ySpeed,zSpeed);
            quadSize = (float) speed.length() * SIZE_FACTOR;

        this.morphColor();
        //setAlpha(.25f);
    }

    @Nonnull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
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

        // Scaling and size adjustments based on speed
        if (speed.length() < 0.2) {
            selectSprite(2 + age / 200);
            if (this.quadSize < 2) {
                scale(drag * 3 + 1);
            }
            if (speed.length() < 0.05f) {
                this.remove();
                return;
            }
        } else {
            quadSize = (float) speed.length() * SIZE_FACTOR;
        }
        if (speed.length() < 0.05f) {
            this.remove();
            return;
        }

        // Apply drag if present
        if (drag != 0) {
            float scaleFactor = drag > 0 ? 1 / (1 + drag) : (1 - drag);
            speed = speed.scale(scaleFactor);
        }

        // Update position
        xd = speed.x();
        yd = speed.y();
        zd = speed.z();
        morphColor();
        this.move(this.xd, this.yd, this.zd);

        // Check if the particle is close to the ground and set fire
        if (isCloseToGround()) {
            setFire();
        }
    }

    private boolean isCloseToGround() {
        BlockPos pos = new BlockPos(this.x, this.y, this.z);
        // Check a specific range below the particle, adjust the range as needed
        for (int i = 1; i <= 3; i++) {
            BlockPos below = pos.below(i);
            if (!this.level.isEmptyBlock(below)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void move(double vx, double vy, double vz) {
        //List<VoxelShape> list = this.level.getEntityCollisions(null,this.getBoundingBox().expandTowards(vx,vy,vz));
        Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(vx, vy, vz), this.getBoundingBox(), this.level, List.of());

        float coef = vec3.distanceTo(new Vec3(vx,vy,vz)) > 0.1 ? (float) 0.7 : 1;

        Vec3 vFinal = vec3.normalize().scale(speed.length()*coef);

        speed = vFinal;
        this.setBoundingBox(this.getBoundingBox().move(vec3));
        this.setLocationFromBoundingbox();
    }

    public void setFire() {
        BlockPos pos = new BlockPos(this.x, this.y - 1, this.z); // Position of the block directly below the particle
        BlockState blockState = this.level.getBlockState(pos);

        Direction[] directions = {Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction direction : directions) {
            BlockPos adjacentPos = pos.relative(direction);
            BlockState adjacentState = this.level.getBlockState(adjacentPos);

            // Check if the adjacent position is air and if the block is flammable on the checked side
            if (this.level.isEmptyBlock(adjacentPos) && blockState.getBlock().isFlammable(blockState, this.level, pos, direction)) {
                this.level.setBlockAndUpdate(adjacentPos, Blocks.FIRE.defaultBlockState());
                break;
            }
        }
    }


    public void morphColor() {
        float speedFactor = (float) speed.length();
        // Mix between yellow and red based on speed
        Color yellow = new Color(0xFFFF00);
        Color red = new Color(0xFF0000);
        Color color = mixColors(yellow, red, speedFactor);
        if (speed.length() < 1D) {
            color = color.mixWith(Color.WHITE, (float) (1 - speed.length() / 3f));
        }
        Vec3 colorVec = color.asVector();
        setColor((float) colorVec.x, (float) colorVec.y, (float) colorVec.z);
        setAlpha((float) clamp(speed.length()*2, 0.2, 1));
    }

    // Utility method for color mixing
    private Color mixColors(Color color1, Color color2, float factor) {
        int red = (int) (color1.getRed() * (1 - factor) + color2.getRed() * factor);
        int green = (int) (color1.getGreen() * (1 - factor) + color2.getGreen() * factor);
        int blue = (int) (color1.getBlue() * (1 - factor) + color2.getBlue() * factor);
        return new Color(red, green, blue);
    }
    public int getLightColor(float partialTick) {
        BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
        return this.level.isLoaded(blockpos) ? 15728880 : 0;
    }

    private void selectSprite(int index) {
        setSprite(sprites.get(index, 10));
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
