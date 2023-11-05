package com.rae.creatingspace.server.blockentities;

import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CryogenicTankBlockEntity extends SmartBlockEntity implements Nameable {
    private final Component defaultName;
    private Component customName;
    public CryogenicTankBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        defaultName = getDefaultName();

    }
    public static Component getDefaultName() {

        return BlockInit.CRYOGENIC_TANK.get().getName();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public FluidTank TANK = new FluidTank(4000) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().getFluidType().getTemperature() < 200;
        }
    };

    public LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(()-> this.TANK);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.fluidOptional.cast();
        }
        return super.getCapability(cap, side);
    }


    @Override
    public Component getName() {
        return this.customName != null ? this.customName
                : defaultName;
    }
    public void setCustomName(Component customName) {
        this.customName = customName;
    }

    public Component getCustomName() {
        return customName;
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        CompoundTag tankTag = new CompoundTag();
        tag.put("Tank", TANK.writeToNBT(tankTag));
        super.write(tag, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        TANK.readFromNBT((CompoundTag) tag.get("Tank"));
    }


    public void setTank(CompoundTag tag) {
        TANK.readFromNBT(tag);
    }

    public FluidTank getTank() {
        return TANK;
    }
}
