package com.rae.creatingspace.content.fluids.storage;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlockEntityInit.CRYOGENIC_TANK.get(),
                (be,context) -> be.TANK
        );
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


    @Override
    public @NotNull Component getName() {
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
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        CompoundTag tankTag = new CompoundTag();
        tag.put("Fluids", TANK.writeToNBT(registries,tankTag));
        super.write(tag,registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag,registries, clientPacket);
        if (tag.contains("Fluids")) {
            TANK.readFromNBT(registries, (CompoundTag) tag.get("Fluids"));
        }
    }


    public void setTank(HolderLookup.Provider registries, CompoundTag tag) {
        TANK.readFromNBT(registries, tag);
    }

    public FluidTank getTank() {
        return TANK;
    }
    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    public void  tick() {
        super.tick();
        assert level != null;
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
        LangBuilder mb = new LangBuilder("creatingspace").translate("generic.unit.millibuckets");
        LangBuilder mbs = new LangBuilder("creatingspace").translate("generic.unit.fluidflow");
        new LangBuilder("creatingspace").translate("gui.goggles.fluid_container")
                .forGoggles(tooltip);

            FluidTank tank = TANK;
            String fluidName = TANK.getFluid().getFluid().getFluidType().getDescriptionId();

            FluidStack fluidStack = tank.getFluidInTank(0);

        new LangBuilder("creatingspace").add(Component.translatable(fluidName))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);

        new LangBuilder("creatingspace")
                    .add(CreateLang.number(fluidStack.getAmount())
                            .add(mb)
                            .style(ChatFormatting.GOLD))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(tank.getTankCapacity(0))
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
