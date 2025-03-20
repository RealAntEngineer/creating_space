package com.rae.creatingspace.legacy.server.blockentities;

import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.rae.creatingspace.legacy.server.blocks.ChemicalSynthesizerBlock;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
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

import java.util.List;

public class ChemicalSynthesizerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation/*implements MenuProvider*/ {

    //doesn't load item with a hopper, but unload them and work both way with Create's funnel so no problem

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

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public void  tick(Level level, BlockPos pos, BlockState state, ChemicalSynthesizerBlockEntity synthesizerBlockEntity) {
        //verifying the recipe then craft methane

        super.tick();
        if (!level.isClientSide()) {
            if (syncCooldown > 0) {
                syncCooldown--;
                if (syncCooldown == 0 && queuedSync)
                    sendData();
            }
            if (hasRecipe(synthesizerBlockEntity)) {
                synthesizerBlockEntity.progress++;
                notifyUpdate();

                if (synthesizerBlockEntity.progress >= synthesizerBlockEntity.maxProgress) {
                    craftFluid(synthesizerBlockEntity);
                }
            } else {
                synthesizerBlockEntity.resetProgress();
                notifyUpdate();
            }
        }
    }

    private void resetProgress() {
        this.progress =0;
    }
    static int amount = 20;
    private static void craftFluid(ChemicalSynthesizerBlockEntity synthesizerBlockEntity) {
        if(hasRecipe(synthesizerBlockEntity)) {

            synthesizerBlockEntity.inventory.extractItem(0,1,false);
            synthesizerBlockEntity.HYDROGEN_TANK.drain((int) (amount*4f/FluidInit.LIQUID_HYDROGEN.getType().getDensity()*1000f),IFluidHandler.FluidAction.EXECUTE);
            Fluid fluid = FluidInit.LIQUID_METHANE.get();
            if (!synthesizerBlockEntity.METHANE_TANK.isEmpty())
                fluid = synthesizerBlockEntity.METHANE_TANK.getFluid().getFluid();
            synthesizerBlockEntity.METHANE_TANK.fill(
                    new FluidStack(fluid, (int) (amount*16f/FluidInit.LIQUID_METHANE.getType().getDensity()*1000f)), IFluidHandler.FluidAction.EXECUTE);
            synthesizerBlockEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(ChemicalSynthesizerBlockEntity synthesizerBlockEntity) {
        SimpleContainer inventory = new SimpleContainer(synthesizerBlockEntity.inventory.getSlots());

        for (int i = 0;i < synthesizerBlockEntity.inventory.getSlots();i++){
            inventory.setItem(i,synthesizerBlockEntity.inventory.getStackInSlot(i));
        }

        boolean hasHydrogenInTank = synthesizerBlockEntity.HYDROGEN_TANK.getFluidAmount()>=amount*4f/FluidInit.LIQUID_HYDROGEN.getType().getDensity()*1000f;
        boolean methaneTankNotFull = synthesizerBlockEntity.METHANE_TANK.getSpace() >= amount*16f/FluidInit.LIQUID_METHANE.getType().getDensity()*1000f;
        boolean hasCarbonInSlot = synthesizerBlockEntity.inventory.getStackInSlot(0).getItem() == ItemInit.COAL_DUST.get();
        return hasHydrogenInTank && hasCarbonInSlot && methaneTankNotFull;
    }

    @Override
    public void read(CompoundTag nbt,boolean clientPacket) {
        super.read(nbt,clientPacket);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        HYDROGEN_TANK.setFluid(new FluidStack(FluidInit.LIQUID_HYDROGEN.get(), nbt.getInt("hydrogenAmount")));
        METHANE_TANK.setFluid(new FluidStack(FluidInit.LIQUID_METHANE.get(), nbt.getInt("methaneAmount")));
    }


    @Override
    protected void write(CompoundTag nbt,boolean clientPacket) {
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putInt("hydrogenAmount", HYDROGEN_TANK.getFluidAmount());
        nbt.putInt("methaneAmount", METHANE_TANK.getFluidAmount());
        super.write(nbt,clientPacket);
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
            return TagsInit.CustomFluidTags.LIQUID_HYDROGEN
                            .matches(stack.getFluid());
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

            return TagsInit.CustomFluidTags.LIQUID_METHANE.matches(stack.getFluid());
        }

    };

    public int getMethaneAmount() {
        return this.METHANE_TANK.getFluidAmount();
    }

    public int getHydrogenAmount() {
        return this.HYDROGEN_TANK.getFluidAmount();
    }


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = Lang.translate("generic.unit.millibuckets");
        LangBuilder mbs = Lang.translate("generic.unit.fluidflow");
        Lang.translate("gui.goggles.fluid_container")
                .forGoggles(tooltip);

        for (int i = 0; i <= 1; i++) {
            FluidTank tank = switch (i){
                case 0 -> METHANE_TANK;
                case 1 -> HYDROGEN_TANK;
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };
            String fluidName = switch (i){
                case 0 -> FluidInit.LIQUID_METHANE.getType().getDescriptionId();
                case 1 -> FluidInit.LIQUID_HYDROGEN.getType().getDescriptionId();
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
        return true;
    }

}
