package com.rae.creatingspace.legacy.server.blockentities;


import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.legacy.server.blocks.RocketGeneratorBlock;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
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

public class RocketGeneratorBlockEntity extends GeneratingKineticBlockEntity {
    public RocketGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type,pos, state);
    }

    @Override
    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        super.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    @Override
    public void initialize() {
        super.initialize();
        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
            updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {
        if (this.getBlockState().getValue(RocketGeneratorBlock.GENERATING)) {
            //int speed = calculateSpeed();
            //prevCalculatedSpeed = speed;
            return 64;
        } else {
            //prevCalculatedSpeed = 0;
            return 0;
        }
    }
    @Override
    protected void write(CompoundTag nbt, boolean clientPacket) {
        nbt.putInt("oxygenAmount",OXYGEN_TANK.getFluidAmount());
        nbt.putInt("methaneAmount",METHANE_TANK.getFluidAmount());
        super.write(nbt, clientPacket);
    }

    @Override
    protected void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        METHANE_TANK.setFluid(new FluidStack(FluidInit.LIQUID_METHANE.get(), nbt.getInt("methaneAmount")));
        OXYGEN_TANK.setFluid(new FluidStack(FluidInit.LIQUID_OXYGEN.get(), nbt.getInt("oxygenAmount")));

    }



    //fluid
    private final LazyOptional<IFluidHandler> ofluidOptional = LazyOptional.of(()-> this.OXYGEN_TANK);
    private final FluidTank OXYGEN_TANK = new FluidTank(1000){
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getRawFluid().isSame(FluidInit.LIQUID_OXYGEN.get());
        }
    };

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.FLUID_HANDLER){
            //only south for oxygen input and north for methane output
            Direction localDir = this.getBlockState().getValue(RocketGeneratorBlock.H_FACING);

            if (localDir == side.getCounterClockWise(Direction.Axis.Y)){
                return this.ofluidOptional.cast();
            }
            if (localDir == side.getClockWise(Direction.Axis.Y)){
                return this.mfluidOptional.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    private final LazyOptional<IFluidHandler> mfluidOptional = LazyOptional.of(()-> this.METHANE_TANK);
    private final FluidTank METHANE_TANK  = new FluidTank(1000){
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getRawFluid().isSame(FluidInit.LIQUID_METHANE.get());
        }
    };


    public void tick(Level level, BlockPos pos, BlockState state, RocketGeneratorBlockEntity blockEntity) {
        super.tick();

        if (!level.isClientSide()) {
            if (syncCooldown > 0) {
                syncCooldown--;
                if (syncCooldown == 0 && queuedSync)
                    sendData();
                }
            if (state.getValue(RocketGeneratorBlock.GENERATING)) {
                consumePropellant();
                if (OXYGEN_TANK.isEmpty() || METHANE_TANK.isEmpty()) {
                    BlockState newBlockState = state.setValue(RocketGeneratorBlock.GENERATING, false);
                    level.setBlockAndUpdate(pos, newBlockState);
                    this.updateGeneratedRotation();

                }
            }
        }

    }


    private void consumePropellant(){
        float o2Coef = 4 * 1000f /FluidInit.LIQUID_OXYGEN.getType().getDensity();
        float methCoef = 1000f /FluidInit.LIQUID_METHANE.getType().getDensity();

        int amount = 20;

        OXYGEN_TANK.drain((int) (amount*o2Coef), IFluidHandler.FluidAction.EXECUTE);
        METHANE_TANK.drain((int) (amount*methCoef), IFluidHandler.FluidAction.EXECUTE);
        sendData();

    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = Lang.translate("generic.unit.millibuckets");
        LangBuilder mbs = Lang.translate("generic.unit.fluidflow");
        Lang.translate("gui.goggles.fluid_container")
                .forGoggles(tooltip);

        for (int i = 0; i <= 1; i++) {
            FluidTank tank = switch (i){
                case 0 -> OXYGEN_TANK;
                case 1 -> METHANE_TANK;
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };
            String fluidName = switch (i){
                case 0 -> FluidInit.LIQUID_OXYGEN.getType().getDescriptionId();
                case 1 -> FluidInit.LIQUID_METHANE.getType().getDescriptionId();
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };

            FluidStack fluidStack = tank.getFluidInTank(0);

            Lang.builder().add(Component.translatable(fluidName))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);

            Lang.builder()
                    .add(Lang.number(fluidStack.getAmount())
                            .add(mb)
                            .style(ChatFormatting.GOLD))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(Lang.number(tank.getTankCapacity(0))
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        }
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
