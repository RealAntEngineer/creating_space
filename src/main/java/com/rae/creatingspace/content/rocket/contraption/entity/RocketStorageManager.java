package com.rae.creatingspace.content.rocket.contraption.entity;

import com.mojang.serialization.Codec;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@NonnullDefault
public class RocketStorageManager extends MountedStorageManager {

    // codec for persisting per-tag propellant mass / consumption ratio; mirrors RocketContraption.TPTF_CODEC's key handling
    public static final Codec<HashMap<TagKey<Fluid>, Float>> TAG_TO_FLOAT_CODEC =
            Codec.unboundedMap(TagKey.codec(Registries.FLUID), Codec.FLOAT)
                    .xmap(HashMap::new, HashMap::new);

    int ticksSinceLastExchange;
    AtomicInteger version;

    //normalized (sum to 1) share of total theoretical consumption per tag. Computed once at assembly, then persisted.
    private @Nullable HashMap<TagKey<Fluid>, Float> normalizedConsumptionRatio = null;
    //mass of propellant actually loaded, per tag. Needed because different propellants deplete at different rates.
    private final HashMap<TagKey<Fluid>, Float> propellantMassPerTag = new HashMap<>();

    float currentDeltaV;
    float dryMass;
    float inertFluidMass;
    float propellantMass;
    float meanVe;

    /**
     * called just after the contraption is search and validated for assembly, before the inventory is wrapped
     */
    public void onContraptionAssemble(RocketContraption rocketContraption) {
        HashMap<TagKey<Fluid>, RocketContraption.ConsumptionInfo> tptfc = rocketContraption.getTPTFluidConsumption();

        propellantMassPerTag.clear();
        for (TagKey<Fluid> tag : tptfc.keySet()) {
            propellantMassPerTag.put(tag, 0f);
        }

        dryMass = rocketContraption.getDryMass();

        computeConsumptionData(tptfc);
    }

    public float getCurrentDeltaV() {
        return currentDeltaV;
    }

    public float getInertFluidMass() {
        return inertFluidMass;
    }

    public float getPropellantMass() {
        return propellantMass;
    }

    public float getMeanVe() {
        return meanVe;
    }

    public RocketStorageManager() {
        version = new AtomicInteger();
        ticksSinceLastExchange = 0;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.items = new CargoInvWrapper(this.items);
        if (this.fuelItems != null) {
            this.fuelItems = new CargoInvWrapper(this.fuelItems);
        }
        this.fluids = new CargoTankWrapper(this.fluids);
        IFluidHandler fluidHandler = getFluids();
        int nbrOfTank = fluidHandler.getTanks();
        //!! O(nbr_tank*nbr_prop)

        for (int i = 0; i < nbrOfTank; i++) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
            onFilled(fluidInTank);
        }
        if (normalizedConsumptionRatio != null) {
            recomputeDeltaV();
        }
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        nbt.putInt("TicksSinceLastExchange", ticksSinceLastExchange);

