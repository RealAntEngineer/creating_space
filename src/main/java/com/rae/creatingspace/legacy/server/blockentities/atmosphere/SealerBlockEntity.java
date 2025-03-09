package com.rae.creatingspace.legacy.server.blockentities.atmosphere;

import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.legacy.server.blocks.atmosphere.OxygenBlock;
import com.rae.creatingspace.legacy.server.blocks.atmosphere.SealerBlock;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SealerBlockEntity extends KineticBlockEntity {
    public int o2amount = 0;
    public int prevO2amount = 0;
    private boolean trying = false;
    private boolean automaticRetry = false;
    public HashSet<BlockPos> oxygenBlockList = new HashSet<>();
    public int lastRoomSize = 0;
    public boolean roomIsSealed = false;//put in private with no set value
    private int remainingTries = 0;
    private int maxTries = 20;
    private int verificationCoolDown;
    private boolean oxygenBlockChange = false;
    private int range = 10;

    public void oxygenBlockChanged() {
        this.oxygenBlockChange = true;
    }

    public void setTrying(boolean trying) {
        this.trying = trying;
    }

    public void setAutomaticRetry(boolean automaticRetry) {
        this.automaticRetry = automaticRetry;
        setChanged();
        sendData();
    }

    public boolean isAutomaticRetry() {
        return automaticRetry;
    }

    public boolean isTrying() {
        return trying;
    }

    public SealerBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public FluidTank OXYGEN_TANK = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {

        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return TagsInit.CustomFluidTags.LIQUID_OXYGEN.matches(stack.getFluid());

        }
    };
    public LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(()-> this.OXYGEN_TANK);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            Direction localDir = this.getBlockState().getValue(SealerBlock.FACING);

            // Check if the side is either the back, top, or bottom
            if (side == localDir.getOpposite() || side == Direction.UP || side == Direction.DOWN) {
                return this.fluidOptional.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    public Boolean isRoomSealable(Level level, BlockPos pos, BlockPos sealerPos, int distanceMax){


        ArrayList<BlockPos> toVisit = new ArrayList<>();

        //prevent placing the first block if it's not a block of air
        if (level.getBlockState(pos).is(Blocks.AIR)) {
            toVisit.add(0, pos);
        }
        else {
            return true;
        }
        // add the block to the world and the block list when finishing visiting the rest
        boolean test = true;

        while (test && !toVisit.isEmpty()) {
            if (distanceSquared(pos, sealerPos) > (distanceMax * distanceMax)) {
                test = false;
            } else {
                pos = toVisit.get(0);
                toVisit.remove(0);

                BlockState newBlock = BlockInit.OXYGEN.getDefaultState()
                        .setValue(OxygenBlock.BREATHABLE, false);
                level.setBlockAndUpdate(pos, newBlock);
                level.setBlockEntity(new OxygenBlockEntity(BlockEntityInit.OXYGEN.get(), pos, newBlock));
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof OxygenBlockEntity oxygenBlockEntity) {
                    oxygenBlockEntity.setMasterPos(sealerPos);
                }
                oxygenBlockList.add(pos);


                for (Direction direction : Direction.values()) {
                    BlockPos newPos = pos.relative(direction);
                    BlockState stateAtDir = level.getBlockState(newPos);

                    if (stateAtDir.is(Blocks.AIR)) {
                        toVisit.add(0,newPos);
                    }
                }
            }
        }
        oxygenBlockChange = false;
        return test;

    }
    public void unSealRoom(){
        this.roomIsSealed = false;
    }

    public void tick(Level level, BlockPos worldPos, BlockState sealerState) {

        if (!level.isClientSide()){

            prevO2amount = o2amount;
            o2amount = OXYGEN_TANK.getFluidAmount();
            setChanged();
            sendData();

            //look for condition speed and O2requirement

            int nbrOfBlock = lastRoomSize;
            //look for consumption
            if (Math.abs(getSpeed())>=speedRequirement(nbrOfBlock) && OXYGEN_TANK.getFluidAmount()>= o2consumption(nbrOfBlock) ){
                if (trying||roomIsSealed) {
                    OXYGEN_TANK.drain(o2consumption(nbrOfBlock), IFluidHandler.FluidAction.EXECUTE);
                }
                tickSealingLogic(level,worldPos,sealerState);

            }
            else {
                unSealRoom();
                resetRemainingTries();
                removeO2inRoom(level);
                setTrying(automaticRetry);
            }

        }
        super.tick();
    }

    public void tickSealingLogic(Level level, BlockPos worldPos, BlockState sealerState) {
        if (!roomIsSealed && trying) {//put this in a separated methode
            if (remainingTries > 0) {

                Direction facing = sealerState.getValue(SealerBlock.FACING);
                removeO2inRoom(level);
                boolean seal;
                if (level.getBlockState(worldPos.relative(facing)).is(Blocks.AIR)) {

                    seal = isRoomSealable(level, worldPos.relative(facing), worldPos, range);
                } else {
                    seal = true;
                }
                if (seal) {

                    // make room breathable if possible
                    makeRoomBreathable(level, worldPos);
                    //make every reset after changing O2 states, so it doesn't make the room unseal itself

                } else {
                    remainingTries--;
                }
            } else {
                removeO2inRoom(level);
                if (!automaticRetry) {
                    setTrying(false);
                    AllSoundEvents.DENY.playAt(level, worldPos, 0.4f, 1, true);
                } else {
                    setTrying(true);
                    resetRemainingTries();
                }

                //lastRoomSize = 0;
            }
        } else if (roomIsSealed) {
            if (!trying) {
                if (oxygenBlockChange) {
                    verificationCoolDown = 0;
                    oxygenBlockChange = false;
                }
                if (verificationCoolDown > 0) {
                    verificationCoolDown--;
                } else {
                    verificationCoolDown = 80;

                    Direction facing = sealerState.getValue(SealerBlock.FACING);
                    removeO2inRoom(level);
                    boolean seal = isRoomSealable(level, worldPos.relative(facing), worldPos, range);

                    if (!seal) {
                        unSealRoom();
                        resetRemainingTries();
                        removeO2inRoom(level);
                        setTrying(automaticRetry);
                    } else {
                        //extract methode
                        makeRoomBreathable(level, worldPos);
                    }
                        /*else {
                            //catching the notify master of OxygenBlock
                            //shouldn't be an issue anymore
                            remainingTries = 0;
                            roomIsSealed = true;
                            trying = false;
                        }*/
                }
            } else if (!isAutomaticRetry()){
                unSealRoom();
                resetRemainingTries();
            }
        }
    }

    private void makeRoomBreathable(Level level, BlockPos worldPos) {
        for (BlockPos oxygenPos : oxygenBlockList) {
            BlockState state = level.getBlockState(oxygenPos);
            if (state.getBlock() instanceof OxygenBlock) {
                level.setBlockAndUpdate(oxygenPos, state.setValue(OxygenBlock.BREATHABLE, true));
            }
        }

        AllSoundEvents.CONFIRM.playAt(level, worldPos, 0.4f, 1, true);


        lastRoomSize = oxygenBlockList.size();

        roomIsSealed = true;
        setTrying(false);
        oxygenBlockChange = false;
    }

    @Override
    protected void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        roomIsSealed = nbt.getBoolean("roomIsSealed");
        automaticRetry = nbt.getBoolean("automaticRetry");
        trying = nbt.getBoolean("isTrying");
        lastRoomSize = nbt.getInt("lastRoomSize");
        range = nbt.getInt("range");
        remainingTries = nbt.getInt("remainingTries");
        maxTries = nbt.getInt("maxTries");
        verificationCoolDown = nbt.getInt("verificationCoolDown");
        oxygenBlockList = ListLongToPos(nbt.getLongArray("oxygenBlockPos"));
        o2amount = nbt.getInt("oxygenAmount");
        prevO2amount = nbt.getInt("prevO2amount");
        OXYGEN_TANK.setFluid(new FluidStack(FluidInit.LIQUID_OXYGEN.get(), nbt.getInt("oxygenAmount")));
    }


    public void removeO2inRoom(Level level){

        for (BlockPos oxygenPos : this.oxygenBlockList) {
            BlockState state = level.getBlockState(oxygenPos);
            if (state.getBlock() instanceof OxygenBlock) {
                level.setBlockAndUpdate(oxygenPos, Blocks.AIR.defaultBlockState());
            }
        }
        oxygenBlockList.clear();
    }


    @Override
    protected void write(CompoundTag nbt, boolean clientPacket) {
        nbt.putBoolean("roomIsSealed",roomIsSealed);
        nbt.putBoolean("automaticRetry",automaticRetry);
        nbt.putBoolean("isTrying", trying);
        nbt.putInt("lastRoomSize",lastRoomSize);
        nbt.putInt("range",range);
        nbt.putInt("remainingTries",remainingTries);
        nbt.putInt("maxTries",maxTries);
        nbt.putInt("verificationCoolDown",verificationCoolDown);
        nbt.putInt("oxygenAmount",OXYGEN_TANK.getFluidAmount());
        nbt.putInt("prevO2amount",prevO2amount);
        nbt.putLongArray("oxygenBlockPos", ListPosToLong(oxygenBlockList));
        super.write(nbt, clientPacket);
    }

    private int distanceSquared(BlockPos pos1, BlockPos pos2){
        return (pos1.getX()-pos2.getX())*(pos1.getX()-pos2.getX())
                + (pos1.getY()-pos2.getY())*(pos1.getY()-pos2.getY())
                + (pos1.getZ()-pos2.getZ())*(pos1.getZ()-pos2.getZ());
    }

    public void resetRemainingTries(){
        this.remainingTries  = this.maxTries;
    }
    private List<Long> ListPosToLong(HashSet<BlockPos> blockPosList) {

        ArrayList<Long> posList = new ArrayList<>();
        for (BlockPos pos : blockPosList) {
            posList.add(pos.asLong());
        }
        return  posList;
    }
    private HashSet<BlockPos> ListLongToPos(long[] blockPosList) {

        HashSet<BlockPos> posList = new HashSet<>();
        for (Long posLong : blockPosList) {
            posList.add(BlockPos.of(posLong));
        }
        return  posList;
    }

    public static int speedRequirement(int nbrOfBlock){

        int requirement = Math.max(nbrOfBlock/20,16);

        return Math.min(requirement, AllConfigs.server().kinetics.maxRotationSpeed.get());
    }

    public static int o2consumption(int nbrOfBlock){
        return Math.max(nbrOfBlock/20,1);
    }


    public void setSettings(int range, boolean isAutomaticRetry) {
        this.range = range;
        setAutomaticRetry(isAutomaticRetry);
    }

    public int getRange() {
        return this.range;
    }
}
