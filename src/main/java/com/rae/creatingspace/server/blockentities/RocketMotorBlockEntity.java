package com.rae.creatingspace.server.blockentities;


import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.server.blocks.RocketMotorBlock;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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

public class RocketMotorBlockEntity extends GeneratingKineticBlockEntity {
    //to fix
    public RocketMotorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type,pos, state);
        setLazyTickRate(20);
    }
    private int progress = 3;
    private final int maxProgress = 3;//for an average
    private int prevCalculatedSpeed;

    //generate fixed speed and stress -> 200000 SU at 64 rps
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
        if (this.getBlockState().getValue(RocketMotorBlock.GENERATING)) {
            int speed = calculateSpeed();
            prevCalculatedSpeed = speed;
            return speed;
        } else {
            prevCalculatedSpeed = 0;
            return 0;
        }
    }
    @Override
    protected void write(CompoundTag nbt, boolean clientPacket) {
        nbt = OXYGEN_TANK.writeToNBT(nbt);
        nbt = METHANE_TANK.writeToNBT(nbt);
        super.write(nbt, clientPacket);
    }

    @Override
    protected void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        OXYGEN_TANK.readFromNBT(nbt);
        METHANE_TANK.readFromNBT(nbt);
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
            Direction localDir = this.getBlockState().getValue(RocketMotorBlock.FACING);

            if (localDir == side.getCounterClockWise()){
                return this.ofluidOptional.cast();
            }
            if (localDir == side.getClockWise()){
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


    public void tick(Level level, BlockPos pos, BlockState state, RocketMotorBlockEntity blockEntity) {
        if (!level.isClientSide()) {
            if (syncCooldown > 0) {
                syncCooldown--;
                if (syncCooldown == 0 && queuedSync)
                    sendData();
                }
        }

        super.tick();
    }

    private int calculateSpeed() {
        if (progress < maxProgress){
            progress++;
            return prevCalculatedSpeed;
        }
        else {
            progress  = 0;
            int o2amount = OXYGEN_TANK.getFluidAmount()/2;
            int methaneAmount = METHANE_TANK.getFluidAmount()/2;
            int o2Coef = 1000*2*16/FluidInit.LIQUID_OXYGEN.getType().getDensity();
            int methCoef = 1000*16/FluidInit.LIQUID_METHANE.getType().getDensity();
            int max;
            if (o2amount/o2Coef > methaneAmount/methCoef){
                max = o2amount/o2Coef;
            }
            else {
                max = methaneAmount/methCoef;
            }
            if (max == 0){
                this.getBlockState().setValue(RocketMotorBlock.GENERATING,false);
                return 0;
            }
            OXYGEN_TANK.drain(max*o2Coef, IFluidHandler.FluidAction.EXECUTE);
            METHANE_TANK.drain(max*methCoef, IFluidHandler.FluidAction.EXECUTE);
            float newSpeed = max/1000f*256;
            if (newSpeed == 0){
                return 1;
            }
            return (int) newSpeed;
        }
    }

}
