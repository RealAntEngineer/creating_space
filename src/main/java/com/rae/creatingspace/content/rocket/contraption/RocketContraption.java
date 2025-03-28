package com.rae.creatingspace.content.rocket.contraption;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.rocket.engine.RocketEngineBlockEntity;
import com.rae.creatingspace.content.rocket.flight_recorder.FlightRecorderBlock;
import com.rae.creatingspace.init.CSContraptionType;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.rae.creatingspace.legacy.utilities.CSMassUtil;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RocketContraption extends TranslatingContraption {
    private int thrust = 0;
    private int dryMass = 0;
    //private final HashMap<Couple<TagKey<Fluid>>, ConsumptionInfo> theoreticalPerTagFluidConsumption = new HashMap<>();
    private HashMap<PropellantType, ConsumptionInfo> theoreticalPerTagFluidConsumption = new HashMap<>();

    private final ArrayList<BlockPos> localPosOfFlightRecorders = new ArrayList<>();
    public RocketContraption() {
        storage = new RocketStorageManager();
    }
    public RocketStorageManager getStorage(){
        return (RocketStorageManager) storage;
    }
    @Override
    public boolean assemble(Level level, BlockPos pos) throws AssemblyException {

        if (!searchMovedStructure(level, pos, null)) {
            return false;
        }
        startMoving(level);
        expandBoundsAroundAxis(Direction.Axis.Y);
        getStorage().onContraptionAssemble(this);
        return true;
    }

    @Override
    protected void addBlock(Level level,BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair) {
        Block blockAdded = pair.getLeft().state().getBlock();
        BlockEntity blockEntityAdded = pair.getRight();
        BlockPos localPos = pos.subtract(anchor);

        if (blockEntityAdded instanceof RocketEngineBlockEntity engineBlockEntity){

            this.thrust += engineBlockEntity.getThrust();
            float totalPropellantMassFlow = (float) (engineBlockEntity.getThrust() / (
                    engineBlockEntity.getIsp()* CSConfigs.SERVER.rocketEngine.ISPModifier.get() *9.81));
            PropellantType propellantType = engineBlockEntity.getPropellantType();
            ConsumptionInfo previousCombInfo = new ConsumptionInfo(new HashMap<>(), 0);

            if (this.theoreticalPerTagFluidConsumption.containsKey(propellantType)) {
                previousCombInfo = this.theoreticalPerTagFluidConsumption
                        .get(propellantType);
            }
            HashMap<TagKey<Fluid>, Float> proportions = new HashMap<>(engineBlockEntity.getPropellantType().getPropellantRatio());
            multiplyMap(proportions, totalPropellantMassFlow);
            this.theoreticalPerTagFluidConsumption.put(propellantType, previousCombInfo.add(proportions, engineBlockEntity.getThrust()));


        }
        this.dryMass += CSMassUtil.mass(blockAdded.defaultBlockState(), blockEntityAdded);
        if (blockAdded instanceof FlightRecorderBlock){
            this.localPosOfFlightRecorders.add(localPos);
        }
        super.addBlock(level,pos, pair);
    }

    public static void multiplyMap(HashMap<TagKey<Fluid>, Float> map, float amount) {
        for (TagKey<Fluid> fluid :
                map.keySet()) {
            map.put(fluid, map.get(fluid) * amount);
        }
    }

    @Override
    protected boolean moveBlock(Level world, @Nullable Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited) throws AssemblyException {
        return super.moveBlock(world, forcedDirection, frontier, visited);
    }

    @Override //to allow the intial block to be a part of the contraption
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return false;
    }

    @Override
    public ContraptionType getType() {
        return CSContraptionType.ROCKET.get();
    }
    public static final Codec<Map<PropellantType, ConsumptionInfo>> CODEC = Codec.unboundedMap(PropellantTypeInit.getSyncedPropellantRegistry().byNameCodec(), ConsumptionInfo.CODEC);
    @Override
    public void readNBT(Level world, CompoundTag nbt, boolean clientPacket) {

        //TODO add data for server/client sync (possible solution of Interactive bug)
            thrust = nbt.getInt("thrust");
            dryMass = nbt.getInt("dryMass");
            Arrays.stream(nbt.getLongArray("localPosOfFlightRecorders")).forEach(l -> localPosOfFlightRecorders.add(BlockPos.of(l)));
            theoreticalPerTagFluidConsumption = new HashMap<>(CODEC.parse(NbtOps.INSTANCE, nbt.get("theoreticalPerTagFluidConsumption")).result().orElse(new HashMap<>()));
        super.readNBT(world, nbt, clientPacket);
    }

    @Override
    public CompoundTag writeNBT(boolean spawnPacket) {
        //TODO add data for server/client sync
        CompoundTag nbt = super.writeNBT(spawnPacket);
        nbt.putInt("thrust", thrust);
        nbt.putInt("dryMass", dryMass);
        nbt.putLongArray("localPosOfFlightRecorders", localPosOfFlightRecorders.stream().map(BlockPos::asLong).toList());
        nbt.put("theoreticalPerTagFluidConsumption",CODEC.encodeStart(NbtOps.INSTANCE,theoreticalPerTagFluidConsumption).result().orElse(new CompoundTag()));

        return nbt;
    }
    public ArrayList<BlockPos> getLocalPosOfFlightRecorders() {
        return localPosOfFlightRecorders;
    }

    public float getDryMass(){
        return this.dryMass;
    }
    public float getThrust(){
        return this.thrust;
    }

    public HashMap<PropellantType, ConsumptionInfo> getTPTFluidConsumption() {
        return theoreticalPerTagFluidConsumption;
    }

    //public record ConsumptionInfo(float oxConsumption, float fuelConsumption, int partialThrust){
    public record ConsumptionInfo(Map<TagKey<Fluid>, Float> propellantConsumption, int partialThrust) {
        public static final Codec<ConsumptionInfo> CODEC = RecordCodecBuilder.create(
                instance ->
                        instance.group(
                                        Codec.unboundedMap(
                                                TagKey.codec(Registries.FLUID),
                                                Codec.FLOAT
                                        ).fieldOf("propellantConsumption").forGetter(i -> i.propellantConsumption),
                                        Codec.INT.fieldOf("partialThrust").forGetter(i -> i.partialThrust)
                                )
                                .apply(instance, ConsumptionInfo::new)
        );

        //expect that the keys are the same
        public ConsumptionInfo add(Map<TagKey<Fluid>, Float> propellantConsumption, int partialThrust) {
            HashMap<TagKey<Fluid>, Float> newMap = new HashMap<>(this.propellantConsumption());
            for (TagKey<Fluid> fluid :
                    propellantConsumption.keySet()) {
                newMap.put(fluid, newMap.getOrDefault(fluid, 0f) + propellantConsumption.get(fluid));
            }
            return new ConsumptionInfo(newMap,
                    this.partialThrust +partialThrust);
        }
    }
}
