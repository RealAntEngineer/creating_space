package com.rae.creatingspace.server.items;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CryoHandTank extends Item implements ICapabilityProvider {
    public CryoHandTank(Properties properties) {
        super(properties);
    }

    // to complicated keep this but effort put toward oxygen tank for space
    public FluidTank TANK = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            System.out.println("got a fluid : " + stack.getFluid()+ "  " + stack.getAmount());
            return stack.getFluid().getFluidType().getTemperature() < 200;
        }
    };

    public LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(()-> this.TANK);

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        ((CryoHandTank) stack.getItem()).TANK.setFluid(FluidStack.EMPTY);
        return stack;
    }

    public static CompoundTag getRemainingAir(ItemStack stack) {
        CompoundTag orCreateTag = stack.getOrCreateTag();
        return orCreateTag.getCompound("TankContent");
    }



    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float)TANK.getFluidAmount() * 13.0F / (float)TANK.getCapacity());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int w = getBarWidth(stack);
        return Mth.hsvToRgb((0.40F+w)/w, 0.40F, 0.40F);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        System.out.println("someone asked for capability : " );
        System.out.println(cap);
        if (cap == ForgeCapabilities.FLUID_HANDLER_ITEM) {

            return this.fluidOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (level.getBlockState(pos).is(AllBlocks.FLUID_TANK.get())){
            FluidTankBlock fluidTankBlock = (FluidTankBlock) level.getBlockState(pos).getBlock();
            FluidTankBlockEntity be = ConnectivityHandler.partAt(fluidTankBlock.getBlockEntityType(), level, pos);
            if (be == null)
                return InteractionResult.FAIL;

            IFluidTank tank = be.getTank(0);
            if (context.isSecondaryUseActive()){ // drain tank to item
                if (!tank.getFluid().isEmpty()){
                    int space = TANK.getSpace();
                    int fillAmount = Math.min(space, tank.getFluidAmount());

                    if (TANK.isEmpty() || TANK.getFluid().getFluid() == tank.getFluid().getFluid()) {
                        if (TANK.isFluidValid(tank.getFluid())) {
                            TANK.fill(new FluidStack(tank.getFluid(), fillAmount), IFluidHandler.FluidAction.EXECUTE);
                            tank.drain(fillAmount, IFluidHandler.FluidAction.EXECUTE);
                            return InteractionResult.SUCCESS;
                        }
                        else {
                            return InteractionResult.FAIL;
                        }
                    }
                    else {
                        return InteractionResult.FAIL;
                    }
                }
                else {
                    return InteractionResult.FAIL;
                }
            }
            else {
                if (!TANK.getFluid().isEmpty()){
                    int space = tank.getCapacity()-tank.getFluidAmount();
                    int fillAmount = Math.min(space, TANK.getFluidAmount());
                    System.out.println("Try to fill a tank 1");

                    if (tank.getFluidAmount()==0 || TANK.getFluid().getFluid() == tank.getFluid().getFluid()) {
                        System.out.println("Try to fill a tank 2");
                        tank.fill(new FluidStack(tank.getFluid(),fillAmount), IFluidHandler.FluidAction.EXECUTE);
                        TANK.drain(fillAmount, IFluidHandler.FluidAction.EXECUTE);
                        return InteractionResult.SUCCESS;
                    }
                    else {
                        return InteractionResult.FAIL;
                    }
                }
                else {
                    return InteractionResult.FAIL;
                }

            }

        }
        return super.useOn(context);
    }
}
