package com.rae.creatingspace.api.contraption;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.content.rocket.network.SpeedPosRotUpdatePacket;
import com.rae.creatingspace.init.PacketInit;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.redstone.contact.ContactMovementBehaviour;
import com.simibubi.create.content.redstone.contact.RedstoneContactBlock;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;

/**
 * more abstract version of OrientedContraptionEntity, doubled with a sync packet for smooth animation.
 */
public abstract class Synced2AxisContraptionEntity extends AbstractContraptionEntity {


    private float yaw;
    private float pitch;
    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
    private LinearLerpedVec3 speed = new LinearLerpedVec3(0,0,0);
    private @NotNull Vec3 posClientDiff = Vec3.ZERO;
    private @NotNull Vec2 rotSpeed = Vec2.ZERO;
    private @NotNull Vec2 rotClientDiff = Vec2.ZERO;
    //TODO add inertia (acceleration) -> lerp the speed goals ?
    //Quaternion for rotation ?
    private static final EntityDataAccessor<Direction> INITIAL_ORIENTATION =
            SynchedEntityData.defineId(Synced2AxisContraptionEntity.class, EntityDataSerializers.DIRECTION);


    public Synced2AxisContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    LerpedFloat lerpedPith = LerpedFloat.angular().startWithValue(0);
    LerpedFloat lerpedYaw = LerpedFloat.angular().startWithValue(0);
    @Override
    public void tick() {
        // TODO put chasers on the client
        super.tick();
        if (!level().isClientSide()) {


            Vec3 motion =  speed.getVec3(0);
            move(motion.x, motion.y, motion.z);
            super.setContraptionMotion(motion);
            speed.tickChaser();
            //System.out.println("server | time :"+level().getGameTime()+" speed :"+motion+" pos :"+getPosition(0));
            yaw += rotSpeed.y;
            pitch += rotSpeed.x;
            yaw = AngleHelper.wrapAngle180(yaw);
            pitch = AngleHelper.wrapAngle180(pitch);
            pitch = Mth.clamp(pitch,-90,90);

            lerpedPith.setValue(pitch);
            lerpedYaw.setValue(yaw);
            sendPacket();
        } else {
            posClientDiff.scale(0.75f);
            rotClientDiff.scale(0.75f);
            updateClientMotion();
            lerpedYaw.tickChaser();
            lerpedPith.tickChaser();
        }

    }

    @Override
    public void setContraptionMotion(Vec3 vec) {
        if (level().isClientSide()) {
            super.setContraptionMotion(vec);
        } else {
            speed.chase(vec, 10);
        }
    }

    public void updateClientMotion() {
        Vec3 motion = getDeltaMovement().add(posClientDiff.scale(ServerSpeedProvider.get()/2f));

        motion = VecHelper.clampComponentWise(motion, 1);
        //setContraptionMotion(motion);
        move(motion.x, motion.y, motion.z);
        setContraptionMotion(motion);


        pitch += rotSpeed.x + rotClientDiff.x * ServerSpeedProvider.get()/2f;
        yaw += rotSpeed.y + rotClientDiff.y * ServerSpeedProvider.get()/2f;
        pitch =  AngleHelper.wrapAngle180(pitch);
        yaw =  AngleHelper.wrapAngle180(yaw);
        lerpedPith.chase( pitch ,Math.abs(rotSpeed.x)+1, LerpedFloat.Chaser.LINEAR);
        lerpedYaw.chase( yaw ,Math.abs(rotSpeed.y)+1, LerpedFloat.Chaser.LINEAR);
    }

