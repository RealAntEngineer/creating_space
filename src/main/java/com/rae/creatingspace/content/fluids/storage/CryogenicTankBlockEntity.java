package com.rae.creatingspace.content.fluids.storage;

import com.rae.creatingspace.init.ingameobject.BlockInit;
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
import net.minecraft.world.Nameable;
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

import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

public class CryogenicTankBlockEntity extends SmartBlockEntity implements Nameable, IHaveGoggleInformation {
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
            notifyUpdate();
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
        tag.put(FLUID_NBT_KEY, TANK.writeToNBT(tankTag));
        super.write(tag, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        TANK.readFromNBT((CompoundTag) tag.get(FLUID_NBT_KEY));
    }


    public void setTank(CompoundTag tag) {
        TANK.readFromNBT(tag);
    }

    public FluidTank getTank() {
        return TANK;
    }
    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    public void  tick(Level level, BlockPos pos, BlockState state, CryogenicTankBlockEntity cryogenicTankBlockEntity) {
        super.tick();
        if (!level.isClientSide()) {
            if (syncCooldown > 0) {
                syncCooldown--;
                if (syncCooldown == 0 && queuedSync)
                    sendData();
            }
        }
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

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = Lang.translate("generic.unit.millibuckets");
        LangBuilder mbs = Lang.translate("generic.unit.fluidflow");
        Lang.translate("gui.goggles.fluid_container")
                .forGoggles(tooltip);

            FluidTank tank = TANK;
            String fluidName = TANK.getFluid().getFluid().getFluidType().getDescriptionId();

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
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
