package com.rae.creatingspace.server.blockentities;

import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.rae.creatingspace.server.blocks.ChemicalSynthesizerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChemicalSynthesizerBlockEntity extends BlockEntity /*implements MenuProvider*/ {

    //doesn't load item with a hopper, but unload them and work both way with Create's funnel so no problem
    private final ItemStackHandler inventory = new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            ChemicalSynthesizerBlockEntity.this.setChanged();
            super.onContentsChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot){
                case 0 -> stack.getItem() == ItemInit.COAL_DUST.get();
                default -> super.isItemValid(slot, stack);
            };

        }
    };

    private final LazyOptional<IItemHandlerModifiable> itemOptional = LazyOptional.of(() -> this.inventory);

    private int progress = 0;
    private int maxProgress = 80;

    public ChemicalSynthesizerBlockEntity(BlockEntityType<?> type,BlockPos pos, BlockState state) {
        super(type,pos, state);
    }

    public static void  tick(Level level, BlockPos pos, BlockState state, ChemicalSynthesizerBlockEntity synthesizerBlockEntity) {
        //verifying the recipe then craft methane
        if(level.isClientSide()){
            return ;
        }
        if(hasRecipe(synthesizerBlockEntity)){
            synthesizerBlockEntity.progress++;
            setChanged(level,pos,state);

            if(synthesizerBlockEntity.progress >= synthesizerBlockEntity.maxProgress){
                craftFluid(synthesizerBlockEntity);
            }
        } else {
            synthesizerBlockEntity.resetProgress();
            setChanged(level,pos,state);
        }
    }

    private void resetProgress() {
        this.progress =0;
    }

    private static void craftFluid(ChemicalSynthesizerBlockEntity synthesizerBlockEntity) {
        if(hasRecipe(synthesizerBlockEntity)) {
            //System.out.print(synthesizerBlockEntity.METHANE_TANK.getFluidAmount());
            synthesizerBlockEntity.inventory.extractItem(0,1,false);
            synthesizerBlockEntity.HYDROGEN_TANK.drain(100,IFluidHandler.FluidAction.EXECUTE);
            synthesizerBlockEntity.METHANE_TANK.fill(new FluidStack(FluidInit.LIQUID_METHANE.get(),100), IFluidHandler.FluidAction.EXECUTE);
            //System.out.print(synthesizerBlockEntity.METHANE_TANK.isFluidValid(new FluidStack(FluidInit.LIQUID_METHANE.get(),100)));
            //synthesizerBlockEntity.fluidHandler.set...
            synthesizerBlockEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(ChemicalSynthesizerBlockEntity synthesizerBlockEntity) {
        SimpleContainer inventory = new SimpleContainer(synthesizerBlockEntity.inventory.getSlots());

        for (int i = 0;i < synthesizerBlockEntity.inventory.getSlots();i++){
            inventory.setItem(i,synthesizerBlockEntity.inventory.getStackInSlot(i));
        }

        boolean hasHydrogenInTank = synthesizerBlockEntity.HYDROGEN_TANK.getFluidAmount()>=100;
        boolean methaneTankNotFull = synthesizerBlockEntity.METHANE_TANK.getSpace() >= 100;
        boolean hasCarbonInSlot = synthesizerBlockEntity.inventory.getStackInSlot(0).getItem() == ItemInit.COAL_DUST.get();
        return hasHydrogenInTank && hasCarbonInSlot && methaneTankNotFull;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        HYDROGEN_TANK.setFluid(new FluidStack(FluidInit.LIQUID_HYDROGEN.get(), nbt.getInt("hydrogenAmount")));
        METHANE_TANK.setFluid(new FluidStack(FluidInit.LIQUID_METHANE.get(), nbt.getInt("methaneAmount")));
    }


    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putInt("hydrogenAmount", HYDROGEN_TANK.getFluidAmount());
        nbt.putInt("methaneAmount", METHANE_TANK.getFluidAmount());
        super.saveAdditional(nbt);
    }

    public void drop() {
        SimpleContainer inventory = new SimpleContainer(this.inventory.getSlots());
        for(int i = 0; i < this.inventory.getSlots(); i++){
            inventory.setItem(i, this.inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level,this.worldPosition,inventory);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemOptional.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER){
            //only south for hydrogen input and north for methane output
            Direction localDir = this.getBlockState().getValue(ChemicalSynthesizerBlock.FACING);

            if (localDir == side.getOpposite()){
                return this.hydrogenFluidOptional.cast();
            }
            if (localDir == side){
                return this.methaneFluidOptional.cast();
            }
        }
        return super.getCapability(cap, side);
    }




    //to delete
    /*private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0-> ChemicalSynthesizerBlockEntity.this.progress;
                case 1-> ChemicalSynthesizerBlockEntity.this.maxProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0-> ChemicalSynthesizerBlockEntity.this.progress = value;
                case 1-> ChemicalSynthesizerBlockEntity.this.maxProgress = value;
                default -> {}
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public ContainerData getContainerData() {
        return this.data;
    }*/

    public ItemStackHandler getInventory() {
        return inventory;
    }

    //fluid


    private final LazyOptional<IFluidHandler> hydrogenFluidOptional = LazyOptional.of(()-> this.HYDROGEN_TANK);
    private final FluidTank HYDROGEN_TANK  = new FluidTank(2000){
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == FluidInit.LIQUID_HYDROGEN.getSource();
        }
    };

    private final LazyOptional<IFluidHandler> methaneFluidOptional = LazyOptional.of(()-> this.METHANE_TANK);
    private final FluidTank METHANE_TANK  = new FluidTank(2000){
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == FluidInit.LIQUID_METHANE.get();
        }

    };

    public int getMethaneAmount() {
        return this.METHANE_TANK.getFluidAmount();
    }

    public int getHydrogenAmount() {
        return this.HYDROGEN_TANK.getFluidAmount();
    }

}
