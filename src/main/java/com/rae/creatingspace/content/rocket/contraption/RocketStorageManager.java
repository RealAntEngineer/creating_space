package com.rae.creatingspace.content.rocket.contraption;

import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.minecart.TrainCargoManager;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RocketStorageManager extends MountedStorageManager {

    int ticksSinceLastExchange;
    AtomicInteger version;
    private HashMap<PropellantType, RocketContraption.ConsumptionInfo> theoreticalPerTagFluidConsumption = new HashMap<>();
    private final ArrayList<TagKey<Fluid>> listOfPropellantFluid = new ArrayList<>();

    float currentDeltaV;
    float dryMass;
    float inertFluidMass;
    float propellantMass;
    float meanVe;

    /**
     * called just after the contraption is search and validated for assembly, before the inventory is wrapped
     */
    public void onContraptionAssemble(RocketContraption rocketContraption) {
        this.theoreticalPerTagFluidConsumption = rocketContraption.getTPTFluidConsumption();
        this.theoreticalPerTagFluidConsumption.keySet().forEach( p -> listOfPropellantFluid.addAll(p.getPropellantRatio().keySet()));
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
        calculateVe();
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        nbt.putInt("TicksSinceLastExchange", ticksSinceLastExchange);
        nbt.putFloat("currentDeltaV",currentDeltaV);
        nbt.putFloat("dryMass",dryMass);
        nbt.putFloat("inertFluidMass",inertFluidMass);
        nbt.putFloat("propellantMass",propellantMass);
        try {
            nbt.put("theoreticalPerTagFluidConsumption",RocketContraption.CODEC.encodeStart(NbtOps.INSTANCE,theoreticalPerTagFluidConsumption).result().orElseThrow());
        } catch (Exception ignored){

        }
    }

    @Override
    public void read(CompoundTag nbt,boolean clientPacket,Contraption contraption) {
        super.read(nbt,clientPacket, contraption);
        ticksSinceLastExchange = nbt.getInt("TicksSinceLastExchange");
        currentDeltaV = nbt.getFloat("currentDeltaV");
        dryMass = nbt.getFloat("dryMass");
        inertFluidMass = nbt.getFloat("inertFluidMass");
        propellantMass = nbt.getFloat("propellantMass");
        try {
            theoreticalPerTagFluidConsumption = new HashMap<>(RocketContraption.CODEC.parse(NbtOps.INSTANCE, nbt.get("theoreticalPerTagFluidConsumption")).result().orElseThrow());
            theoreticalPerTagFluidConsumption.keySet().forEach( p -> listOfPropellantFluid.addAll(p.getPropellantRatio().keySet()));
            calculateVe();
        } catch (Exception ignored) {

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
        currentDeltaV = (float) (meanVe * Math.log((dryMass+propellantMass)/(dryMass+propellantMass+inertFluidMass)));
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
                onFilled(new FluidStack(resource.getFluid(),filled));
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
        if (isProp(filled)){
            propellantMass += (float) (filled.getAmount() * filled.getFluid().getFluidType().getDensity()) /1000;
        } else {
            inertFluidMass += (float) (filled.getAmount() * filled.getFluid().getFluidType().getDensity()) /1000;

        }
    }
    private void onDrained(FluidStack drained) {
        if (isProp(drained)){
            propellantMass -= (float) (drained.getAmount() * drained.getFluid().getFluidType().getDensity()) /1000;
        } else {
            inertFluidMass -= (float) (drained.getAmount() * drained.getFluid().getFluidType().getDensity()) /1000;

        }
    }
    private boolean isProp(FluidStack stack){
        AtomicBoolean flag = new AtomicBoolean(false);
        listOfPropellantFluid.forEach(p -> {
            if (stack.getFluid().is(p)){
                flag.set(true);
            }
        });
        return flag.get();
    }
    private void calculateVe(){
        float totalThrust = 0;
        float totalTheoreticalConsumption = 0;
        for (PropellantType combination : theoreticalPerTagFluidConsumption.keySet()) {
            RocketContraption.ConsumptionInfo info = theoreticalPerTagFluidConsumption.get(combination);
            //mean speed of ejected gasses for the fluid -> need to be done for a couple of tag -> ox/fuel
            for (float consumption :
                    info.propellantConsumption().values()) {
                totalTheoreticalConsumption += consumption;
            }
            totalThrust += info.partialThrust();
        }
        meanVe = totalThrust/totalTheoreticalConsumption;
        currentDeltaV = (float) (meanVe * Math.log((dryMass+propellantMass+inertFluidMass)/(dryMass+inertFluidMass)));
        System.out.println("deltaV : "+currentDeltaV);
    }
}