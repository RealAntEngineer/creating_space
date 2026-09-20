package com.rae.creatingspace.content.rocket.rocket_control;

import com.mojang.serialization.Codec;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.content.rocket.contraption.RocketContraption;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RocketControlsBlockEntity extends SmartBlockEntity implements Nameable, IDisplayAssemblyExceptions/*implements MenuProvider*/ {
    private final Component defaultName;
    private Component customName;
    protected AssemblyException lastException;

    private boolean assembleNextTick = false;

    public static Codec<Map<ResourceLocation,BlockPos>> POS_MAP_CODEC = Codec.unboundedMap(ResourceLocation.CODEC,BlockPos.CODEC);
    public RocketControlsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type,pos, state);
        defaultName = getDefaultName();
    }
    public static Component getDefaultName() {

        return BlockInit.ROCKET_CONTROLS.get().getName();
    }
    @Override
    public @NotNull Component getName() {
        return this.customName != null ? this.customName
                : defaultName;
    }

    public void setCustomName(Component customName) {
        this.customName = customName;
        notifyUpdate();
    }

    public Component getCustomName() {
        return customName;
    }


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS);

    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return lastException;
    }

    public void queueAssembly() {
        this.assembleNextTick = true;
    }

    private void assemble() {

        assert level != null;
        if (!(level.getBlockState(worldPosition)
                .getBlock() instanceof RocketControlsBlock)) {
            return;
        }

        RocketContraption contraption = new RocketContraption();


        try {
            lastException = null;
            if (!contraption.assemble(level, worldPosition))
                return;

            sendData();
        } catch (AssemblyException e) {
            lastException = e;
            sendData();
            return;
        }

        if (contraption.containsBlockBreakers())
            award(AllAdvancements.CONTRAPTION_ACTORS);

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);

        RocketContraptionEntity rocketContraptionEntity =
                RocketContraptionEntity.create(level, contraption);
        BlockPos anchor = worldPosition;
        rocketContraptionEntity.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        //rocketContraptionEntity.setInitialPosMap(initialPosMap);
        //we should make that a little better -> no initialisation of entry point before launch
        //rocketContraptionEntity.rocketEntryCoordinate = initialPosMap.getOrDefault(destination, this.worldPosition);
        level.addFreshEntity(rocketContraptionEntity);

        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);

    }

    @Override
    public void tick() {
        super.tick();
        assert this.level != null;

        if (assembleNextTick) {
            assemble();
            assembleNextTick = false;
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        AssemblyException.write(compound, registries,lastException);
        super.write(compound,registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        lastException = AssemblyException.read(compound,registries);
        super.read(compound,registries, clientPacket);
    }
}
