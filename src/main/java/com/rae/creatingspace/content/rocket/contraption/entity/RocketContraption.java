package com.rae.creatingspace.content.rocket.contraption.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.rocket.CSMassUtil;
import com.rae.creatingspace.content.rocket.engine.RocketEngineBlockEntity;
import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.content.rocket.flight_recorder.FlightRecorderBlock;
import com.rae.creatingspace.init.CSContraptionType;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
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
    public static final UnboundedMapCodec<TagKey<Fluid>, ConsumptionInfo> TPTF_CODEC                = Codec.unboundedMap(TagKey.codec(Registries.FLUID), ConsumptionInfo.CODEC);
    private final       ArrayList<BlockPos>                               localPosOfFlightRecorders = new ArrayList<>();
    private int                                     thrust                            = 0;
    private int                                     dryMass                           = 0;
    //private final HashMap<Couple<TagKey<Fluid>>, ConsumptionInfo> theoreticalPerTagFluidConsumption = new HashMap<>();
    private HashMap<TagKey<Fluid>, ConsumptionInfo> theoreticalPerTagFluidConsumption = new HashMap<>();

    public RocketContraption() {
        storage = new RocketStorageManager();
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
    public ContraptionType getType() {
        return CSContraptionType.ROCKET.get();
    }

    @Override
    protected boolean moveBlock(Level world, @Nullable Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited) throws AssemblyException {
        return super.moveBlock(world, forcedDirection, frontier, visited);
    }

    @Override
    protected void addBlock(Level level, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair) {
        Block       blockAdded       = pair.getLeft().state().getBlock();
        BlockEntity blockEntityAdded = pair.getRight();
        BlockPos    localPos         = pos.subtract(anchor);

        if (blockEntityAdded instanceof RocketEngineBlockEntity engineBlockEntity) {

            this.thrust += engineBlockEntity.getThrust();//verify what's it's doing
            float totalPropellantMassFlow = (float) (engineBlockEntity.getThrust() / (
                    engineBlockEntity.getIsp() * CSConfigs.SERVER.rocketEngine.ISPModifier.get() * 9.81));
            // -> go to FluidTagKeys.
            HashMap<TagKey<Fluid>, Float> PFTMassFlow = new HashMap<>(engineBlockEntity.getPropellantType().getPropellantRatio());
            multiplyMap(PFTMassFlow, totalPropellantMassFlow);
            for (TagKey<Fluid> fluidTagKey : PFTMassFlow.keySet()) {
                ConsumptionInfo previousCombInfo = new ConsumptionInfo(0f, 0);
                if (this.theoreticalPerTagFluidConsumption.containsKey(fluidTagKey)) {
                    previousCombInfo = this.theoreticalPerTagFluidConsumption
                            .get(fluidTagKey);
                }
                this.theoreticalPerTagFluidConsumption.put(fluidTagKey, previousCombInfo.add(PFTMassFlow.get(fluidTagKey), engineBlockEntity.getThrust()));
            }

        }
        this.dryMass += CSMassUtil.mass(blockAdded.defaultBlockState(), blockEntityAdded);
        if (blockAdded instanceof FlightRecorderBlock) {
            this.localPosOfFlightRecorders.add(localPos);
        }
        super.addBlock(level, pos, pair);
    }

    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return false;
    }

    @Override
    public void readNBT(Level world, CompoundTag nbt, boolean clientPacket) {

        //TODO add data for server/client sync (possible solution of Interactive bug)
        thrust = nbt.getInt("thrust");
        dryMass = nbt.getInt("dryMass");
        Arrays.stream(nbt.getLongArray("localPosOfFlightRecorders")).forEach(l -> localPosOfFlightRecorders.add(BlockPos.of(l)));
        theoreticalPerTagFluidConsumption = new HashMap<>(TPTF_CODEC.parse(NbtOps.INSTANCE, nbt.get("theoreticalPerTagFluidConsumption")).result().orElse(new HashMap<>()));
        super.readNBT(world, nbt, clientPacket);
    }

    @Override
    public CompoundTag writeNBT(boolean spawnPacket) {
        //TODO add data for server/client sync
        CompoundTag nbt = super.writeNBT(spawnPacket);
        nbt.putInt("thrust", thrust);
        nbt.putInt("dryMass", dryMass);
        nbt.putLongArray("localPosOfFlightRecorders", localPosOfFlightRecorders.stream().map(BlockPos::asLong).toList());
        nbt.put("theoreticalPerTagFluidConsumption", TPTF_CODEC.encodeStart(NbtOps.INSTANCE, theoreticalPerTagFluidConsumption).result().orElse(new CompoundTag()));

        return nbt;
    }

    public RocketStorageManager getStorage() {
        return (RocketStorageManager) storage;
    }

    //Custom logic
    public static void multiplyMap(HashMap<TagKey<Fluid>, Float> map, float amount) {
        map.replaceAll((f, v) -> map.get(f) * amount);
    }

    @Deprecated
    public ArrayList<BlockPos> getLocalPosOfFlightRecorders() {
        return localPosOfFlightRecorders;
    }

    public float getDryMass() {
        return this.dryMass;
    }

    public float getThrust() {
        return this.thrust;
    }

    public HashMap<TagKey<Fluid>, ConsumptionInfo> getTPTFluidConsumption() {
        return theoreticalPerTagFluidConsumption;
    }
    public static Codec<HashMap<PropellantType, RocketContraption.ConsumptionInfo>> getCodecMapInfo(RegistryAccess registries) {
        return Codec.unboundedMap(
                        registries.registryOrThrow(PropellantTypeInit.Keys.PROPELLANT_TYPE).byNameCodec().xmap(
                                // decode
                                type -> type,

                                // encode
                                type -> type == PropellantTypeInit.METHALOX_DIRECT
                                        ? registries.registryOrThrow(PropellantTypeInit.Keys.PROPELLANT_TYPE).get(CreatingSpace.resource("methalox"))
                                        : type
                        ),
                        RocketContraption.ConsumptionInfo.CODEC)
                .xmap(HashMap::new, i -> i);
    }

    //public record ConsumptionInfo(float oxConsumption, float fuelConsumption, int partialThrust){
    public record ConsumptionInfo(Float fluidConsumption, int partialThrust) {
        public static final Codec<ConsumptionInfo> CODEC = RecordCodecBuilder.create(
                instance ->
                        instance.group(Codec.FLOAT.fieldOf("propellantConsumption").forGetter(i -> i.fluidConsumption),
                                        Codec.INT.fieldOf("partialThrust").forGetter(i -> i.partialThrust)
                                )
                                .apply(instance, ConsumptionInfo::new)
        );

        //expect that the keys are the same
        public ConsumptionInfo add(Float propellantConsumption, int partialThrust) {
            return new ConsumptionInfo(
                    this.fluidConsumption + propellantConsumption,
                    this.partialThrust + partialThrust);
        }
    }
}
