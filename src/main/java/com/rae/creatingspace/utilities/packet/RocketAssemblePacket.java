package com.rae.creatingspace.utilities.packet;

import com.rae.creatingspace.server.blockentities.RocketControlsBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RocketAssemblePacket extends BlockEntityConfigurationPacket<RocketControlsBlockEntity> {
    private Boolean assembleNextTick;
    private ResourceKey<Level> destination;

    public RocketAssemblePacket(BlockPos pos, Boolean assembleNextTick) {
        super(pos);
        this.assembleNextTick = assembleNextTick;
    }

    public RocketAssemblePacket(FriendlyByteBuf buffer) {
        super(buffer);
    }
    public RocketAssemblePacket(BlockPos pos) {
        super(pos);
    }


    public static RocketAssemblePacket tryAssemble(BlockPos pos, ResourceKey<Level> destination) {
        RocketAssemblePacket packet = new RocketAssemblePacket(pos,true);
        packet.assembleNextTick = true;
        packet.destination = destination;
        return packet;
    }


    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {

        buffer.writeBoolean(assembleNextTick);
        buffer.writeResourceKey(destination);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
            assembleNextTick = buffer.readBoolean();
            destination = buffer.readResourceKey(Registry.DIMENSION_REGISTRY);

    }

    @Override
    protected void applySettings(ServerPlayer player, RocketControlsBlockEntity controlsBlockEntity) {
        Level level = controlsBlockEntity.getLevel();
        BlockPos blockPos = controlsBlockEntity.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);

        controlsBlockEntity.queueAssembly(this.destination);
    }
    @Override
    protected void applySettings(RocketControlsBlockEntity controlsBlockEntity) {

    }
}
