package com.rae.creatingspace.server.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.rae.creatingspace.api.design.PropellantType;
import com.rae.creatingspace.api.squedule.RocketPath;
import com.rae.creatingspace.api.squedule.RocketScheduleRuntime;
import com.rae.creatingspace.init.EntityDataSerializersInit;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.ingameobject.EntityInit;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.rae.creatingspace.init.ingameobject.SoundInit;
import com.rae.creatingspace.server.contraption.RocketContraption;
import com.rae.creatingspace.utilities.CSDimensionUtil;
import com.rae.creatingspace.utilities.CSNBTUtil;
import com.rae.creatingspace.utilities.CustomTeleporter;
import com.rae.creatingspace.utilities.data.FlightDataHelper;
import com.rae.creatingspace.utilities.packet.RocketContraptionUpdatePacket;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.rae.creatingspace.init.ingameobject.SoundInit.ROCKET_LAUNCH;

public class RocketContraptionEntity extends AbstractContraptionEntity {
    //TODO make a way to automate rockets ( a special menu in the rocket controller + a path and actions
    // (spaceport block ? to define where the rocket will go)
    // for a normal rocket a path an action will be generated without the player knowing ?

    //TODO prevent the rocket from consuming fuel when world is loading ? correct gestion of client player loading
    // to avoid player falling out of the rocket ( do we force the player to be transported to where the rocket is
    // (it may move while the player is away)
    private static final Logger LOGGER = LogUtils.getLogger();
    double clientOffsetDiff;
    double speed;

    int soundTickCount = 0;

    int rocketSoundLength = 120;

    boolean shouldHandleCalculation = false;
    //inventory management
    // maybe we could make it simpler ?
    HashMap<PropellantType, RocketContraption.ConsumptionInfo> theoreticalPerTagFluidConsumption;// to separate the fluids -> ratio of the engine ?
    HashMap<PropellantType, RocketContraption.ConsumptionInfo> realPerTagFluidConsumption;// to separate the fluids -> ratio of the engine ?
    HashMap<TagKey<Fluid>, Float> partialDrainAmountPerFluid = new HashMap<>();
    //end of inventory management
    public static Codec<HashMap<PropellantType, RocketContraption.ConsumptionInfo>> CODEC_MAP_INFO = Codec.unboundedMap(
            PropellantTypeInit.PROPELLANT_TYPE.get().getCodec(),
            RocketContraption.ConsumptionInfo.CODEC
    ).xmap(HashMap::new, i -> i);
    public static Codec<HashMap<TagKey<Fluid>, Float>> CODEC_MAP_CONSUMPTION = Codec.unboundedMap(
            TagKey.codec(Registry.FLUID_REGISTRY),
            Codec.FLOAT
    ).xmap(HashMap::new, i -> i);
    public BlockPos rocketEntryCoordinate = new BlockPos(0,0,0);
    public float totalThrust = 0;
    public float initialMass;
    public ResourceLocation originDimension = Level.OVERWORLD.location();
    public ResourceLocation destination;
    private boolean failedToLaunch = false;
    private List<BlockPos> localPosOfFlightRecorders;

    public FlightDataHelper.RocketAssemblyData assemblyData;

    //TODO make a record and CODEC
    public HashMap<TagKey<Fluid>, ArrayList<Fluid>> consumableFluids = new HashMap<>();//
    private HashMap<String, BlockPos> initialPosMap;
    public RocketScheduleRuntime schedule;
    /*public static final EntityDataAccessor<Boolean> REENTRY_ENTITY_DATA_ACCESSOR =
            SynchedEntityData.defineId(RocketContraptionEntity.class, EntityDataSerializers.BOOLEAN);
    //used to now if the rocket need to move or not ( just assemble or finished the list of instruction)
    public static final EntityDataAccessor<Boolean> RUNNING_ENTITY_DATA_ACCESSOR =
            SynchedEntityData.defineId(RocketContraptionEntity.class, EntityDataSerializers.BOOLEAN);*/
    public static final EntityDataAccessor<RocketStatus> STATUS_DATA_ACCESSOR =
            SynchedEntityData.defineId(RocketContraptionEntity.class, EntityDataSerializersInit.STATUS_SERIALIZER);