    public void sendPacket() {
        //System.out.println("server | time :"+ (level().getGameTime())+" | " + yaw + " | " + rotSpeed.y);

        if (!level().isClientSide()) {
            PacketInit.getChannel()
                    .send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                            new SpeedPosRotUpdatePacket(getId(), getAnchorVec(), getDeltaMovement(), yaw, pitch, rotSpeed));
        }
    }
    //go back to client offset for both pos and rot, it was better.
    @OnlyIn(Dist.CLIENT)
    public static void handlePacket(SpeedPosRotUpdatePacket packet) {
        assert Minecraft.getInstance().level != null;
        Entity entity = Minecraft.getInstance().level.getEntity(packet.entityID);
        if (!(entity instanceof Synced2AxisContraptionEntity ce))
            return;
        ce.posClientDiff = packet.coord.subtract(ce.getAnchorVec());
        ce.setContraptionMotion(packet.speed);
        ce.rotClientDiff = new Vec2( AngleHelper.getShortestAngleDiff(packet.pitch , ce.pitch),
                AngleHelper.getShortestAngleDiff(packet.yaw , ce.yaw));
        ce.yaw = packet.yaw;
        ce.pitch = packet.pitch;
        ce.rotSpeed = packet.rotSpeed;
    }
    /**
     * necessary to avoid "vibration" the  updateClientMotion() is used to sync client to server entity
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public final void lerpTo(double p_19896_, double p_19897_, double p_19898_, float p_19899_, float p_19900_, int p_19901_, boolean p_19902_) {
    }

    @Override
    protected void tickContraption() { // -> maybe on the rocket rather than on the Synced Axis contraption ?
        tickActors();
        //aligns redstone contacts
        //Ticks the redstone contacts 2 times if on server
        if (!level().isClientSide()) {
            for (MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> pair : contraption.getActors()) {
                MovementContext context = pair.right;
                StructureTemplate.StructureBlockInfo blockInfo = pair.left;
                MovementBehaviour actor = MovementBehaviour.REGISTRY.get(blockInfo.state());
                if (actor instanceof ContactMovementBehaviour redstoneContact) {

                    Vec3 oldMotion = context.motion;
                    Vec3 actorPosition = toGlobalVector(VecHelper.getCenterOf(blockInfo.pos())
                            .add(actor.getActiveAreaOffset(context)), 1);
                    BlockPos gridPosition = BlockPos.containing(actorPosition);
                    boolean newPosVisited =
                            !context.stall && shouldActorTrigger(context, blockInfo, actor, actorPosition, gridPosition);
                    context.rotation = v -> applyRotation(v, 1);
                    context.position = actorPosition;
                    if (!isActorActive(context, actor) && !actor.mustTickWhileDisabled())
                        continue;
                    if (newPosVisited && !context.stall) {
                        redstoneContact.visitNewPosition(context, gridPosition);
                        if (!isAlive())
                            break;
                        context.firstMovement = false;
                    }
                    if (!oldMotion.equals(context.motion)) {
                        redstoneContact.onSpeedChanged(context, oldMotion, context.motion);
                        if (!isAlive())
                            break;
                    }
                    redstoneContact.tick(context);
                    if (context.data.contains("lastContact")) {
                        BlockPos pos = NbtUtils.readBlockPos((CompoundTag) context.data.get("lastContact"));
                        float worldDirection = level().getBlockState(pos).getValue(RedstoneContactBlock.FACING).toYRot();
                        float currentDirection = blockInfo.state().getValue(RedstoneContactBlock.FACING).toYRot();
                        setPos(Vec3.atLowerCornerOf(pos.relative(level().getBlockState(pos).getValue(RedstoneContactBlock.FACING)).subtract(blockInfo.pos())));
                        yaw = worldDirection-currentDirection;
                    }
                    if (!isAlive())
                        break;
                }
            }

        }
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
        float syncedYaw;
        float syncedPitch;
        if (level().isClientSide()) {
            syncedPitch = lerpedPith.getValue(Minecraft.getInstance().getPartialTick());
            syncedYaw = lerpedYaw.getValue(Minecraft.getInstance().getPartialTick());
        }
        else {
            syncedPitch = pitch;
            syncedYaw = yaw;
        }
        crs.zRotation = syncedPitch;
        crs.yRotation = -syncedYaw + yawOffset;

        if (pitch != 0 && yaw != 0) {
            crs.secondYRotation = -syncedYaw;
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
        lerpedYaw.setValue(yaw);
        pitch = compound.getFloat("Pitch");
        lerpedPith.setValue(pitch);

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
        lerpedYaw.setValue(yaw);
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
        return -lerpedYaw.getValue(partialTicks);
    }

    public float getViewXRot(float partialTicks) {
        return lerpedPith.getValue(partialTicks);
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
    //why do lerp doesn't work ?
    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        float angleInitialYaw = getInitialYaw();
        float angleYaw = getViewYRot(partialTicks);
        float anglePitch = getViewXRot(partialTicks);
        //first order interpolation, for a better interpolation there should also be acceleration.
        //Still jagged as fuck.
        //System.out.println("client | time :"+ (level().getGameTime() + partialTicks)+ " | " +yaw+" | "+ lerpedYaw.getValue(partialTicks));

        TransformStack.of(matrixStack)
                .nudge(getId())
                .center()
                .rotateYDegrees(angleYaw)
                .rotateZDegrees(anglePitch)
                .rotateYDegrees(angleInitialYaw)
                .uncenter();
    }


    public void setRotSpeed(@NotNull Vec2 rotSpeed) {
        this.rotSpeed = rotSpeed;
    }
}
