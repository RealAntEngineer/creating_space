package com.rae.creatingspace.api.contraption;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.content.rocket.network.SpeedPosRotUpdatePacket;
import com.rae.creatingspace.init.PacketInit;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

/**
 * more abstract version of OrientedContraptionEntity, doubled with a sync pack for smooth animation.
 */
public abstract class Synced2AxisContraptionEntity extends AbstractContraptionEntity {
    private float yaw;
    private float pitch;
    private @NotNull Vec3 speed = Vec3.ZERO;
    private @NotNull Vec2 rotSpeed = Vec2.ZERO;
    private boolean dirty = false;

    //Quaternion for rotation ?
    private static final EntityDataAccessor<Direction> INITIAL_ORIENTATION =
            SynchedEntityData.defineId(Synced2AxisContraptionEntity.class, EntityDataSerializers.DIRECTION);


    public Synced2AxisContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        // TODO put chasers on the client
        super.tick();
        if (!level().isClientSide()) {
            /*if (dirty){

            }*/
            sendPacket();
            move(speed.x, speed.y, speed.z);
            pitch += rotSpeed.y;
            yaw += rotSpeed.x;
            System.out.println(speed);

        }

    }
    public Vec3 getCoord() {
        return getAnchorVec();
    }
    /*
    to avoid drifting.
    public void updateClientMotion() {
        Vec3 motion = speed.add(clientOffsetDiff.scale(ServerSpeedProvider.get()/2f));

        motion = VecHelper.clampComponentWise(motion, 1);
        //setContraptionMotion(motion);
        move(motion.x, motion.y, motion.z);
    }*/

    public void sendPacket() {
        if (!level().isClientSide()) {
            PacketInit.getChannel()
                    .send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                            new SpeedPosRotUpdatePacket(getId(), getAnchorVec(), speed, yaw, pitch, rotSpeed));
        }
    }
    //go back to client offset for both pos and rot, it was better.
    @OnlyIn(Dist.CLIENT)
    public static void handlePacket(SpeedPosRotUpdatePacket packet) {
        assert Minecraft.getInstance().level != null;
        Entity entity = Minecraft.getInstance().level.getEntity(packet.entityID);
        if (!(entity instanceof Synced2AxisContraptionEntity ce))
            return;
        ce.moveTo(packet.coord);
        ce.speed = packet.speed;
        ce.setContraptionMotion(packet.speed);
        ce.yaw = packet.yaw;
        ce.pitch = packet.pitch;
        ce.rotSpeed = packet.rotSpeed;
        System.out.println("packed received on the client with : "+packet.coord + " | " + packet.yaw + " | " + packet.pitch);
        //ce.lerpedYaw.chase(packet.yaw, packet.yaw - ce.yaw, LerpedFloat.Chaser.LINEAR);
        //ce.lerpedPitch.chase(packet.pitch, packet.pitch - ce.pitch, LerpedFloat.Chaser.LINEAR); -> chasers are better
        //ce.clientPosDiff = packet.coord.subtract(ce.getCoord());
    }
    /**
     * necessary to avoid "vibration" the  updateClientMotion() is used to sync client to server entity
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public final void lerpTo(double p_19896_, double p_19897_, double p_19898_, float p_19899_, float p_19900_, int p_19901_, boolean p_19902_) {
    }

    public void setSpeeds(Vec3 speed, Vec2 rotSpeed) {
        if (!level().isClientSide()) {
            if (speed!=null)this.speed = speed;
            if (rotSpeed !=null)this.rotSpeed = rotSpeed;
            this.dirty = true;
        }
    }

    @Override
    public void setContraptionMotion(Vec3 vec) {
        super.setContraptionMotion(vec);
        setSpeeds(vec, null);
    }

    public @NotNull Vec3 getSpeed() {
        return speed;
    }

    @Override
    protected void tickContraption() {
        tickActors();
    }

    //Cleaned up OrientedContraptionEntity
    public void setInitialOrientation(Direction direction) {
        entityData.set(INITIAL_ORIENTATION, direction);
    }

    public Direction getInitialOrientation() {
        return entityData.get(INITIAL_ORIENTATION);
    }

    @Override
    public float getYawOffset() {
        return getInitialYaw();
    }

    public float getInitialYaw() {
        return entityData.get(INITIAL_ORIENTATION).toYRot();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(INITIAL_ORIENTATION, Direction.UP);
    }

    @Override
    public ContraptionRotationState getRotationState() {
        ContraptionRotationState crs = new ContraptionRotationState();

        float yawOffset = getYawOffset();
        crs.zRotation = pitch;
        crs.yRotation = -yaw + yawOffset;

        if (pitch != 0 && yaw != 0) {
            crs.secondYRotation = -yaw;
            crs.yRotation = yawOffset;
        }

        return crs;
    }

    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
        super.readAdditional(compound, spawnPacket);

        if (compound.contains("InitialOrientation"))
            setInitialOrientation(NBTHelper.readEnum(compound, "InitialOrientation", Direction.class));

        yaw = compound.getFloat("Yaw");
        pitch = compound.getFloat("Pitch");


    }

    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        super.writeAdditional(compound, spawnPacket);
        NBTHelper.writeEnum(compound, "InitialOrientation", entityData.get(INITIAL_ORIENTATION));

        compound.putFloat("Yaw", yaw);
        compound.putFloat("Pitch", pitch);

    }


    public void startAtInitialYaw() {
        startAtYaw(getInitialYaw());
    }

    public void startAtYaw(float yaw) {
        this.yaw = yaw;
        this.rotSpeed = new Vec2(0, 0);
    }

    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate(localPos, getInitialYaw(), Direction.Axis.Y);
        localPos = VecHelper.rotate(localPos, getViewXRot(partialTicks), Direction.Axis.Z);
        localPos = VecHelper.rotate(localPos, getViewYRot(partialTicks), Direction.Axis.Y);
        return localPos;
    }

    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate(localPos, -getViewYRot(partialTicks), Direction.Axis.Y);
        localPos = VecHelper.rotate(localPos, -getViewXRot(partialTicks), Direction.Axis.Z);
        localPos = VecHelper.rotate(localPos, -getInitialYaw(), Direction.Axis.Y);
        return localPos;
    }

    public float getViewYRot(float partialTicks) {
        if (level().isClientSide()) {
            return - (yaw + rotSpeed.x * partialTicks);
        } else {
            return yaw;
        }
    }

    public float getViewXRot(float partialTicks) {
        if (level().isClientSide()) {
            return (pitch + rotSpeed.y * partialTicks);
        } else {
            return pitch;
        }
    }

    @Override
    protected StructureTransform makeStructureTransform() {
        BlockPos offset = BlockPos.containing(getAnchorVec().add(.5, .5, .5));
        return new StructureTransform(offset, 0, -yaw + getInitialYaw(), 0);
    }

    @Override
    protected float getStalledAngle() {
        return yaw;
    }

    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        yaw = angle;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        float angleInitialYaw = getInitialYaw();
        float angleYaw = getViewYRot(partialTicks);
        float anglePitch = getViewXRot(partialTicks);
        //first order interpolation, for a better interpolation there should also be acceleration.
        matrixStack.translate(speed.x * partialTicks, speed.y * partialTicks, speed.z*partialTicks);
        System.out.println(speed.x * partialTicks +" | "+ speed.y * partialTicks + " | " + speed.z*partialTicks);
        TransformStack.of(matrixStack)
                .nudge(getId())
                .center()
                .rotateYDegrees(angleYaw)
                .rotateZDegrees(anglePitch)
                .rotateYDegrees(angleInitialYaw)
                .uncenter();
    }
}
