package com.rae.creatingspace.content.rocket.contraption.entity;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.minecraft.core.BlockPos;
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

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RocketStorageManager extends MountedStorageManager {

    int           ticksSinceLastExchange;
    AtomicInteger version;
    float currentDeltaV;
    //private final ArrayList<TagKey<Fluid>> listOfPropellantFluid = new ArrayList<>();
    float dryMass;
    float inertFluidMass;
    float propellantMass;
    float meanVe;
    //you shouldn't calculate DeltaV if this is null.
    private HashMap<TagKey<Fluid>, RocketContraption.ConsumptionInfo> theoreticalPerTagFluidConsumption = null;

    public RocketStorageManager() {
        version = new AtomicInteger();
        ticksSinceLastExchange = 0;
    }

    /**
     * called just after the contraption is search and validated for assembly, before the inventory is wrapped
     */
    public void onContraptionAssemble(RocketContraption rocketContraption) {
        this.theoreticalPerTagFluidConsumption = rocketContraption.getTPTFluidConsumption();
        /*this.theoreticalPerTagFluidConsumption.keySet().forEach(location -> {
             if (location != null) {
                        listOfPropellantFluid.add( location);
                    }

        });*/

        dryMass = rocketContraption.getDryMass();

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

    @Override
    public void initialize() {
        super.initialize();
        this.items = new CargoInvWrapper(this.items);
        if (this.fuelItems != null) {
            this.fuelItems = new CargoInvWrapper(this.fuelItems);
        }
        this.fluids = new CargoTankWrapper(this.fluids);
        IFluidHandler fluidHandler = getFluids();
        int           nbrOfTank    = fluidHandler.getTanks();
        //!! O(nbr_tank*nbr_prop)

        for (int i = 0; i < nbrOfTank; i++) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
            onFilled(fluidInTank);
        }
        if (theoreticalPerTagFluidConsumption != null) {
            calculateVe();
        }
    }

    @Override
    public void addBlock(Level level, BlockState state, BlockPos globalPos, BlockPos localPos, @Nullable BlockEntity be) {
        super.addBlock(level, state, globalPos, localPos, be);
        //TODO put the initialisation of the mass, and of the thrust, consumptions... here instead of the contraption.
    }

    @Override
    public void read(CompoundTag nbt, boolean spawnPacket, Contraption contraption) {//lookup and write here what spawnPacket actually means
        super.read(nbt, spawnPacket, contraption);
        ticksSinceLastExchange = nbt.getInt("TicksSinceLastExchange");
        currentDeltaV = nbt.getFloat("currentDeltaV");
        dryMass = nbt.getFloat("dryMass");
        inertFluidMass = nbt.getFloat("inertFluidMass");
        propellantMass = nbt.getFloat("propellantMass");
        //Should we do a sync with the client ?
        if (!spawnPacket) {
            theoreticalPerTagFluidConsumption = new HashMap<>(RocketContraption.TPTF_CODEC.parse(NbtOps.INSTANCE, nbt.get("theoreticalPerTagFluidConsumption")).result().orElseThrow());
            calculateVe();
        }
    }

    @Override
    public void write(CompoundTag nbt, boolean spawnPacket) {
        super.write(nbt, spawnPacket);
        nbt.putInt("TicksSinceLastExchange", ticksSinceLastExchange);
        if (!spawnPacket) {
            nbt.putFloat("currentDeltaV", currentDeltaV);
            nbt.putFloat("dryMass", dryMass);
            nbt.putFloat("inertFluidMass", inertFluidMass);
            nbt.putFloat("propellantMass", propellantMass);
            nbt.put("theoreticalPerTagFluidConsumption", RocketContraption.TPTF_CODEC.encodeStart(NbtOps.INSTANCE, theoreticalPerTagFluidConsumption).result().orElseThrow());
        }
    }

    private void onFilled(FluidStack filled) {
        if (theoreticalPerTagFluidConsumption != null) {
            if (isProp(filled)) {
                propellantMass += (float) (filled.getAmount() * filled.getFluid().getFluidType().getDensity()) / 1000;
            } else {
                inertFluidMass += (float) (filled.getAmount() * filled.getFluid().getFluidType().getDensity()) / 1000;
            }
        }
    }

    //Only on the server.... -> should be called each times the fluid inventory changed
    private void calculateVe() {
        float totalThrust                 = 0;
        float totalTheoreticalConsumption = 0;
        for (TagKey<Fluid> fluidTagKey : theoreticalPerTagFluidConsumption.keySet()) {
            RocketContraption.ConsumptionInfo info = theoreticalPerTagFluidConsumption.get(fluidTagKey);
            //mean speed of ejected gasses for the fluid -> need to be done for a couple of tag -> ox/fuel

            totalTheoreticalConsumption += info.fluidConsumption();
            totalThrust += info.partialThrust();
        }
        //TODO, would probably be better if we have the ISP and the thrust -> no division so less possibility of errors
        meanVe = totalTheoreticalConsumption > 0 ? totalThrust / totalTheoreticalConsumption : 0;
        currentDeltaV = (float) (meanVe * Math.log((dryMass + propellantMass + inertFluidMass) / (dryMass + inertFluidMass)));
        System.out.println("propellant mass : " + propellantMass);
        System.out.println("inert fluids mass : " + inertFluidMass);
        System.out.println("meanVe : " + meanVe);
        System.out.println("deltaV : " + currentDeltaV);
    }

    //should only be called on the server.
    private boolean isProp(FluidStack stack) {
        assert theoreticalPerTagFluidConsumption != null;
        for (TagKey<Fluid> p : theoreticalPerTagFluidConsumption.keySet()) {
            if (stack.getFluid().is(p)) {
                return true;
            }
        }
        return false;
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
        currentDeltaV = (float) (meanVe * Math.log((dryMass + propellantMass) / (dryMass + propellantMass + inertFluidMass)));
    }

    public void resetIdleCargoTracker() {
        ticksSinceLastExchange = 0;
    }

    private void onDrained(FluidStack drained) {
        if (theoreticalPerTagFluidConsumption != null) {
            if (isProp(drained)) {
                propellantMass -= (float) (drained.getAmount() * drained.getFluid().getFluidType().getDensity()) / 1000;
            } else {
                inertFluidMass -= (float) (drained.getAmount() * drained.getFluid().getFluidType().getDensity()) / 1000;

            }
        }
    }

    class CargoInvWrapper extends MountedItemStorageWrapper {

        public CargoInvWrapper(MountedItemStorageWrapper wrapped) {
            super(wrapped.storages);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            if (!stack.equals(getStackInSlot(slot)))
                changeDetected();
            super.setStackInSlot(slot, stack);
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
}