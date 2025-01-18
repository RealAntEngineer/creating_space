package com.rae.creatingspace.server.blockentities;

import com.mojang.serialization.Codec;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.server.blocks.RocketControlsBlock;
import com.rae.creatingspace.server.contraption.RocketContraption;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RocketControlsBlockEntity extends SmartBlockEntity implements Nameable, IDisplayAssemblyExceptions/*implements MenuProvider*/ {
    private final Component defaultName;
    private Component customName;
    protected AssemblyException lastException;

    private boolean assembleNextTick = false;
    private ResourceLocation destination;

    public HashMap<ResourceLocation,BlockPos> initialPosMap = new HashMap<>();

    public static Codec<Map<ResourceLocation,BlockPos>> POS_MAP_CODEC = Codec.unboundedMap(ResourceLocation.CODEC,BlockPos.CODEC);
    public RocketControlsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type,pos, state);
        defaultName = getDefaultName();
    }
    public static Component getDefaultName() {

        return BlockInit.ROCKET_CONTROLS.get().getName();
    }
    @Override
    public Component getName() {
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
    /*@Override
    public void initialize() {
        super.initialize();
        if (!getBlockState().canSurvive(level, worldPosition))
            level.destroyBlock(worldPosition, true);

    }*/


    @Override
    public AssemblyException getLastAssemblyException() {
        return lastException;
    }

    public void queueAssembly(ResourceLocation destination) {
        this.assembleNextTick = true;
        this.destination = destination;
    }

    public void queueAssembly() {
        this.assembleNextTick = true;
        this.destination = null;
    }

    private void assemble() {

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

        if (destination == null){
            destination = level.dimension().location();
        }

        RocketContraptionEntity rocketContraptionEntity =
                RocketContraptionEntity.create(level, contraption, destination);
        BlockPos anchor = worldPosition;
        rocketContraptionEntity.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        rocketContraptionEntity.setInitialPosMap(initialPosMap);
        //we should make that a little better -> no initialisation of entry point before launch
        rocketContraptionEntity.rocketEntryCoordinate = initialPosMap.getOrDefault(destination.toString(), this.worldPosition);
        level.addFreshEntity(rocketContraptionEntity);

        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);

    }
    @Override
    public void tick() {
        super.tick();

        assert this.level != null;
        if (!this.initialPosMap.containsKey(this.level.dimension().location())) {
            this.initialPosMap.put(this.level.dimension().location(),this.getBlockPos());
        }


        if (assembleNextTick) {
            assemble();
            assembleNextTick = false;
        }

    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        AssemblyException.write(compound, lastException);
        compound.put("initialPosMap", putPosMap(this.initialPosMap));
        super.write(compound, clientPacket);
    }

    public static CompoundTag putPosMap(HashMap<ResourceLocation,BlockPos> initialPosMap) {
        return (CompoundTag) POS_MAP_CODEC.encodeStart(NbtOps.INSTANCE,initialPosMap).resultOrPartial(
                (e)-> {
                    CreatingSpace.LOGGER.warn("Rocket Control failed to parse pos map : ");
                    CreatingSpace.LOGGER.error(e);
                }).orElse(new CompoundTag());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        lastException = AssemblyException.read(compound);
        this.initialPosMap = getPosMap((CompoundTag) compound.get("initialPosMap"));
        super.read(compound, clientPacket);
    }
    public static  HashMap<ResourceLocation,BlockPos> getPosMap(CompoundTag compound) {
        return  new HashMap<>(POS_MAP_CODEC.parse(NbtOps.INSTANCE,compound).resultOrPartial(
                (e)-> {
                    CreatingSpace.LOGGER.warn("Rocket Control failed to parse pos map : ");
                    CreatingSpace.LOGGER.error(e);
                }).orElse(new HashMap<>()));
    }

    public boolean noLocalisation() {
        return initialPosMap.isEmpty();
    }

    public HashMap<ResourceLocation, BlockPos> getInitialPosMap() {
        return initialPosMap;
    }

    public void setInitialPosMap(HashMap<ResourceLocation, BlockPos> initialPosMap) {
        this.initialPosMap = new HashMap<>(initialPosMap);
        notifyUpdate();
    }
}
