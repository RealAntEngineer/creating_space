package com.rae.creatingspace.content.rocket.contraption.entity;

import com.mojang.serialization.Codec;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.contraption.Synced2AxisContraptionEntity;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.rocket.RocketMenu;
import com.rae.creatingspace.content.rocket.RocketTeleporter;
import com.rae.creatingspace.content.rocket.squedule.RocketPath;
import com.rae.creatingspace.content.rocket.squedule.RocketScheduleRuntime;
import com.rae.creatingspace.init.EntityDataSerializersInit;
import com.rae.creatingspace.init.ingameobject.EntityInit;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import com.rae.creatingspace.legacy.utilities.CSNBTUtil;
import com.rae.creatingspace.content.rocket.flight_recorder.FlightDataHelper;
import com.simibubi.create.content.contraptions.*;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.rae.creatingspace.init.ingameobject.SoundInit.ROCKET_LAUNCH;

public class RocketContraptionEntity extends Synced2AxisContraptionEntity {
    //TODO make a way to automate rockets ( a special menu in the rocket controller + a path and actions
    // (spaceport block ? to define where the rocket will go)
    // for a normal rocket a path an action will be generated without the player knowing ?

    //TODO prevent the rocket from consuming fuel when world is loading ? correct gestion of client player loading
    // to avoid player falling out of the rocket ( do we force the player to be transported to where the rocket is
    // (it may move while the player is away)
    private static final Logger LOGGER = LoggerFactory.getLogger("CS/RocketContraptionEntity");
    int soundEffectTickCount = 0;
    static int ROCKET_SOUND_LENGTH = 35;
    //inventory management
    // maybe we could make it simpler ?
    HashMap<TagKey<Fluid>, RocketContraption.ConsumptionInfo> realPerTagFluidConsumption;// to separate the fluids -> ratio of the engine ?
    HashMap<TagKey<Fluid>, Float> partialDrainAmountPerFluid = new HashMap<>();
    //end of inventory management
    public static Codec<HashMap<TagKey<Fluid>, RocketContraption.ConsumptionInfo>> CODEC_MAP_INFO = RocketContraption.TPTF_CODEC
            .xmap(HashMap::new, i -> i);
    public static Codec<HashMap<TagKey<Fluid>, Float>> CODEC_MAP_CONSUMPTION = Codec.unboundedMap(
            TagKey.codec(Registries.FLUID),
            Codec.FLOAT
    ).xmap(HashMap::new, i -> i);
    public float totalThrust = 0;//maybe always use the value of the contraption
    public float initialMass;
    private List<BlockPos> localPosOfFlightRecorders;//will be transferred to the rocket controls, or it's equivalent
    public FlightDataHelper.RocketAssemblyData assemblyData;//this is shit, we need the freaking inventory manager
    //TODO make a record and CODEC
    public HashMap<TagKey<Fluid>, ArrayList<Fluid>> consumableFluids = new HashMap<>();//should be inside RocketStorageManager
    public RocketScheduleRuntime schedule;
    public static final EntityDataAccessor<RocketStatus> STATUS_DATA_ACCESSOR =
            SynchedEntityData.defineId(RocketContraptionEntity.class, EntityDataSerializersInit.STATUS_SERIALIZER);
    private static final EntityDataAccessor<Direction> INITIAL_ORIENTATION =
            SynchedEntityData.defineId(RocketContraptionEntity.class, EntityDataSerializers.DIRECTION);
    public RocketPath nextPath;
    private ControlMode controlMode = ControlMode.DOCKING;

    //rotations