    //initializing and saving methods

    //make the launch after the assembling of the rocket.
    public RocketContraptionEntity(EntityType<?> type, Level level) {
        super(type, level);
        schedule = new RocketScheduleRuntime(this);
    }

    public static RocketContraptionEntity create(Level level, RocketContraption contraption, ResourceLocation destination) {
        RocketContraptionEntity entity =
                new RocketContraptionEntity(EntityInit.ROCKET_CONTRAPTION.get(), level);
        entity.originDimension = level.dimension().location();
        entity.destination = destination;//will be set after the

        entity.setContraption(contraption);
        entity.theoreticalPerTagFluidConsumption = contraption.getTPTFluidConsumption();
        entity.realPerTagFluidConsumption = new HashMap<>();
        entity.consumableFluids = new HashMap<>();
        entity.totalThrust = contraption.getThrust();
        entity.localPosOfFlightRecorders = contraption.getLocalPosOfFlightRecorders();
        entity.noPhysics = false;
        LOGGER.info("finishing setting up parameters");
        return entity;
    }

    //put that in a rocket assembly helper class ?
    //TODO put every static method into a helper class ( make an api ?)
    //TODO make a python program that does that for testing purpose
    /**
     * should only be used on the server
     */
    public static void handelTrajectoryCalculation(@NotNull RocketContraptionEntity rocketContraptionEntity) {
        //System.out.println(rocketContraptionEntity.deltaV());

        RocketContraption contraption = (RocketContraption) rocketContraptionEntity.contraption;

        float deltaVNeeded = CSDimensionUtil.cost(rocketContraptionEntity.originDimension, rocketContraptionEntity.destination);

        if (contraption==null){
            return;
        }
        float totalThrust =0;
        float inertFluidsMass= 0;
        IFluidHandler fluidHandler = contraption.getSharedFluidTanks();
        int nbrOfTank = fluidHandler.getTanks();
        //both research of every consumable fluid and addition of the total consumption
        float totalTheoreticalConsumption = 0;
        //TODO that could be in the inventory manager of the rocket -> 1.8
        for (PropellantType combination : rocketContraptionEntity.theoreticalPerTagFluidConsumption.keySet()) {
            RocketContraption.ConsumptionInfo info = rocketContraptionEntity.theoreticalPerTagFluidConsumption.get(combination);
            //mean speed of ejected gasses for the fluid -> need to be done for a couple of tag -> ox/fuel
            for (float consumption :
                    info.propellantConsumption().values()) {
                totalTheoreticalConsumption += consumption;
            }
            totalThrust += info.partialThrust();
            //initialise if not present
            for (TagKey<Fluid> fluid :
                    combination.getPropellantRatio().keySet()) {
                addToConsumableFluids(rocketContraptionEntity, fluid);
            }
        }

        float meanVe = totalThrust/totalTheoreticalConsumption;
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
        // each propellant is making a contribution, so it should appear here : need to write done the math...

        float finalPropellantMass = (float) ((emptyMass+initialPropellantMass)/Math.exp(deltaVNeeded/meanVe)-emptyMass);

        float consumedPropellantMass = initialPropellantMass - finalPropellantMass;


        //mean consumption -> make the consumption diff between CH4 and 02 -> adding H2 for advanced engine ?
        //a map of fluidTag/ Integer
        //should rather calculate the deltaV max ?
        rocketContraptionEntity.initialMass = emptyMass+initialPropellantMass;

        int distance = (int) (300 - rocketContraptionEntity.position().y());

        float gravity = CSDimensionUtil.gravity(rocketContraptionEntity.level.dimensionTypeId().location());

        float acceleration = totalThrust/(emptyMass+initialPropellantMass)-gravity;
        float perTickSpeed = getPerTickSpeed(acceleration);

        float totalTickTime = distance / perTickSpeed;

        //fill the real consumption map and fill the consumedMass map for mass verification
        HashMap<TagKey<Fluid>,Integer> consumedMassForEachPropellant = new HashMap<>();//just to determine if there is enough fluid

        for (PropellantType propellantType : rocketContraptionEntity.theoreticalPerTagFluidConsumption.keySet()) {
            RocketContraption.ConsumptionInfo info = rocketContraptionEntity.theoreticalPerTagFluidConsumption.get(propellantType);

            float theoreticalPartialConsumption = 0;
            for (float consumption :
                    info.propellantConsumption().values()) {
                theoreticalPartialConsumption += consumption;
            }

            float ponderationCoef = theoreticalPartialConsumption/totalTheoreticalConsumption;
            float realPartialConsumption = ponderationCoef*consumedPropellantMass;
            //that's the consumed mass for the ensemble of engine with the same propellant combination
            HashMap<TagKey<Fluid>, Float> correctedConsumptions = new HashMap<>(info.propellantConsumption());
            RocketContraption.multiplyMap(correctedConsumptions, realPartialConsumption / theoreticalPartialConsumption / totalTickTime);

            rocketContraptionEntity.realPerTagFluidConsumption.put(propellantType,
                    new RocketContraption.ConsumptionInfo(
                            correctedConsumptions,
                            info.partialThrust()));
            for (TagKey<Fluid> fluid :
                    correctedConsumptions.keySet()) {
                Integer prevValue = consumedMassForEachPropellant.getOrDefault(fluid, 0);
                consumedMassForEachPropellant.put(fluid, (int) (prevValue + correctedConsumptions.get(fluid)));

            }

        }


        //verify if there is enough fluid
        FlightDataHelper.RocketAssemblyData assemblyData =
                FlightDataHelper.RocketAssemblyData.create(
                        massForEachPropellant,
                        consumedMassForEachPropellant,
                        finalPropellantMass,
                        totalThrust,
                        (emptyMass+initialPropellantMass)*gravity);
        rocketContraptionEntity.assemblyData = assemblyData;
        rocketContraptionEntity.failedToLaunch = assemblyData.hasFailed();//just for the fluids

        //may need to put that on the RocketAssemblyData ( when doing the automatic rocket : 1.7 )
        if (acceleration <=0 ){
            rocketContraptionEntity.failedToLaunch = true;
            rocketContraptionEntity.getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.BLOCKED);
        }
        if (distance<=0){
            rocketContraptionEntity.failedToLaunch = true;
            rocketContraptionEntity.getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.BLOCKED);
            return;
        }
        rocketContraptionEntity.getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.TRAVELING);
        /*System.out.println("consumableFluids : " + rocketContraptionEntity.consumableFluids);
        System.out.println("theoreticalPerTagFluidConsumption: " + rocketContraptionEntity.theoreticalPerTagFluidConsumption);
        System.out.println("realPerTagFluidConsumption: " + rocketContraptionEntity.realPerTagFluidConsumption);*/
    }

    //the rocket kill itself upon arrival in other dim
    public float deltaV() {
        //wrong because no consideration for the ratio of propellants
        float totalThrust = 0;
        float inertFluidsMass = 0;
        IFluidHandler fluidHandler = contraption.getSharedFluidTanks();
        int nbrOfTank = fluidHandler.getTanks();
        //both research of every consumable fluid and addition of the total consumption
        float totalTheoreticalConsumption = 0;
        //TODO that could be in the inventory manager of the rocket -> 1.8
        for (PropellantType combination : this.theoreticalPerTagFluidConsumption.keySet()) {
            RocketContraption.ConsumptionInfo info = this.theoreticalPerTagFluidConsumption.get(combination);
            //mean speed of ejected gasses for the fluid -> need to be done for a couple of tag -> ox/fuel
            for (float consumption :
                    info.propellantConsumption().values()) {
                totalTheoreticalConsumption += consumption;
            }
            totalThrust += info.partialThrust();
            //initialise if not present
            for (TagKey<Fluid> fluid :
                    combination.getPropellantRatio().keySet()) {
                addToConsumableFluids(this, fluid);
            }
        }

        float meanVe = totalThrust > 0 ? totalThrust / totalTheoreticalConsumption : 0;
        // massForEachPropellant is just to determine if there is enough fluid,
        // need to be called after the consumedFluids map is build
        HashMap<TagKey<Fluid>, Integer> massForEachPropellant =
                getMassMap(this);


        for (int i = 0; i < nbrOfTank; i++) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
            FluidType fluidType = fluidInTank.getFluid().getFluidType();

            inertFluidsMass += (float) (fluidInTank.getAmount() * fluidType.getDensity()) / 1000;
        }
        float initialPropellantMass = 0;
        for (int mass : massForEachPropellant.values()) {
            initialPropellantMass += mass;
        }
        float emptyMass = inertFluidsMass + ((RocketContraption) contraption).getDryMass();
        return (float) (meanVe * Math.log((emptyMass + initialPropellantMass) / (emptyMass)));
    }
    private static void addToConsumableFluids(RocketContraptionEntity rocketContraptionEntity, TagKey<Fluid> consumedFluid) {
        rocketContraptionEntity.consumableFluids.put(consumedFluid, new ArrayList<>());
        IFluidHandler fluidHandler = rocketContraptionEntity.contraption.getSharedFluidTanks();
        int nbrOfTank = fluidHandler.getTanks();

        for (int i = 0; i < nbrOfTank; i++) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
            if (fluidInTank.getFluid().is(consumedFluid)) {
                if (!rocketContraptionEntity.consumableFluids.get(consumedFluid).contains(fluidInTank.getFluid())) {
                    rocketContraptionEntity.consumableFluids.get(consumedFluid).add(fluidInTank.getFluid());
                }
            }
        }
    }
    private static HashMap<TagKey<Fluid>, Integer> getMassMap(RocketContraptionEntity rocketContraptionEntity ) {

        HashMap<TagKey<Fluid>, Integer> massForEachPropellant = new HashMap<>();
        ArrayList<TagKey<Fluid>> allPropellantTags = new ArrayList<>();
        //remove the string from the consumableFluids
        allPropellantTags.addAll(rocketContraptionEntity.consumableFluids.keySet());
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

    // used to know if the rocket is going up or down
    @Override
    public void disassemble() {
        //doesn't work with create_interactive
        for (BlockPos localPos:this.localPosOfFlightRecorders){
            StructureTemplate.StructureBlockInfo oldStructureInfo = this.contraption.getBlocks().get(localPos);
            CompoundTag nbt = oldStructureInfo.nbt;
            nbt.put("lastAssemblyData",FlightDataHelper.RocketAssemblyData.toNBT(this.assemblyData));
            StructureTemplate.StructureBlockInfo newStructureInfo =
                    new StructureTemplate.StructureBlockInfo(oldStructureInfo.pos,oldStructureInfo.state,nbt);
            this.contraption.getBlocks().put(localPos,newStructureInfo);
        }
        super.disassemble();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        //this.entityData.define(REENTRY_ENTITY_DATA_ACCESSOR,false);
        //this.entityData.define(RUNNING_ENTITY_DATA_ACCESSOR, false);
        this.entityData.define(STATUS_DATA_ACCESSOR, RocketStatus.IDLE);
    }
    @Override
    public void tick() {
        if (soundTickCount == 0) {
            playSound(RegistryObject.create(new ResourceLocation("creating_space", "rocket_launch_sound"), ForgeRegistries.SOUND_EVENTS).get(), 1, 1);
        }
        soundTickCount++;
        if (soundTickCount == rocketSoundLength) {
            playSound(RegistryObject.create(new ResourceLocation("creating_space", "rocket_launch_sound"), ForgeRegistries.SOUND_EVENTS).get(), 1, 1);
            soundTickCount = 0;
        }

        //movement is bugged when in ground -> avoid collision by slowing down upon landing ? or breaking blocks
        boolean wasRunning = isInPropulsionPhase();
        if (isInPropulsionPhase()) {
            //put the handleTrajectory calculation on the start path
            if (!level.isClientSide() && shouldHandleCalculation) {
                //so the pos is initialized
                handelTrajectoryCalculation(this);
                shouldHandleCalculation = false;
            }
        }
        schedule.tick(level);
        super.tick();
        if (wasRunning && !isInPropulsionPhase()) {
            schedule.destinationReached();
        }
    }

    @Override
    protected void tickContraption() {
        if (!(contraption instanceof RocketContraption))
            return;
        //TODO make schedule for rocket
        if (failedToLaunch) {//happens when fail to have enough fuel
            getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.BLOCKED);
            //disassemble();
            return;
        }

        if (level.isClientSide) {
            clientOffsetDiff *= .75f;
            updateClientMotion();
        }

        tickActors();
        if (isInPropulsionPhase() && !level.isClientSide) {
            tickConsumptionAndSpeed();
            Vec3 movementVec = getDeltaMovement();
            if (!level.isClientSide) tickDimensionChangeLogic();


            if (ContraptionCollider.collideBlocks(this)) {
                if (!level.isClientSide) {
                    //stopRocket();
                    getEntityData().set(STATUS_DATA_ACCESSOR, isReentry() ? RocketStatus.IDLE : RocketStatus.BLOCKED);
                    setContraptionMotion(Vec3.ZERO);
                    //disassemble();
                    return;
                }
            }
            if (tickCount > 2 && isInPropulsionPhase()) {
                movementVec = VecHelper.clampComponentWise(movementVec, (float) 1);
                move(movementVec.x, movementVec.y, movementVec.z);
            }

        /*if (Math.signum(prevAxisMotion) != Math.signum(axisMotion) && prevAxisMotion != 0)
            contraption.stop(level);*/
        }
        if (!isInPropulsionPhase()) {
            setContraptionMotion(Vec3.ZERO);
        }
        if (!level.isClientSide)
            sendPacket();
    }

    @Override
    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource damageSource) {
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


            ServerLevel destServerLevel = this.level.getServer().getLevel(
                    ResourceKey.create(Registry.DIMENSION_REGISTRY,
                            this.destination)
            );

            if (destServerLevel!=null /*&& level.dimension() == this.originDimension*/) {

                this.changeDimension(destServerLevel,new CustomTeleporter(destServerLevel));
            }
            else {
                //put an error log with the exception thrown
                LOGGER.info("dimension change failed at first step");
                LOGGER.info("rocket info :");
                LOGGER.info("destination :" + destServerLevel);
                LOGGER.info("current dimension :" + level.dimension());
                LOGGER.info("origin Dimension : " + this.originDimension);
                LOGGER.info("gravity of current dimension" + CSDimensionUtil.gravity(this.level.dimensionTypeId().location()));
            }
        }
    }
    protected void tickConsumptionAndSpeed() {
        if (level.isClientSide())
            return;

        float gravity = CSDimensionUtil.gravity(this.level.dimensionTypeId().location());

        if (!isReentry() ){
            if (!level.isClientSide())
                consumePropellant(this);
        }

        Vec3 movementVec;
        float acceleration = getAcceleration(
                initialMass,
                (int) totalThrust,gravity,isReentry());

        float speed = getPerTickSpeed(acceleration);
        movementVec = new Vec3(0,speed,0);

        this.speed = speed;
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
        for (PropellantType combination : realPerTagFluidConsumption.keySet()) {
            RocketContraption.ConsumptionInfo info = realPerTagFluidConsumption.get(combination);


            //temporary fix : don't consume if the list is empty. It should never happen though
            for (TagKey<Fluid> fluidTag :
                    info.propellantConsumption().keySet()) {
                Float prevPartialDrainValue = partialDrainAmountPerFluid.get(fluidTag);
                ArrayList<Fluid> fluids = consumableFluids.get(fluidTag);
                //ArrayList<Fluid> fuelFluids = consumableFluids.get("fuel").get(combination.get(false));
                if (!(fluids == null || fluids.isEmpty() /*|| fuelFluids == null || fuelFluids.isEmpty())*/)) {

                    Fluid oxFluid = fluids.get(0);
                    //Fluid fuelFluid = fuelFluids.get(0);

                    FluidType oxFluidType = oxFluid.getFluidType();
                    float oxRo = (float) oxFluidType.getDensity() / 1000;
                    //FluidType fuelFluidType = fuelFluid.getFluidType();
                    //float fuelRo = (float) fuelFluidType.getDensity() / 1000;

                    float oxAmount = info.propellantConsumption().get(fluidTag) / oxRo; // oxConsumption in kg, oxRo in kg/mb
                    //float fuelAmount = info.fuelConsumption() / fuelRo;
                    if (prevPartialDrainValue == null) {
                        prevPartialDrainValue = 0f;
                    }
                    float partialOxConsumedAmount = prevPartialDrainValue;
                    partialOxConsumedAmount = partialOxConsumedAmount + oxAmount - ((int) oxAmount);
                    //float partialFuelConsumedAmount = prevPartialDrainValue.get(false);
                    //partialFuelConsumedAmount = partialFuelConsumedAmount + fuelAmount - ((int) fuelAmount);

                    if (partialOxConsumedAmount >= 1) {
                        oxAmount = oxAmount + 1;
                        partialOxConsumedAmount = partialOxConsumedAmount - 1;
                    }
                    /*if (partialFuelConsumedAmount >= 1) {
                        fuelAmount = fuelAmount + 1;
                        partialFuelConsumedAmount = partialFuelConsumedAmount - 1;
                    }*/
                    partialDrainAmountPerFluid.put(fluidTag, partialOxConsumedAmount);


                    int consumedOx = fluidHandler.drain(new FluidStack(oxFluid, (int) oxAmount), IFluidHandler.FluidAction.EXECUTE).getAmount();//drain ox
                    //int consumedFuel = fluidHandler.drain(new FluidStack(fuelFluid, (int) fuelAmount), IFluidHandler.FluidAction.EXECUTE).getAmount();//drain fuel

                    if (consumedOx == 0) {
                        RocketContraptionEntity.addToConsumableFluids(this, fluidTag);
                        //RocketContraptionEntity.addToConsumableFluids(this, combination.get(false), false);

                    }
                }
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
            List<Entity> collidingEntities = level.getEntities(this, this.getBoundingBox());
            collidingEntities.removeAll(passengers);
            this.unRide();
            BlockPos previousRocketPos = this.getOnPos();
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
                                //adding previously riding passengers and collidingEntities ( separated, so they keep riding when arriving)
                                for (int i = 0; i < passengers.size(); i++) {
                                    Entity passenger = passengers.get(i);
                                    passenger.moveTo(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, passenger.getYRot(), passenger.getXRot());

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
                                for (int i = 0; i < collidingEntities.size(); i++) {
                                    Entity movedEntity = collidingEntities.get(i);
                                    BlockPos posDif = movedEntity.getOnPos().subtract(previousRocketPos);
                                    movedEntity.moveTo(portalinfo.pos.x + posDif.getX(), portalinfo.pos.y + posDif.getY(), portalinfo.pos.z + posDif.getZ(), movedEntity.getYRot(), movedEntity.getXRot());

                                    if (movedEntity instanceof ServerPlayer player) {
                                        player.changeDimension(destLevel, new CustomTeleporter(destLevel));
                                    } else {
                                        if (!(movedEntity instanceof Player)) {
                                            movedEntity.changeDimension(destLevel, new CustomTeleporter(destLevel));
                                        }
                                    }
                                }

                                destLevel.addDuringTeleport(entity);
                                if (CSDimensionUtil.gravity(destLevel.dimensionTypeId().location()) == 0f) {
                                    //entity.disassemble();
                                    entity.stopRocket();
                                    System.out.println("going to : " + destination);
                                }
                                else{
                                    entity.entityData.set(STATUS_DATA_ACCESSOR, RocketStatus.ON_FINAL);
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


    public static float getAcceleration(float initialMass, int thrust, float gravity, boolean reentry) {
        if (!reentry) {
              float acceleration = (float) thrust / initialMass;
            return (acceleration - gravity);
        } else {
            return -gravity;
        }
    }

    //network and client only

    @Override
    public AABB getBoundingBoxForCulling() {
        return isInPropulsionPhase() ?
                new AABB(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE,
                        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE) :
                super.getBoundingBoxForCulling();
    }

    //that's for knowing what direction the rocket is facing ? -> look for Create's elevator
    public double getAxisCoord() {
        Vec3 anchorVec = getAnchorVec();
        return  anchorVec.y;
    }

    //only works if the rocket is moving straight up or down
    public void updateClientMotion() {
        Vec3 motion = new Vec3(0, (speed + clientOffsetDiff / 2f) * ServerSpeedProvider.get(), 0);

        motion = VecHelper.clampComponentWise(motion, 1);
        setContraptionMotion(motion);
    }
    public void sendPacket() {
        PacketInit.getChannel()
                .send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                        new RocketContraptionUpdatePacket(getId(),getAxisCoord(), speed));
    }

    @OnlyIn(Dist.CLIENT)
    public static void handlePacket(RocketContraptionUpdatePacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.entityID);
        if (!(entity instanceof RocketContraptionEntity ce))
            return;
        ce.speed = packet.speed;
        ce.clientOffsetDiff = packet.coord - ce.getAxisCoord();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
    }

    /**
     * necessary to avoid "vibration" the  updateClientMotion() is used to sync client to server entity
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yw, float pt, int inc, boolean t) {
    }

    //saving and getters
    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnData) {
        super.readAdditional(compound, spawnData);
        this.initialPosMap = getPosMap((CompoundTag) compound.get("initialPosMap"));
        this.localPosOfFlightRecorders = CSNBTUtil.LongsToBlockPos(compound.getLongArray("localPosOfFlightRecorders"));//to remove
        this.totalThrust = compound.getFloat("thrust");
        this.initialMass = compound.getFloat("initialMass");
        this.theoreticalPerTagFluidConsumption = CODEC_MAP_INFO.parse(NbtOps.INSTANCE, compound.getCompound("theoreticalPerTagFluidConsumption")).result().orElse(new HashMap<>());
        this.realPerTagFluidConsumption = CODEC_MAP_INFO.parse(NbtOps.INSTANCE, compound.getCompound("realPerTagFluidConsumption")).result().orElse(new HashMap<>());
        this.partialDrainAmountPerFluid = CODEC_MAP_CONSUMPTION.parse(NbtOps.INSTANCE, compound.getCompound("partialDrainAmountPerFluid")).result().orElse(new HashMap<>());
        this.assemblyData = FlightDataHelper.RocketAssemblyData.fromNBT(compound.getCompound("assemblyData"));
        this.failedToLaunch = assemblyData.hasFailed();
        //this.entityData.set(REENTRY_ENTITY_DATA_ACCESSOR,compound.getBoolean("reentry"));
        //this.entityData.set(RUNNING_ENTITY_DATA_ACCESSOR, compound.getBoolean("running"));
        this.entityData.set(STATUS_DATA_ACCESSOR, RocketStatus.valueOf(compound.getString("status")));
        this.destination = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, compound.get("destination")).get().orThrow();

        this.originDimension =
                ResourceLocation.CODEC.parse(NbtOps.INSTANCE, compound.get("origin")).get().orThrow();
        this.schedule.read((CompoundTag) compound.get("Runtime"));
        for (PropellantType combination : realPerTagFluidConsumption.keySet()) {
            for (TagKey<Fluid> fluid :
                    combination.getPropellantRatio().keySet()) {
                RocketContraptionEntity.addToConsumableFluids(this, fluid);

            }
        }
    }

    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        compound.put("initialPosMap", putPosMap(this.initialPosMap, new CompoundTag()));
        compound.putLongArray("localPosOfFlightRecorders", CSNBTUtil.BlockPosToLong(this.localPosOfFlightRecorders));//to remove
        compound.putFloat("initialMass", this.initialMass);
        compound.put("theoreticalPerTagFluidConsumption", CODEC_MAP_INFO.encodeStart(NbtOps.INSTANCE, this.theoreticalPerTagFluidConsumption).get().left().orElse(new CompoundTag()));
        compound.put("realPerTagFluidConsumption", CODEC_MAP_INFO.encodeStart(NbtOps.INSTANCE, this.realPerTagFluidConsumption).get().left().orElse(new CompoundTag()));
        compound.put("partialDrainAmountPerFluid", CODEC_MAP_CONSUMPTION.encodeStart(NbtOps.INSTANCE, this.partialDrainAmountPerFluid).get().left().orElse(new CompoundTag()));

        compound.put("assemblyData", FlightDataHelper.RocketAssemblyData.toNBT(this.assemblyData));
        compound.putFloat("thrust", this.totalThrust);

        //compound.putBoolean("reentry",isReentry());
        //compound.putBoolean("running",isInPropulsionPhase());
        compound.putString("status", this.entityData.get(STATUS_DATA_ACCESSOR).toString());
        compound.put("origin", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.originDimension).get().orThrow());
        compound.put("destination", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.destination).get().orThrow());
        compound.put("Runtime", schedule.write());

        super.writeAdditional(compound, spawnPacket);
    }

    public boolean isReentry() {
        return this.entityData.get(STATUS_DATA_ACCESSOR) == RocketStatus.ON_FINAL;
    }

    public boolean isInPropulsionPhase() {
        return this.entityData.get(STATUS_DATA_ACCESSOR).propelled_phase;
    }

    public static CompoundTag putPosMap(HashMap<String, BlockPos> initialPosMap, CompoundTag compound) {
        if (compound == null) {
            compound = new CompoundTag();
        }
        for (String key : initialPosMap.keySet()) {
            compound.putLong("dimensionInitialPosOf:" + key, initialPosMap.get(key).asLong());
        }

        return compound;
    }

    public static HashMap<String, BlockPos> getPosMap(CompoundTag compound) {
        HashMap<String, BlockPos> initialPosMap = new HashMap<>();

        if (compound != null) {
            for (String key : compound.getAllKeys()) {
                if (key.contains("dimensionInitialPosOf:")) {
                    initialPosMap.put(
                            key.substring(22),
                            BlockPos.of(compound.getLong(key)));
                }
            }
        }

        return initialPosMap;
    }
    public void setShouldHandleCalculation(boolean shouldHandleCalculation) {
        this.shouldHandleCalculation = shouldHandleCalculation;
    }
    public void setAccessibilityData(HashMap<String, BlockPos> initialPosMap) {
        this.initialPosMap = initialPosMap;
    }

    public HashMap<String, BlockPos> getInitialPosMap() {
        return initialPosMap;
    }

    //navigation part (schedule)
    public void successfulNavigation() {
        System.out.println("rocket found a path and has enough fuel");
    }

    public int countPlayerPassengers() {
        AtomicInteger count = new AtomicInteger();
        getIndirectPassengers()
                .forEach(p -> {
                    if (p instanceof Player)
                        count.incrementAndGet();
                });
        return count.intValue();
    }

    public int startNavigation(RocketPath nextPath) {
        if (!level.isClientSide()) {
            //so the pos is initialized
            this.originDimension = nextPath.origin;
            this.destination = nextPath.destination;
            //getEntityData().set(RUNNING_ENTITY_DATA_ACCESSOR, true);
            getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.TRAVELING);

            shouldHandleCalculation = false;
            handelTrajectoryCalculation(this);
            //shouldHandleCalculation = false;
        }
        //fail according to handleTrajectoryCalculation ?
        return 0;
    }

    private void stopRocket() {
        System.out.println("previous status" + getEntityData().get(STATUS_DATA_ACCESSOR));
        //getEntityData().set(RUNNING_ENTITY_DATA_ACCESSOR, false);
        //getEntityData().set(REENTRY_ENTITY_DATA_ACCESSOR, false);
        getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.IDLE);
        setContraptionMotion(Vec3.ZERO);
    }

    public enum RocketStatus {
        //replacement for the RUNNING_ENTITY_DATA_ACCESSOR and REENTRY_ENTITY_DATA_ACCESSOR
        IDLE(false),
        TRAVELING(true),//going up to the next dimension
        BLOCKED(false),//used when physically blocked and when not enough fuel, -> separate into several cases ?
        ON_FINAL(true);
        final boolean propelled_phase;

        RocketStatus(boolean propelled_phase) {
            this.propelled_phase = propelled_phase;
        }
    }
}
