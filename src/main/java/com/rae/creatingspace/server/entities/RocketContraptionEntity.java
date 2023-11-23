package com.rae.creatingspace.server.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.ingameobject.EntityInit;
import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import com.rae.creatingspace.server.contraption.RocketContraption;
import com.rae.creatingspace.utilities.CSDimensionUtil;
import com.rae.creatingspace.utilities.CustomTeleporter;
import com.rae.creatingspace.utilities.packet.RocketContraptionUpdatePacket;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RocketContraptionEntity extends AbstractContraptionEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    Direction movementAxis = Direction.UP;
    double clientOffsetDiff;
    double axisMotion;
    float totalConsumedAmount;
    float totalTickTime;
    float partialConsumedAmount = 0;
    public BlockPos rocketEntryCoordinate = new BlockPos(0,0,0);
    public float trust = 0;
    public float initialMass;
    private int propellantConsumption = 0;
    public ResourceKey<Level> originDimension = Level.OVERWORLD;
    public ResourceKey<Level> destination;
    private boolean disassembleOnFirstTick = false;
    static float O2ro = (float) FluidInit.LIQUID_OXYGEN.get().getFluidType().getDensity() / 1000;
    static float CH4ro = (float) FluidInit.LIQUID_METHANE.get().getFluidType().getDensity() / 1000;

    public HashMap<String, ArrayList<Fluid>> consumableFluids = new HashMap<>(
            Map.of("oxygen",new ArrayList<>(), "methane", new ArrayList<>()));
    //initializing and saving methods

    public RocketContraptionEntity(EntityType<?> type, Level level) {
        super(type, level);
    }
    public static RocketContraptionEntity create(Level level, RocketContraption contraption, ResourceKey<Level> destination) {
        RocketContraptionEntity entity =
                new RocketContraptionEntity(EntityInit.ROCKET_CONTRAPTION.get(), level);
        entity.originDimension = level.dimension();
        entity.destination = destination;

        entity.setContraption(contraption);
        handelTrajectoryCalculation(entity);
        entity.trust = contraption.getTrust();

        LOGGER.info("finishing setting up parameters");
        entity.noPhysics = false;
        return entity;
    }

    private static void handelTrajectoryCalculation(RocketContraptionEntity rocketContraptionEntity){
        RocketContraption contraption = (RocketContraption) rocketContraptionEntity.contraption;

        float deltaVNeeded = CSDimensionUtil.accessibleFrom(rocketContraptionEntity.originDimension)
                .get(rocketContraptionEntity.destination).deltaV();

        if (contraption==null){
            return;
        }

        float trust = contraption.getTrust();
        float propellantConsumption = contraption.getPropellantConsumption();
        float Ve = trust / propellantConsumption;
        float inertFluidsMass= 0;

        float o2amount = 0;
        float ch4amount = 0;

        IFluidHandler fluidHandler = contraption.getSharedFluidTanks();

        int nbrOfTank = fluidHandler.getTanks();

        for (int i=0 ; i < nbrOfTank; i++) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
            FluidType fluidType = fluidInTank.getFluid().getFluidType();

            if (TagsInit.CustomFluidTags.LIQUID_METHANE.matches(fluidInTank.getFluid())){
                ch4amount += fluidHandler.getFluidInTank(i).getAmount();
                if (!rocketContraptionEntity.consumableFluids.get("methane").contains(fluidInTank.getFluid())){
                    rocketContraptionEntity.consumableFluids.get("methane").add(fluidInTank.getFluid());
                }

            }
            else if (TagsInit.CustomFluidTags.LIQUID_OXYGEN.matches(fluidInTank.getFluid())){
                o2amount += fluidHandler.getFluidInTank(i).getAmount();
                if (!rocketContraptionEntity.consumableFluids.get("oxygen").contains(fluidInTank.getFluid())){
                    rocketContraptionEntity.consumableFluids.get("oxygen").add(fluidInTank.getFluid());
                }
            }
            else {

                inertFluidsMass += (float) (fluidInTank.getAmount() * fluidType.getDensity()) /1000;
            }
        }

        float emptyMass = inertFluidsMass + contraption.getDryMass();
        float initialPropellantMass = o2amount*O2ro+ch4amount*CH4ro;
        float finalPropellantMass = (float) ((emptyMass+initialPropellantMass)/Math.exp(deltaVNeeded/Ve)-emptyMass);

        float consumedPropellantMass = initialPropellantMass - finalPropellantMass;

        rocketContraptionEntity.totalConsumedAmount = consumedPropellantMass/(CH4ro+O2ro);
        rocketContraptionEntity.initialMass = emptyMass+initialPropellantMass;

        int distance = (int) (300 - rocketContraptionEntity.position().y());

        float gravity = CSDimensionUtil.gravity(rocketContraptionEntity.level.dimensionTypeId());

        float acceleration = trust/(emptyMass+initialPropellantMass)-gravity;
        float perTickSpeed = getPerTickSpeed(acceleration);

        rocketContraptionEntity.totalTickTime = distance/perTickSpeed;
        if (acceleration <=0 ){
            rocketContraptionEntity.disassembleOnFirstTick = true;
        }

        if (rocketContraptionEntity.totalConsumedAmount > o2amount||rocketContraptionEntity.totalConsumedAmount >ch4amount){
            rocketContraptionEntity.disassembleOnFirstTick = true;
        }
        if (distance<=0){
            rocketContraptionEntity.disassembleOnFirstTick = true;
        }
    }

    private static float getPerTickSpeed(float acceleration) {
        float perTickSpeed;
        perTickSpeed= (float) ( Math.signum(acceleration)*Math.log(1.4 + Math.abs(acceleration)/20));
        perTickSpeed = Mth.clamp(perTickSpeed, -1, 1);
        return perTickSpeed;
    }

    public static final EntityDataAccessor<Boolean> REENTRY_ENTITY_DATA_ACCESSOR =
            SynchedEntityData.defineId(RocketContraptionEntity.class, EntityDataSerializers.BOOLEAN);

   @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(REENTRY_ENTITY_DATA_ACCESSOR,false);
          }

    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnData) {
        super.readAdditional(compound, spawnData);
        this.trust = compound.getFloat("trust");
        this.initialMass = compound.getFloat("initialMass");
        this.totalTickTime = compound.getFloat("totalTime");
        this.totalConsumedAmount = compound.getFloat("totalCA");
        this.partialConsumedAmount = compound.getFloat("partialCA");
        this.propellantConsumption = compound.getInt("propellantConsumption");
        this.entityData.set(REENTRY_ENTITY_DATA_ACCESSOR,compound.getBoolean("reentry"));

        this.destination = ResourceKey.create(Registry.DIMENSION_REGISTRY,
                new ResourceLocation(
                        compound.getString("destination:nameSpace"),
                        compound.getString("destination:path")));

        this.originDimension = ResourceKey.create(Registry.DIMENSION_REGISTRY,
                new ResourceLocation(
                        compound.getString("origin:nameSpace"),
                        compound.getString("origin:path")));
        fillConsumableFluidsMap();


    }
    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        compound.putInt("propellantConsumption", this.propellantConsumption);
        compound.putFloat("initialMass",this.initialMass);
        compound.putFloat("totalTime",this.totalTickTime);
        compound.putFloat("totalCA",this.totalConsumedAmount);
        compound.putFloat("partialCA",this.partialConsumedAmount);
        compound.putFloat("trust",this.trust);

        compound.putBoolean("reentry",isReentry());

        compound.putString("origin:nameSpace",this.originDimension.location().getNamespace());
        compound.putString("origin:path",this.originDimension.location().getPath());


        compound.putString("destination:nameSpace",this.destination.location().getNamespace());
        compound.putString("destination:path",this.destination.location().getPath());
        super.writeAdditional(compound, spawnPacket);
    }
    @Override
    protected void tickContraption() {
        if (!(contraption instanceof RocketContraption))
            return;

        if (disassembleOnFirstTick){
            if (!level.isClientSide){
                disassemble();
            }
            return;
        }

        double prevAxisMotion = axisMotion;
        if (level.isClientSide) {
            clientOffsetDiff *= .75f;
            updateClientMotion();
        }

        tickConsumptionAndSpeed();
        tickActors();
        Vec3 movementVec = getDeltaMovement();
        if (!level.isClientSide)tickDimensionChangeLogic();


        if (ContraptionCollider.collideBlocks(this)) {
            if (!level.isClientSide)
                disassemble();
            return;
        }
        if (tickCount>2) {
            movementVec = VecHelper.clampComponentWise(movementVec, (float) 1);
            move(movementVec.x, movementVec.y, movementVec.z);
        }
        if (Math.signum(prevAxisMotion) != Math.signum(axisMotion) && prevAxisMotion != 0)
            contraption.stop(level);
        if (!level.isClientSide)
            sendPacket();
    }

    /*@Override
    public void move(double x, double y, double z) {
        Vec3 prevPos = this.position();
        super.move(MoverType.PISTON,new Vec3(x, y, z));
        if (!this.level.isClientSide() && (y!=0||x!=0||z!=0)){
            if(prevPos == this.position() ){
                disassemble();
            }
        }
    }*/


    @Override
    public Vec3 getContactPointMotion(Vec3 globalContactPoint) {
        if (contraption instanceof TranslatingContraption)
            return getDeltaMovement();
        return super.getContactPointMotion(globalContactPoint);
    }

    private void tickDimensionChangeLogic() {
        if (position().get(Direction.Axis.Y) > 300  &&  !isReentry()){


            ServerLevel destServerLevel = this.level.getServer().getLevel(this.destination);

            if (destServerLevel!=null /*&& level.dimension() == this.originDimension*/) {

                this.changeDimension(destServerLevel,new CustomTeleporter(destServerLevel));
            }
            else {
                LOGGER.info("dimension change failed at first step");
                LOGGER.info("rocket info :");
                LOGGER.info("destination :" + destServerLevel);
                LOGGER.info("current dimension :" + level.dimension());
                LOGGER.info("origin Dimension : " + this.originDimension);
                LOGGER.info("gravity of current dimension" + CSDimensionUtil.gravity(this.level.dimensionTypeId()));
            }
        }
    }
    protected void tickConsumptionAndSpeed() {
        if (level.isClientSide())
            return;

        float gravity = CSDimensionUtil.gravity(this.level.dimensionTypeId());

        if (!isReentry() ){
            if (!level.isClientSide())
                consumePropellant(this);
        }

        Vec3 movementVec;
        float acceleration = getAcceleration(
                initialMass,
                (int) trust,gravity,isReentry());

        float speed = getPerTickSpeed(acceleration);
        movementVec = new Vec3(0,speed,0);

        axisMotion = speed;
        setContraptionMotion(movementVec);
    }

    private void consumePropellant(RocketContraptionEntity rocketContraptionEntity) {
        if (level.isClientSide()){
            return;
        }
        RocketContraption rocketContraption = (RocketContraption) rocketContraptionEntity.contraption;
        IFluidHandler fluidHandler = rocketContraption.getSharedFluidTanks();

        float drainAmount =  (totalConsumedAmount / totalTickTime);

        if (drainAmount <0){
            drainAmount = rocketContraption.getPropellantConsumption();
        }
        else {
            partialConsumedAmount = partialConsumedAmount +  drainAmount - ((int)drainAmount);
            if (partialConsumedAmount>=1){
                drainAmount = drainAmount + 1;
                partialConsumedAmount = partialConsumedAmount -1;
            }
        }
        //make in a loop so it look for every one ?
        Fluid methaneFluid = consumableFluids.get("methane").get(0);
        Fluid oxygenFluid = consumableFluids.get("oxygen").get(0);
        //correct that so it works for all the fluids matching the methane and oxygen tag -> list of consumable fluid ?
        int consumedMethane = fluidHandler.drain(new FluidStack(methaneFluid, (int) drainAmount) , IFluidHandler.FluidAction.EXECUTE ).getAmount();//drain methane
        int consumedOxygen = fluidHandler.drain(new FluidStack(oxygenFluid, (int) drainAmount) , IFluidHandler.FluidAction.EXECUTE ).getAmount();//drain oxygen

        if (consumedOxygen ==0 || consumedMethane==0){
            fillConsumableFluidsMap();
        }

    }

    private void fillConsumableFluidsMap(){
        IFluidHandler fluidHandler = contraption.getSharedFluidTanks();

        int nbrOfTank = fluidHandler.getTanks();

        for (int i=0 ; i < nbrOfTank; i++) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(i);

            if (TagsInit.CustomFluidTags.LIQUID_METHANE.matches(fluidInTank.getFluid())){
                if (!this.consumableFluids.get("methane").contains(fluidInTank.getFluid())){
                    this.consumableFluids.get("methane").add(fluidInTank.getFluid());
                }

            }
            else if (TagsInit.CustomFluidTags.LIQUID_OXYGEN.matches(fluidInTank.getFluid())){
                if (!this.consumableFluids.get("oxygen").contains(fluidInTank.getFluid())){
                    this.consumableFluids.get("oxygen").add(fluidInTank.getFluid());
                }
            }
        }
    }
    @Nullable
    @Override
    public Entity changeDimension(ServerLevel destLevel, ITeleporter teleporter) {
        //rewrite so passengers get teleported with it
        if (!ForgeHooks.onTravelToDimension(this, destLevel.dimension())) return null;
        if (this.level instanceof ServerLevel && !this.isRemoved()) {
            this.level.getProfiler().push("changeDimension");

            List<Entity> passengers = this.getPassengers();

            this.unRide();
            this.level.getProfiler().push("reposition");
            PortalInfo portalinfo = teleporter.getPortalInfo(this, destLevel, this::findDimensionEntryPoint);
            if (portalinfo == null) {
                return null;
            } else {
                Entity transportedEntity = teleporter.placeEntity(this, (ServerLevel) this.level, destLevel, this.getYRot(),

                        spawnPortal -> { //Forge: Start custom logic
                            this.level.getProfiler().popPush("reloading");

                            RocketContraptionEntity entity = (RocketContraptionEntity) this.getType().create(destLevel);

                            if (entity != null) {

                                entity.restoreFrom(this);//copy the contraption first
                                entity.moveTo(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, portalinfo.yRot, entity.getXRot());
                                entity.setDeltaMovement(portalinfo.speed);
                                //adding previously riding passengers
                                for (int i = 0; i < passengers.size(); i++) {
                                    Entity passenger = passengers.get(i);
                                    passenger.moveTo(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, portalinfo.yRot, passenger.getXRot());

                                    if (passenger instanceof ServerPlayer player) {
                                        player.changeDimension(destLevel, new CustomTeleporter(destLevel));
                                        entity.addSittingPassenger(player, i);
                                    } else {
                                        if (!(passenger instanceof Player)) {
                                            passenger.changeDimension(destLevel, new CustomTeleporter(destLevel));
                                            entity.addSittingPassenger(passenger, i);
                                        }
                                    }
                                }


                                destLevel.addDuringTeleport(entity);
                                if (CSDimensionUtil.gravity(destLevel.dimensionTypeId()) == 0f){
                                    entity.disassemble();
                                }
                                else{
                                    entity.entityData.set(REENTRY_ENTITY_DATA_ACCESSOR,true);
                                }
                            }
                            return entity;
                        }); //Forge: End custom logic

                this.removeAfterChangingDimensions();
                this.level.getProfiler().pop();
                ((ServerLevel) this.level).resetEmptyTime();
                destLevel.resetEmptyTime();
                this.level.getProfiler().pop();
                return transportedEntity;
            }
        } else {
            return null;
        }
    }
    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        return localPos;
    }

    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        return localPos;
    }

    @Override
    protected StructureTransform makeStructureTransform() {
        return new StructureTransform(new BlockPos(getAnchorVec().add(.5, .5, .5)), 0, 0, 0);
    }

    @Override
    protected float getStalledAngle() {
        return 0;
    }

    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        setPosRaw(x, y, z);
        clientOffsetDiff = 0;
    }

    @Override
    public ContraptionRotationState getRotationState() {
        return ContraptionRotationState.NONE;
    }

    @Override
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
    }



    public boolean isReentry(){
        return this.entityData.get(REENTRY_ENTITY_DATA_ACCESSOR);
    }


    public static float getAcceleration(float initialMass, int trust, float gravity, boolean reentry) {
        if (!reentry) {
              float acceleration = (float) trust / initialMass;

            return (acceleration - gravity);
        } else {
            return -gravity;
        }
    }

    public void updateClientMotion() {

        Vec3 motion = new Vec3(0,(axisMotion + clientOffsetDiff/2f) * ServerSpeedProvider.get(),0);

        motion = VecHelper.clampComponentWise(motion, 1);
        setContraptionMotion(motion);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yw, float pt, int inc, boolean t) {}

    public double getAxisCoord() {
        Vec3 anchorVec = getAnchorVec();
        return  anchorVec.y;
    }
    public void sendPacket() {
        PacketInit.getChannel()
                .send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                        new RocketContraptionUpdatePacket(getId(),getAxisCoord(), axisMotion));
    }

    @OnlyIn(Dist.CLIENT)
    public static void handlePacket(RocketContraptionUpdatePacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.entityID);
        if (!(entity instanceof RocketContraptionEntity ce))
            return;
        ce.axisMotion = packet.motion;
        ce.clientOffsetDiff = packet.coord - ce.getAxisCoord();
    }
}