    //initializing and saving methods
    //make the launch after the assembling of the rocket.
    public RocketContraptionEntity(EntityType<?> type, Level level) {
        super(type, level);
        schedule = new RocketScheduleRuntime(this);
    }
    public static RocketContraptionEntity create(Level level, RocketContraption contraption, Direction initialOrientation) {
        RocketContraptionEntity entity =
                new RocketContraptionEntity(EntityInit.ROCKET_CONTRAPTION.get(), level);
        entity.setContraption(contraption);
        entity.realPerTagFluidConsumption = new HashMap<>();
        entity.consumableFluids = new HashMap<>();
        entity.totalThrust = contraption.getThrust();
        entity.localPosOfFlightRecorders = contraption.getLocalPosOfFlightRecorders();
        entity.noPhysics = false;
        entity.setInitialOrientation(initialOrientation);
        entity.startAtInitialYaw();
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

        float deltaVNeeded = (float) rocketContraptionEntity.nextPath.cost;
        if (CSConfigs.COMMON.additionalLogInfo.get()){
            CreatingSpace.LOGGER.info("-------------------trajectory calculation---------------------");
        }
        if (CSConfigs.COMMON.additionalLogInfo.get()){
            CreatingSpace.LOGGER.info("initial delta V prevision{}", contraption.getStorage().getCurrentDeltaV());

        }

        if (contraption==null){
            CreatingSpace.LOGGER.warn("no contraption, aborting calculation");
            return;
        }

        //both research of every consumable fluid and addition of the total consumption

        //TODO that could be in the inventory manager of the rocket -> 1.8
        //hmmmm, maybe do that elsewhere ?
        float totalThrust =rocketContraptionEntity.totalThrust;
        for (TagKey<Fluid> fluid  : ((RocketContraption)rocketContraptionEntity.contraption).getTPTFluidConsumption().keySet()) {
            searchForFluid(rocketContraptionEntity, fluid);
        }


        float meanVe = ((RocketContraption) rocketContraptionEntity.contraption).getStorage().getMeanVe();
        float totalTheoreticalConsumption = totalThrust / meanVe;
        if (CSConfigs.COMMON.additionalLogInfo.get()){
            CreatingSpace.LOGGER.info("finished propellants loading pass, result :");
            CreatingSpace.LOGGER.info("thrust : {}N", totalThrust);
            CreatingSpace.LOGGER.info("total theoretical consumption : {} Kg/s", totalTheoreticalConsumption);
            CreatingSpace.LOGGER.info("mean exhaust velocity {} m/s", meanVe);
            CreatingSpace.LOGGER.info("consumable fluid found in rocket : {}", rocketContraptionEntity.consumableFluids);
        }

        // massForEachPropellant is just to determine if there is enough fluid,
        // need to be called after the consumedFluids map is build
        HashMap<TagKey<Fluid>,Integer> massForEachPropellant = getMassMap(rocketContraptionEntity);
        float initialPropellantMass = contraption.getStorage().getPropellantMass();

        float emptyMass = contraption.getStorage().getInertFluidMass() + contraption.getDryMass();
        if (CSConfigs.COMMON.additionalLogInfo.get()) {
            CreatingSpace.LOGGER.info("finished mass pass, result:");
            CreatingSpace.LOGGER.info("total initial mass for propellants {}Kg", initialPropellantMass);
            CreatingSpace.LOGGER.info("inert mass {} Kg (inert fluid {}| dry mass{})", emptyMass, contraption.getStorage().getInertFluidMass(), contraption.getDryMass());
        }

        float finalPropellantMass = (float) ((emptyMass+initialPropellantMass)/Math.exp(deltaVNeeded/meanVe)-emptyMass);

        float consumedPropellantMass = initialPropellantMass - finalPropellantMass;
        if (CSConfigs.COMMON.additionalLogInfo.get()) {
            CreatingSpace.LOGGER.info("estimated propellant consumption : {} Kg", consumedPropellantMass);
        }

        rocketContraptionEntity.initialMass = emptyMass+initialPropellantMass ;

        int distance = (int) (300 - rocketContraptionEntity.position().y());

        float gravity = CSDimensionUtil.gravity(rocketContraptionEntity.level().dimensionTypeId().location());

        float acceleration = totalThrust/(emptyMass+initialPropellantMass)-gravity;
        float perTickSpeed = getPerTickSpeed(acceleration);

        float totalTickTime = distance / perTickSpeed;
        if (CSConfigs.COMMON.additionalLogInfo.get()) {
            CreatingSpace.LOGGER.info("distance : {}", distance);
            CreatingSpace.LOGGER.info("speed : {}blocks/ticks", perTickSpeed);
            CreatingSpace.LOGGER.info("travel time : {} ticks", totalTickTime);
        }
        //fill the real consumption map and fill the consumedMass map for mass verification
        HashMap<TagKey<Fluid>,Float> consumedMassForEachPropellant = new HashMap<>();//just to determine if there is enough fluid
        float realPartialConsumption = consumedPropellantMass/totalTheoreticalConsumption;
        for (TagKey<Fluid> fluidTagKey : ((RocketContraption) rocketContraptionEntity.contraption).getTPTFluidConsumption().keySet()) {
            RocketContraption.ConsumptionInfo info = ((RocketContraption) rocketContraptionEntity.contraption).getTPTFluidConsumption().get(fluidTagKey);
            //that's the consumed mass for the ensemble of engine with the same propellant combination
            Float correctedConsumptions = info.fluidConsumption() * realPartialConsumption / totalTickTime;

            rocketContraptionEntity.realPerTagFluidConsumption.put(fluidTagKey, new RocketContraption.ConsumptionInfo( correctedConsumptions ,info.partialThrust()));
             consumedMassForEachPropellant.put(fluidTagKey,
                     consumedMassForEachPropellant.getOrDefault(fluidTagKey, 0f) + correctedConsumptions);
        }

        if (CSConfigs.COMMON.additionalLogInfo.get()) {
            CreatingSpace.LOGGER.info("finished correction of consumption path :");
            CreatingSpace.LOGGER.info("consumed mass for each fluid tag :{}", consumedMassForEachPropellant);
            CreatingSpace.LOGGER.info("consumed mass per propellant : {}", rocketContraptionEntity.realPerTagFluidConsumption);
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
        if (CSConfigs.COMMON.additionalLogInfo.get()) {
            CreatingSpace.LOGGER.info("determining if the rocket can go :");
            CreatingSpace.LOGGER.info(String.valueOf(assemblyData));
        }
        if (distance<=0){
            rocketContraptionEntity.getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.PROPULSION_ISSUE);
            return;
        }
        if (assemblyData.hasFailed()) {
            rocketContraptionEntity.getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.FUEL_ISSUE);
            return;
        }
        rocketContraptionEntity.getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.TRAVELING);
    }
    public float deltaV() {
        //wrong because no consideration for the ratio of propellants
        float totalThrust = 0;
        float inertFluidsMass = 0;
        IFluidHandler fluidHandler = contraption.getStorage().getFluids();
        int nbrOfTank = fluidHandler.getTanks();
        //both research of every consumable fluid and addition of the total consumption
        float totalTheoreticalConsumption = 0;
        //TODO that could be in the inventory manager of the rocket -> 1.8
        for (TagKey<Fluid> fluidTagKey : ((RocketContraption) this.contraption).getTPTFluidConsumption().keySet()) {
            //PropellantType combination = PropellantTypeInit.getSyncedPropellantRegistry().get(location);
            RocketContraption.ConsumptionInfo info = ((RocketContraption) this.contraption).getTPTFluidConsumption().get(fluidTagKey);
            //mean speed of ejected gasses for the fluid -> need to be done for a couple of tag -> ox/fuel
            totalTheoreticalConsumption += info.fluidConsumption();

            totalThrust += info.partialThrust();
            //initialise if not present
            searchForFluid(this, fluidTagKey);

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
    private static void searchForFluid(RocketContraptionEntity rocketContraptionEntity, TagKey<Fluid> tag) {
        rocketContraptionEntity.consumableFluids.put(tag, new ArrayList<>());
        IFluidHandler fluidHandler = rocketContraptionEntity.contraption.getStorage().getFluids();
        int nbrOfTank = fluidHandler.getTanks();

        for (int i = 0; i < nbrOfTank; i++) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
            if (fluidInTank.getFluid().is(tag)) {
                if (!rocketContraptionEntity.consumableFluids.get(tag).contains(fluidInTank.getFluid())) {
                    rocketContraptionEntity.consumableFluids.get(tag).add(fluidInTank.getFluid());
                }
            }
        }
    }
    private static HashMap<TagKey<Fluid>, Integer> getMassMap(RocketContraptionEntity rocketContraptionEntity ) {

        HashMap<TagKey<Fluid>, Integer> massForEachPropellant = new HashMap<>();
        //remove the string from the consumableFluids
        ArrayList<TagKey<Fluid>> allPropellantTags = new ArrayList<>(rocketContraptionEntity.consumableFluids.keySet());
        IFluidHandler fluidHandler = rocketContraptionEntity.contraption.getStorage().getFluids();
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
    @Override
    public void disassemble() {
        //doesn't work with create_interactive
        for (BlockPos localPos:this.localPosOfFlightRecorders){
            StructureTemplate.StructureBlockInfo oldStructureInfo = this.contraption.getBlocks().get(localPos);
            CompoundTag nbt = oldStructureInfo.nbt();
            nbt.put("lastAssemblyData",FlightDataHelper.RocketAssemblyData.toNBT(this.assemblyData));
            StructureTemplate.StructureBlockInfo newStructureInfo =
                    new StructureTemplate.StructureBlockInfo(oldStructureInfo.pos(),oldStructureInfo.state(),nbt);
            this.contraption.getBlocks().put(localPos,newStructureInfo);
        }
        super.disassemble();
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATUS_DATA_ACCESSOR, RocketStatus.IDLE);
        entityData.define(INITIAL_ORIENTATION, Direction.UP);
    }

    @Override
    public boolean startControlling(BlockPos controlsLocalPos, Player player) {
        return !isInPropulsionPhase() && CSDimensionUtil.isOrbit(player.level().dimension().location());
    }

    @Override
    public void tick() {
        //movement is bugged when in ground -> avoid collision by slowing down upon landing ? or breaking blocks
        boolean wasRunning = isInPropulsionPhase();
        if (isInPropulsionPhase()) {
            if (soundEffectTickCount <= 0) {
                //level.playSeededSound(null, this, SoundEvents.ALLAY_HURT, SoundSource.MASTER, 1, 1,0);
                if (!level().isClientSide) playSound(ROCKET_LAUNCH.get(), 1, 1);
                soundEffectTickCount = ROCKET_SOUND_LENGTH;
            } else {
                soundEffectTickCount--;
            }
        }
        schedule.tick(level());
        super.tick();
        if (wasRunning && !isInPropulsionPhase()) {
            schedule.destinationReached();
        }


    }

    @Override
    protected void tickContraption() {
        if (!(contraption instanceof RocketContraption))
            return;

        super.tickContraption();
        if (!level().isClientSide) {
            if (isInPropulsionPhase()) {
                tickConsumptionAndSpeed();
                tickDimensionChangeLogic();
                Vec3 movementVec = getDeltaMovement();


                if (ContraptionCollider.collideBlocks(this) && !(level().getMaxBuildHeight() < this.getBoundingBox().maxY + movementVec.y)) {
                    //stopRocket();
                    getEntityData().set(STATUS_DATA_ACCESSOR, isReentry() ? RocketStatus.IDLE : RocketStatus.COLLISION);
                    //setContraptionMotion(Vec3.ZERO);

                } else if (tickCount > 2) {//that means the rocket takes 2 ticks more than expected to go up
                    movementVec = VecHelper.clampComponentWise(movementVec, (float) 1);
                    move(movementVec.x, movementVec.y, movementVec.z);
                }
            }
            //setContraptionMotion(getDeltaMovement().scale(0.8));
        }
        /*
        if (!isInPropulsionPhase()) {
            setContraptionMotion(Vec3.ZERO);
            this.speed = 0;
        }*/
    }

    @Override
    public boolean control(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player) {
        if (level().isClientSide)
            return true;
        if (player.isSpectator())
            return false;
        if (!toGlobalVector(VecHelper.getCenterOf(controlsLocalPos), 1).closerThan(player.position(), 8))
            return false;
        if (isInPropulsionPhase())
            return false;
        float speedModificator = 0.2f;
        Vec3 speed = Vec3.ZERO;
        Vec2 rotSpeed = Vec2.ZERO;
        if (heldControls.contains(12)){
            if ((player instanceof ServerPlayer serverPlayer)) {

                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inv, p) ->
                        RocketMenu.create(id, inv, this), Component.translatable("container.my_item_menu")), buf ->
                        buf.writeVarInt(this.getId()));
            }
        }
        if (heldControls.contains(11)){
            speedModificator = 2f;
        }
        if (heldControls.contains(5)){
            speed =  speed.add(VecHelper.clampComponentWise( Vec3.atLowerCornerOf(Direction.DOWN.getNormal())
                    .yRot((float) (getViewYRot(1)/180*Math.PI)), 0.8f).scale(speedModificator));
        }
        if (heldControls.contains(4)){
            speed =  speed.add(VecHelper.clampComponentWise( Vec3.atLowerCornerOf(Direction.UP.getNormal())
                    .yRot((float) (getViewYRot(1)/180*Math.PI)), 0.8f).scale(speedModificator));
        }

        if (heldControls.contains(0)){
            speed =  speed.add(VecHelper.clampComponentWise( Vec3.atLowerCornerOf(getInitialOrientation().getNormal())
                    .yRot((float) (getViewYRot(1)/180*Math.PI)), 0.8f).scale(speedModificator));
        }
        if (heldControls.contains(1)){
            speed =  speed.add(VecHelper.clampComponentWise( Vec3.atLowerCornerOf(getInitialOrientation().getOpposite().getNormal())
                    .yRot((float) (getViewYRot(1)/180*Math.PI)), 0.8f).scale(speedModificator));
        }
        if (heldControls.contains(2)){
            speed =  speed.add(VecHelper.clampComponentWise( Vec3.atLowerCornerOf(getInitialOrientation().getCounterClockWise().getNormal())
                    .yRot((float) (getViewYRot(1)/180*Math.PI)), 0.8f).scale(speedModificator));
        }
        if (heldControls.contains(3)){
            speed =  speed.add(VecHelper.clampComponentWise( Vec3.atLowerCornerOf(getInitialOrientation().getClockWise().getNormal())
                    .yRot((float) (getViewYRot(1)/180*Math.PI)), 0.8f).scale(speedModificator));
        }
        if (heldControls.contains(8)){
            rotSpeed = new Vec2(0,-3f*speedModificator);
        }
        if (heldControls.contains(9)){
            rotSpeed = new Vec2(0,3f*speedModificator);
        }
        setRotSpeed(rotSpeed);
        setContraptionMotion(speed);
        return true;
    }

    @Override
    public void stopControlling(BlockPos controlsLocalPos) {
        super.stopControlling(controlsLocalPos);
        if (!isInPropulsionPhase()){
            setContraptionMotion(Vec3.ZERO);
        }
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


            ServerLevel destServerLevel = this.level().getServer().getLevel(
                    ResourceKey.create(Registries.DIMENSION,
                            this.nextPath.destination)
            );

            if (destServerLevel!=null) {

                this.changeDimension(destServerLevel,new RocketTeleporter(destServerLevel));
            }
            else {
                LOGGER.error("rocket failed to get server for destination : {}", this.nextPath.destination);
                this.entityData.set(STATUS_DATA_ACCESSOR, RocketStatus.ON_FINAL);
            }
        }
    }
    protected void tickConsumptionAndSpeed() {
        if (level().isClientSide())
            return;

        float gravity = CSDimensionUtil.gravity(this.level().dimension().location());

        if (!isReentry() ){
            if (!level().isClientSide())
                consumePropellant(this);
        }

        Vec3 movementVec;
        float acceleration = getAcceleration(
                initialMass,
                (int) totalThrust,gravity,isReentry());

        float speed = getPerTickSpeed(acceleration);
        movementVec = new Vec3(0,speed,0);
        setContraptionMotion(movementVec);
    }

    private void consumePropellant(RocketContraptionEntity rocketContraptionEntity) {
        if (level().isClientSide()){
            return;
        }
        RocketContraption rocketContraption = (RocketContraption) rocketContraptionEntity.contraption;
        IFluidHandler fluidHandler = rocketContraption.getStorage().getFluids();
        //need to construct a map of drainAmount and partial drain -> map of couple/record(int,float)
        //make in a loop so it look for every one ?
        for (TagKey<Fluid> fluidTag : realPerTagFluidConsumption.keySet()) {
            RocketContraption.ConsumptionInfo info = realPerTagFluidConsumption.get(fluidTag);
                Float prevPartialDrainValue = partialDrainAmountPerFluid.get(fluidTag);
                ArrayList<Fluid> fluids = consumableFluids.get(fluidTag);
                if (!(fluids == null || fluids.isEmpty())) {

                    Fluid oxFluid = fluids.get(0);

                    FluidType oxFluidType = oxFluid.getFluidType();
                    float rho = (float) oxFluidType.getDensity() / 1000;

                    float amount = info.fluidConsumption() / rho; // oxConsumption in kg, rho in kg/mb
                    if (prevPartialDrainValue == null) {
                        prevPartialDrainValue = 0f;
                    }
                    float partialConsumedAmount = prevPartialDrainValue;
                    partialConsumedAmount = partialConsumedAmount + amount - ((int) amount);

                    if (partialConsumedAmount >= 1) {
                        amount = amount + 1;
                        partialConsumedAmount = partialConsumedAmount - 1;
                    }
                    partialDrainAmountPerFluid.put(fluidTag, partialConsumedAmount);


                    int consumedOx = fluidHandler.drain(new FluidStack(oxFluid, (int) amount), IFluidHandler.FluidAction.EXECUTE).getAmount();//drain ox

                    if (consumedOx == 0) {
                        RocketContraptionEntity.searchForFluid(this, fluidTag);
                }
            }
        }
    }
    //merge that with the static method ?
    @Nullable
    @Override
    public Entity changeDimension(ServerLevel destLevel, @NotNull ITeleporter teleporter) {
        //rewrite so passengers get teleported with it
        if (!ForgeHooks.onTravelToDimension(this, destLevel.dimension())) return null;
        if (this.level() instanceof ServerLevel && !this.isRemoved()) {
            this.level().getProfiler().push("changeDimension");

            List<Entity> passengers = this.getPassengers();
            List<Entity> collidingEntities = level().getEntities(this, this.getBoundingBox());
            collidingEntities.removeAll(passengers);
            this.unRide();
            BlockPos previousRocketPos = this.getOnPos();
            this.level().getProfiler().push("reposition");
            PortalInfo portalinfo = teleporter.getPortalInfo(this, destLevel, this::findDimensionEntryPoint);
            if (portalinfo == null) {
                return null;
            } else {
                Entity transportedEntity = teleporter.placeEntity(this, (ServerLevel) this.level(), destLevel, this.getYRot(),

                        spawnPortal -> { //Forge: Start custom logic
                            this.level().getProfiler().popPush("reloading");

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
                                        player.changeDimension(destLevel, new RocketTeleporter(destLevel));
                                        entity.addSittingPassenger(player, i);
                                    } else {
                                        if (!(passenger instanceof Player)) {
                                            passenger.changeDimension(destLevel, new RocketTeleporter(destLevel));
                                            entity.addSittingPassenger(passenger, i);
                                        }
                                    }
                                }
                                for (Entity movedEntity : collidingEntities) {
                                    BlockPos posDif = movedEntity.getOnPos().subtract(previousRocketPos);
                                    movedEntity.moveTo(portalinfo.pos.x + posDif.getX(), portalinfo.pos.y + posDif.getY(), portalinfo.pos.z + posDif.getZ(), movedEntity.getYRot(), movedEntity.getXRot());

                                    if (movedEntity instanceof ServerPlayer player) {
                                        player.changeDimension(destLevel, new RocketTeleporter(destLevel));
                                    } else {
                                        if (!(movedEntity instanceof Player)) {
                                            movedEntity.changeDimension(destLevel, new RocketTeleporter(destLevel));
                                        }
                                    }
                                }

                                destLevel.addDuringTeleport(entity);
                                if (CSDimensionUtil.isOrbit(destLevel.dimension().location())) {
                                    //entity.disassemble();
                                    entity.stopRocket();
                                    entity.schedule.destinationReached();
                                }
                                else{
                                    entity.entityData.set(STATUS_DATA_ACCESSOR, RocketStatus.ON_FINAL);
                                }
                            }
                            return entity;
                        }); //Forge: End custom logic

                this.removeAfterChangingDimensions();
                this.level().getProfiler().pop();
                ((ServerLevel) this.level()).resetEmptyTime();
                destLevel.resetEmptyTime();
                this.level().getProfiler().pop();
                return transportedEntity;
            }
        } else {
            return null;
        }
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
    public @NotNull AABB getBoundingBoxForCulling() {
        return isInPropulsionPhase() ?
                new AABB(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE,
                        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE) :
                super.getBoundingBoxForCulling();
    }

    //saving and getters
    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnData) {
        super.readAdditional(compound, spawnData);
        this.localPosOfFlightRecorders = CSNBTUtil.LongsToBlockPos(compound.getLongArray("localPosOfFlightRecorders"));//to remove
        this.totalThrust = compound.getFloat("thrust");
        this.initialMass = compound.getFloat("initialMass");
        this.realPerTagFluidConsumption = CODEC_MAP_INFO.parse(NbtOps.INSTANCE, compound.getCompound("realPerTagFluidConsumption")).result().orElse(new HashMap<>());
        this.partialDrainAmountPerFluid = CODEC_MAP_CONSUMPTION.parse(NbtOps.INSTANCE, compound.getCompound("partialDrainAmountPerFluid")).result().orElse(new HashMap<>());
        this.assemblyData = FlightDataHelper.RocketAssemblyData.fromNBT(compound.getCompound("assemblyData"));
        this.entityData.set(STATUS_DATA_ACCESSOR, RocketStatus.valueOf(compound.getString("status")));

        this.nextPath = RocketPath.parse((CompoundTag) compound.get("nextPath"));
        this.schedule.read((CompoundTag) compound.get("Runtime"));
        for (TagKey<Fluid> fluid  : realPerTagFluidConsumption.keySet())  {
                RocketContraptionEntity.searchForFluid(this, fluid);

        }
    }
    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        compound.putLongArray("localPosOfFlightRecorders", CSNBTUtil.BlockPosToLong(this.localPosOfFlightRecorders));//to remove
        compound.putFloat("initialMass", this.initialMass);
        compound.put("realPerTagFluidConsumption", CODEC_MAP_INFO.encodeStart(NbtOps.INSTANCE, this.realPerTagFluidConsumption).get().left().orElse(new CompoundTag()));
        compound.put("partialDrainAmountPerFluid", CODEC_MAP_CONSUMPTION.encodeStart(NbtOps.INSTANCE, this.partialDrainAmountPerFluid).get().left().orElse(new CompoundTag()));

        compound.put("assemblyData", FlightDataHelper.RocketAssemblyData.toNBT(this.assemblyData));
        compound.putFloat("thrust", this.totalThrust);
        compound.putString("status", this.entityData.get(STATUS_DATA_ACCESSOR).toString());
        compound.put("nextPath",nextPath!=null?nextPath.serialize():new CompoundTag());
        compound.put("Runtime", schedule.write());

        super.writeAdditional(compound, spawnPacket);
    }
    @Override
    public boolean isSilent() {
        return false;
    }
    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.MASTER;
    }
    public boolean isReentry() {
        return this.entityData.get(STATUS_DATA_ACCESSOR) == RocketStatus.ON_FINAL;
    }
    public boolean isInPropulsionPhase() {
        return this.entityData.get(STATUS_DATA_ACCESSOR).propelled_phase;
    }
    //navigation part (schedule)
    public void successfulNavigation() {
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
        if (!level().isClientSide()) {
            //so the pos is initialized
            this.nextPath = nextPath;
            //this.rocketEntryCoordinate = new BlockPos(nextPath.XZCoord.x,CSDimensionUtil.arrivalHeight(nextPath.destination),nextPath.XZCoord.y);
            getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.TRAVELING);

            handelTrajectoryCalculation(this);
            if (getEntityData().get(STATUS_DATA_ACCESSOR).equals(RocketStatus.COLLISION)) {
                return -1;
            }
        }
        //fail according to handleTrajectoryCalculation ?
        return 0;
    }
    private void stopRocket() {
        getEntityData().set(STATUS_DATA_ACCESSOR, RocketStatus.IDLE);
        setContraptionMotion(Vec3.ZERO);
    }
    public enum ControlMode {
        FIGHTER,
        DOCKING
    }
    public enum RocketStatus {
        IDLE(false),
        TRAVELING(true),
        PROPULSION_ISSUE(false),//going up to the next dimension
        COLLISION(false),//used when physically blocked
        FUEL_ISSUE(false),// and when not enough fuel, -> separate into several cases ?
        ON_FINAL(true);
        final boolean propelled_phase;

        RocketStatus(boolean propelled_phase) {
            this.propelled_phase = propelled_phase;
        }
    }
}