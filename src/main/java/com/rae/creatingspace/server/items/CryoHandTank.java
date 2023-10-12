package com.rae.creatingspace.server.items;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CryoHandTank extends Item implements ICapabilityProvider {

    private final FluidTank TANK;

    private final LazyOptional<IFluidHandler> fluidOptional;


    public CryoHandTank(Properties properties,int capacity) {
        super(properties);
        this.TANK = new FluidTank(capacity){
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid().getFluidType().getTemperature() < 100;
            }
        };
        this.fluidOptional = LazyOptional.of(()-> this.TANK);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F - (float)TANK.getFluidAmount() * 13.0F / (float)TANK.getCapacity());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(0.40F, 0.40F, 0.40F);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER_ITEM) {
            return this.fluidOptional.cast();
        }
        return LazyOptional.empty();
    }
}
