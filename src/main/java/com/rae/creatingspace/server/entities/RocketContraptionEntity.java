package com.rae.creatingspace.server.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.ingameobject.EntityInit;
import com.rae.creatingspace.server.contraption.RocketContraption;
import com.rae.creatingspace.utilities.CSDimensionUtil;
import com.rae.creatingspace.utilities.CustomTeleporter;
import com.rae.creatingspace.utilities.data.FlightDataHelper;
import com.rae.creatingspace.utilities.data.codec.CSNBTUtil;
import com.rae.creatingspace.utilities.packet.RocketContraptionUpdatePacket;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.foundation.utility.Couple;
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
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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
    double clientOffsetDiff;
    double axisMotion;
    //float totalConsumedAmount;
    float totalTickTime;
    //float partialConsumedAmount = 0;
    HashMap<Couple<TagKey<Fluid>>, RocketContraption.ConsumptionInfo> theoreticalPerTagFluidConsumption;// to separate the fluids -> ratio of the engine ?
    HashMap<Couple<TagKey<Fluid>>, RocketContraption.ConsumptionInfo> realPerTagFluidConsumption;// to separate the fluids -> ratio of the engine ?

    HashMap<Couple<TagKey<Fluid>>,Couple<Float>> partialDrainAmountPerFluid = new HashMap<>();
    public BlockPos rocketEntryCoordinate = new BlockPos(0,0,0);
    public float totalTrust = 0;
    public float initialMass;
    private int propellantConsumption = 0;
    public ResourceKey<Level> originDimension = Level.OVERWORLD;
    public ResourceKey<Level> destination;
    private boolean disassembleOnFirstTick = false;
    public FlightDataHelper.RocketAssemblyData assemblyData;

    public HashMap<String,HashMap<TagKey<Fluid>, ArrayList<Fluid>>> consumableFluids = new HashMap<>(
                    Map.of("ox",new HashMap<>(), "fuel", new HashMap<>()));

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
        entity.theoreticalPerTagFluidConsumption = contraption.getTPTFluidConsumption();
        entity.realPerTagFluidConsumption = new HashMap<>();
        entity.consumableFluids = new HashMap<>(
                Map.of("ox",new HashMap<>(), "fuel", new HashMap<>()));;
        handelTrajectoryCalculation(entity);
        entity.totalTrust = contraption.getTrust();

        LOGGER.info("finishing setting up parameters");
        entity.noPhysics = false;
        return entity;
    }

    //put that in a rocket assembly helper class ?
    private static void handelTrajectoryCalculation(RocketContraptionEntity rocketContraptionEntity){
        RocketContraption contraption = (RocketContraption) rocketContraptionEntity.contraption;

        float deltaVNeeded = CSDimensionUtil.accessibleFrom(rocketContraptionEntity.originDimension)
                .get(rocketContraptionEntity.destination).deltaV();

        if (contraption==null){
            return;
        }
        float totalTrust =0;
        float inertFluidsMass= 0;
        IFluidHandler fluidHandler = contraption.getSharedFluidTanks();
        int nbrOfTank = fluidHandler.getTanks();

        float totalTheoreticalConsumption = 0;
        for (Couple<TagKey<Fluid>> combination: rocketContraptionEntity.theoreticalPerTagFluidConsumption.keySet()){

            TagKey<Fluid> consumedOx = combination.get(true);
            TagKey<Fluid> consumedFuel = combination.get(false);
            RocketContraption.ConsumptionInfo info = rocketContraptionEntity.theoreticalPerTagFluidConsumption.get(combination);
            //mean speed of ejected gasses for the fluid -> need to be done for a couple of tag -> ox/fuel

            totalTheoreticalConsumption += info.fuelConsumption()+info.oxConsumption();
            totalTrust += info.partialTrust();
            //initialise if not present

            addToConsumableFluids(rocketContraptionEntity, consumedOx,true);
            addToConsumableFluids(rocketContraptionEntity, consumedFuel,false);
        }
        float meanVe = totalTrust/totalTheoreticalConsumption;
        // massForEachPropellant is just to determine if there is enough fluid,
        // need to be called after the consumedFluids map is build
        HashMap<TagKey<Fluid>,Integer> massForEachPropellant =
                getMassMap(rocketContraptionEntity);


        for (int i=0 ; i < nbrOfTank; i++) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
            FluidType fluidType = fluidInTank.getFluid().getFluidType();

                inertFluidsMass += (float) (fluidInTank.getAmount() * fluidType.getDensity()) /1000;
        }
        float initialPropellantMass = 0;
        for (int mass : massForEachPropellant.values()){
            initialPropellantMass+=mass;
        }
        float emptyMass = inertFluidsMass + contraption.getDryMass();
        // to comment -> may need to calculate the deltaV of the rocket rather than
        // the amount of propellant consumed as the user will have that info
        // need testing -> can it be negative ?
        // yes if there isn't enough propellant
        // each propellant is making a contribution so it should appear here : need to write done the math...

        float finalPropellantMass = (float) ((emptyMass+initialPropellantMass)/Math.exp(deltaVNeeded/meanVe)-emptyMass);

        float consumedPropellantMass = initialPropellantMass - finalPropellantMass;


        //mean consumption -> make the consumption diff between CH4 and 02 -> adding H2 for advanced engine ?
        //a map of fluidTag/ Integer
        //should rather calculate the deltaV max ?
        rocketContraptionEntity.initialMass = emptyMass+initialPropellantMass;

        int distance = (int) (300 - rocketContraptionEntity.position().y());

        float gravity = CSDimensionUtil.gravity(rocketContraptionEntity.level.dimensionTypeId());

        float acceleration = totalTrust/(emptyMass+initialPropellantMass)-gravity;
        float perTickSpeed = getPerTickSpeed(acceleration);

        rocketContraptionEntity.totalTickTime = distance/perTickSpeed;

        //fill the real consumption map and fill the consumedMass map for mass verification
        HashMap<TagKey<Fluid>,Integer> consumedMassForEachPropellant = new HashMap<>();//just to determine if there is enough fluid

        for (Couple<TagKey<Fluid>> combination:rocketContraptionEntity.theoreticalPerTagFluidConsumption.keySet()) {
            RocketContraption.ConsumptionInfo info = rocketContraptionEntity.theoreticalPerTagFluidConsumption.get(combination);

            float theoreticalPartialConsumption = info.fuelConsumption()+info.oxConsumption();
            float ponderationCoef = theoreticalPartialConsumption/totalTheoreticalConsumption;
            float realPartialConsumption = ponderationCoef*consumedPropellantMass;
            //that's the consumed mass for the ensemble of engine with the same propellant combination

            //that's should be right
            float partialOx = realPartialConsumption*info.oxConsumption()/theoreticalPartialConsumption;
            float partialFuel = realPartialConsumption*info.fuelConsumption()/theoreticalPartialConsumption;

            rocketContraptionEntity.realPerTagFluidConsumption.put(combination,
                    new RocketContraption.ConsumptionInfo(
                            partialOx/rocketContraptionEntity.totalTickTime,
                            partialFuel/rocketContraptionEntity.totalTickTime,
                            info.partialTrust()));

            Integer prevOxValue = consumedMassForEachPropellant.get(combination.get(true));
            if (prevOxValue == null){
                prevOxValue = 0;
            }
            Integer prevFuelValue = consumedMassForEachPropellant.get(combination.get(false));
            if (prevFuelValue == null){
                prevFuelValue = 0;
            }
            consumedMassForEachPropellant.put(combination.get(true), (int) (prevOxValue+partialOx));
            consumedMassForEachPropellant.put(combination.get(false), (int) (prevFuelValue+partialFuel));

        }


        //verify if there is enough fluid
        FlightDataHelper.RocketAssemblyData assemblyData = FlightDataHelper.RocketAssemblyData.createFromPropellantMap(massForEachPropellant,consumedMassForEachPropellant,finalPropellantMass);
        rocketContraptionEntity.disassembleOnFirstTick = assemblyData.hasFailed();//just for the fluids

        //may need to put that on the RocketAssemblyData ( when doing the automatic rocket : 1.7 )
        if (acceleration <=0 ){
            rocketContraptionEntity.disassembleOnFirstTick = true;
        }
        if (distance<=0){
            rocketContraptionEntity.disassembleOnFirstTick = true;
        }
    }

    private static void addToConsumableFluids(RocketContraptionEntity rocketContraptionEntity, TagKey<Fluid> consumedFluid, boolean isOxPhase) {

        HashMap<TagKey<Fluid>, ArrayList<Fluid>> previousValue = rocketContraptionEntity.consumableFluids.get(isOxPhase?"ox":"fuel");
        if (previousValue ==null ){
            previousValue = new HashMap<>();
        }
        previousValue.put(consumedFluid,new ArrayList<>());
        rocketContraptionEntity.consumableFluids.put(isOxPhase?"ox":"fuel",previousValue);
        IFluidHandler fluidHandler = rocketContraptionEntity.contraption.getSharedFluidTanks();
        int nbrOfTank = fluidHandler.getTanks();

        for (int i = 0; i < nbrOfTank; i++) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
            if (fluidInTank.getFluid().is(consumedFluid)) {
                if (!rocketContraptionEntity.consumableFluids.get(isOxPhase?"ox":"fuel").get(consumedFluid).contains(fluidInTank.getFluid())) {
                    rocketContraptionEntity.consumableFluids.get(isOxPhase?"ox":"fuel").get(consumedFluid).add(fluidInTank.getFluid());
                }
            }
        }
    }
    private static HashMap<TagKey<Fluid>, Integer> getMassMap(RocketContraptionEntity rocketContraptionEntity ) {

        HashMap<TagKey<Fluid>, Integer> massForEachPropellant = new HashMap<>();
        ArrayList<TagKey<Fluid>> allPropellantTags = new ArrayList<>();
        allPropellantTags.addAll(rocketContraptionEntity.consumableFluids.get("ox").keySet());
        allPropellantTags.addAll(rocketContraptionEntity.consumableFluids.get("fuel").keySet());

        IFluidHandler fluidHandler = rocketContraptionEntity.contraption.getSharedFluidTanks();
        int nbrOfTank = fluidHandler.getTanks();

        for (TagKey<Fluid> consumedFluid:allPropellantTags){
            for (int i = 0; i < nbrOfTank; i++) {
                FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
                FluidType fluidType = fluidInTank.getFluid().getFluidType();
                if (fluidInTank.getFluid().is(consumedFluid)) {
                    Integer prevFluidMass = massForEachPropellant.get(consumedFluid);
                    if (prevFluidMass == null) {
                        prevFluidMass = 0;
                    }
                    float ro = (float) fluidType.getDensity() /1000;
                    massForEachPropellant.put(consumedFluid, (int) (prevFluidMass +
                                               fluidHandler.getFluidInTank(i).getAmount() * ro));

                }
            }
        }
        return massForEachPropellant;
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
   //adjust those two methode so it write and read the 3 new hashmap
    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnData) {
        super.readAdditional(compound, spawnData);
        this.totalTrust = compound.getFloat("trust");
        this.initialMass = compound.getFloat("initialMass");
        this.totalTickTime = compound.getFloat("totalTime");
        this.theoreticalPerTagFluidConsumption = CSNBTUtil.fromNBTtoMapInfo(compound.getCompound("theoreticalPerTagFluidConsumption"));
        this.realPerTagFluidConsumption = CSNBTUtil.fromNBTtoMapInfo(compound.getCompound("realPerTagFluidConsumption"));
        this.partialDrainAmountPerFluid = CSNBTUtil.fromNBTtoMapCouple(compound.getCompound("partialDrainAmountPerFluid"));
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
        for (Couple<TagKey<Fluid>> combination:realPerTagFluidConsumption.keySet()) {
            RocketContraptionEntity.addToConsumableFluids(this,combination.get(true),true);
            RocketContraptionEntity.addToConsumableFluids(this,combination.get(false),false);
        }


    }
    //make a codec ? should not be here


    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        compound.putInt("propellantConsumption", this.propellantConsumption);
        compound.putFloat("initialMass",this.initialMass);
        compound.putFloat("totalTime",this.totalTickTime);
        compound.put("theoreticalPerTagFluidConsumption", CSNBTUtil.fromMapInfoToNBT(this.theoreticalPerTagFluidConsumption));
        compound.put("realPerTagFluidConsumption", CSNBTUtil.fromMapInfoToNBT(this.realPerTagFluidConsumption));
        compound.put("partialDrainAmountPerFluid",CSNBTUtil.fromMapCoupleToNBT(this.partialDrainAmountPerFluid));

        compound.putFloat("trust",this.totalTrust);

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
                setContraptionMotion(Vec3.ZERO);//otherwise the player take damage
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
            if (!level.isClientSide) {
                setContraptionMotion(Vec3.ZERO);//otherwise the player take damage ? no
                disassemble();
            }
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

    @Override
    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

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
                (int) totalTrust,gravity,isReentry());

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
        //need to construct a map of drainAmount and partial drain -> map of couple/record(int,float)

        //make in a loop so it look for every one ?
        for (Couple<TagKey<Fluid>> combination:realPerTagFluidConsumption.keySet()) {
            RocketContraption.ConsumptionInfo info = realPerTagFluidConsumption.get(combination);
            Couple<Float> prevPartialDrainValue = partialDrainAmountPerFluid.get(combination);

            Fluid oxFluid = consumableFluids.get("ox").get(combination.get(true)).get(0);
            Fluid fuelFluid = consumableFluids.get("fuel").get(combination.get(false)).get(0);

            FluidType oxFluidType = oxFluid.getFluidType();
            float oxRo = (float) oxFluidType.getDensity() /1000;
            FluidType fuelFluidType = fuelFluid.getFluidType();
            float fuelRo = (float) fuelFluidType.getDensity() /1000;

            float oxAmount = info.oxConsumption()/oxRo; // oxConsumption in kg, oxRo in kg/mb
            float fuelAmount = info.fuelConsumption()/fuelRo;
            if (prevPartialDrainValue == null){
                prevPartialDrainValue = Couple.create(0f,0f);
            }
            float partialOxConsumedAmount = prevPartialDrainValue.get(true);
            partialOxConsumedAmount = partialOxConsumedAmount +  oxAmount - ((int)oxAmount);
            float partialFuelConsumedAmount = prevPartialDrainValue.get(false);
            partialFuelConsumedAmount = partialFuelConsumedAmount +  fuelAmount - ((int)fuelAmount);

            if (partialOxConsumedAmount>=1){
                oxAmount = oxAmount + 1;
                partialOxConsumedAmount = partialOxConsumedAmount -1;
            }
            if (partialFuelConsumedAmount>=1){
                fuelAmount = fuelAmount + 1;
                partialFuelConsumedAmount = partialFuelConsumedAmount -1;
            }
            partialDrainAmountPerFluid.put(combination,Couple.create(partialOxConsumedAmount,partialFuelConsumedAmount));


            int consumedOx = fluidHandler.drain(new FluidStack(oxFluid, (int) oxAmount), IFluidHandler.FluidAction.EXECUTE).getAmount();//drain ox
            int consumedFuel = fluidHandler.drain(new FluidStack(fuelFluid, (int) fuelAmount), IFluidHandler.FluidAction.EXECUTE).getAmount();//drain fuel

            if (consumedFuel == 0 || consumedOx == 0) {
                RocketContraptionEntity.addToConsumableFluids(this,combination.get(true),true);
                RocketContraptionEntity.addToConsumableFluids(this,combination.get(false),false);

            }
        }

    }
    //merge that with the static method ?

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
