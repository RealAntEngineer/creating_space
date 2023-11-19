package com.rae.creatingspace.server.particle;

import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.util.Mth.clamp;

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
        this.lifetime = (int) (200*world.random.nextFloat());
        hasPhysics = true;
        selectSprite(0);
        this.setPos(x, y, z);
        this.drag = (float) (data.drag*Math.max(0.8,(world.random.nextFloat()+0.4)));
        this.xo = x;
        this.yo = y;
        this.zo = z;

        this.speed = new Vec3(xSpeed,ySpeed,zSpeed);
        this.morphColor();
        //setAlpha(.25f);
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

            speed = speed.scale(scaleFactor);
        }

        xd = speed.x();
        yd = speed.y();
        zd = speed.z();



        //setSpriteFromAge(sprites);
        morphColor();
        this.move(this.xd, this.yd, this.zd);

        if (speed.length()<1){
            selectSprite(1);
            if (this.quadSize < 2) {
                scale(drag * 3 + 1);
            }

            if (speed.length() < 0.05f){
                this.remove();
                return;
            }
        }

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

    public void setFire(){
        BlockPos collidePos = new BlockPos(x,y,z);
        //BlockState blockState = this.level.getBlockState(collidePos.below());
        if (((FireBlock)Blocks.FIRE).canCatchFire(this.level,collidePos.below(),Direction.UP)) {
            this.level.setBlockAndUpdate(collidePos, Blocks.FIRE.defaultBlockState());
            //this.level.sendPacketToServer(Packet);
            this.level.scheduleTick(collidePos,this.level.getBlockState(collidePos).getBlock(),1);
        }
    }

    public void morphColor() {
        Color color = new Color(0x0088EE);
        color = color.setAlpha(1f);
        if (speed.length() < 1D){
            color = color.mixWith(Color.WHITE, (float) (1 - speed.length()/3f));
        }

        Vec3 colorVec = color.asVector();
        setColor((float) colorVec.x, (float) colorVec.y, (float) colorVec.z);
        setAlpha((float) clamp(speed.length()*2,0.2,1));

    }
    public int getLightColor(float partialTick) {
        BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
        return this.level.isLoaded(blockpos) ? 15728880 : 0;
    }

    private void selectSprite(int index) {
        setSprite(sprites.get(index, 1));
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