        nbt.putFloat("currentDeltaV", currentDeltaV);
        nbt.putFloat("dryMass", dryMass);
        nbt.putFloat("inertFluidMass", inertFluidMass);
        nbt.putFloat("propellantMass", propellantMass);
        nbt.putFloat("meanVe", meanVe);
        nbt.put("propellantMassPerTag", TAG_TO_FLOAT_CODEC.encodeStart(NbtOps.INSTANCE, propellantMassPerTag).result().orElseThrow());
        if (normalizedConsumptionRatio != null) {
            nbt.put("normalizedConsumptionRatio", TAG_TO_FLOAT_CODEC.encodeStart(NbtOps.INSTANCE, normalizedConsumptionRatio).result().orElseThrow());
        }
    }

    @Override
    public void addBlock(Level level, BlockState state, BlockPos globalPos, BlockPos localPos, @Nullable BlockEntity be) {
        super.addBlock(level, state, globalPos, localPos, be);
        //TODO put the initialisation of the mass, and of the thrust, consumptions... here instead of the contraption.
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket, @Nullable Contraption contraption) {//lookup and write here what clientPacket actually means
        super.read(nbt, clientPacket, contraption);
        ticksSinceLastExchange = nbt.getInt("TicksSinceLastExchange");
        currentDeltaV = nbt.getFloat("currentDeltaV");
        dryMass = nbt.getFloat("dryMass");
        inertFluidMass = nbt.getFloat("inertFluidMass");
        propellantMass = nbt.getFloat("propellantMass");
        meanVe = nbt.getFloat("meanVe");

        propellantMassPerTag.clear();
        if (nbt.contains("propellantMassPerTag")) {
            propellantMassPerTag.putAll(TAG_TO_FLOAT_CODEC.parse(NbtOps.INSTANCE, nbt.get("propellantMassPerTag")).result().orElseGet(HashMap::new));
        }

        if (nbt.contains("normalizedConsumptionRatio")) {
            normalizedConsumptionRatio = new HashMap<>(TAG_TO_FLOAT_CODEC.parse(NbtOps.INSTANCE, nbt.get("normalizedConsumptionRatio")).result().orElseThrow());
        }

        if (!clientPacket && normalizedConsumptionRatio != null) {
            recomputeDeltaV();
        }
    }

    public void resetIdleCargoTracker() {
        ticksSinceLastExchange = 0;
    }

    public void tickIdleCargoTracker() {
        ticksSinceLastExchange++;
    }

    public int getTicksSinceLastExchange() {
        return ticksSinceLastExchange;
    }

    public int getVersion() {
        return version.get();
    }

    void changeDetected() {
        version.incrementAndGet();
        resetIdleCargoTracker();
        recomputeDeltaV();
    }

    /**
     * Recomputes currentDeltaV using the mixture-ratio-limited usable propellant mass.
     * Any propellant beyond what the limiting tag allows is dead weight still present at burnout.
     */
    private void recomputeDeltaV() {
        float usablePropellantMass = computeUsablePropellantMass();
        float deadWeightPropellant = propellantMass - usablePropellantMass;
        if (deadWeightPropellant < 0) deadWeightPropellant = 0; // guard against float drift

        float wetMass = dryMass + inertFluidMass + propellantMass;
        float burnoutMass = dryMass + inertFluidMass + deadWeightPropellant;

        currentDeltaV = (float) (meanVe * Math.log(wetMass / burnoutMass));
    }

    private float computeUsablePropellantMass() {
        if (normalizedConsumptionRatio == null || normalizedConsumptionRatio.isEmpty()) {
            return 0f;
        }

        // Step 1: find the bottleneck advancement — the burn extent limited by the
        // scarcest propellant relative to its share of consumption.
        float minAdvancement = Float.MAX_VALUE;
        for (var entry : normalizedConsumptionRatio.entrySet()) {
            float ratio = entry.getValue();
            if (ratio <= 0) continue; // tag contributes nothing to consumption, ignore it
            float mass = propellantMassPerTag.getOrDefault(entry.getKey(), 0f);
            float advancement = mass / ratio;
            if (advancement < minAdvancement) {
                minAdvancement = advancement;
            }
        }
        if (minAdvancement == Float.MAX_VALUE) {
            return 0f;
        }

        // Step 2: at that advancement, each tag consumes advancement * ratio mass.
        // Sum these to get the actual usable propellant mass.
        float usablePropellantMass = 0f;
        for (float ratio : normalizedConsumptionRatio.values()) {
            usablePropellantMass += minAdvancement * ratio;
        }
        return usablePropellantMass;
    }

    class CargoInvWrapper extends MountedItemStorageWrapper {

        public CargoInvWrapper(MountedItemStorageWrapper wrapped) {
            super(wrapped.storages);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            ItemStack remainder = super.insertItem(slot, stack, simulate);
            if (!simulate && stack.getCount() != remainder.getCount())
                changeDetected();
            return remainder;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack extracted = super.extractItem(slot, amount, simulate);
            if (!simulate && !extracted.isEmpty())
                changeDetected();
            return extracted;
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            if (!stack.equals(getStackInSlot(slot)))
                changeDetected();
            super.setStackInSlot(slot, stack);
        }

    }

    class CargoTankWrapper extends MountedFluidStorageWrapper {

        CargoTankWrapper(MountedFluidStorageWrapper wrapped) {
            super(wrapped.storages);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            int filled = super.fill(resource, action);
            if (action.execute() && filled > 0) {
                onFilled(new FluidStack(resource.getFluid(), filled));
                changeDetected();
            }
            return filled;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            FluidStack drained = super.drain(resource, action);
            if (action.execute() && !drained.isEmpty()) {
                onDrained(drained);
                changeDetected();
            }
            return drained;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            FluidStack drained = super.drain(maxDrain, action);
            if (action.execute() && !drained.isEmpty()) {
                onDrained(drained);
                changeDetected();
            }
            return drained;
        }

    }

    private void onFilled(FluidStack filled) {
        if (normalizedConsumptionRatio == null) return;

        float mass = (float) (filled.getAmount() * filled.getFluid().getFluidType().getDensity()) / 1000;
        Optional<TagKey<Fluid>> tag = getPropTag(filled);
        if (tag.isPresent()) {
            propellantMass += mass;
            propellantMassPerTag.merge(tag.get(), mass, Float::sum);
        } else {
            inertFluidMass += mass;
        }
    }

    private void onDrained(FluidStack drained) {
        if (normalizedConsumptionRatio == null) return;

        float mass = (float) (drained.getAmount() * drained.getFluid().getFluidType().getDensity()) / 1000;
        Optional<TagKey<Fluid>> tag = getPropTag(drained);
        if (tag.isPresent()) {
            propellantMass -= mass;
            propellantMassPerTag.merge(tag.get(), -mass, Float::sum);
        } else {
            inertFluidMass -= mass;
        }
    }

    //should only be called on the server.
    private Optional<TagKey<Fluid>> getPropTag(FluidStack stack) {
        for (TagKey<Fluid> p : propellantMassPerTag.keySet()) {
            if (stack.getFluid().is(p)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    //Only on the server.... -> called once, at contraption assembly.
    private void computeConsumptionData(HashMap<TagKey<Fluid>, RocketContraption.ConsumptionInfo> tptfc) {
        float totalThrust = 0;
        float totalTheoreticalConsumption = 0;
        for (RocketContraption.ConsumptionInfo info : tptfc.values()) {
            totalTheoreticalConsumption += info.fluidConsumption();
            totalThrust += info.partialThrust();
        }

        meanVe = totalTheoreticalConsumption > 0 ? totalThrust / totalTheoreticalConsumption : 0;

        normalizedConsumptionRatio = new HashMap<>();
        for (var entry : tptfc.entrySet()) {
            float ratio = totalTheoreticalConsumption > 0 ? entry.getValue().fluidConsumption() / totalTheoreticalConsumption : 0f;
            normalizedConsumptionRatio.put(entry.getKey(), ratio);
        }

        recomputeDeltaV();
    }
}