package com.rae.creatingspace.legacy.utilities.packet;

import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class RocketAssemblePacket extends BlockEntityConfigurationPacket<RocketControlsBlockEntity> {
    private Boolean assembleNextTick;

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

    public static RocketAssemblePacket tryAssemble(BlockPos pos) {
        RocketAssemblePacket packet = new RocketAssemblePacket(pos, true);
        packet.assembleNextTick = true;
        return packet;
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {

        buffer.writeBoolean(assembleNextTick);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        assembleNextTick = buffer.readBoolean();

    }

    @Override
    protected void applySettings(ServerPlayer player, RocketControlsBlockEntity controlsBlockEntity) {
        controlsBlockEntity.queueAssembly();
    }

    @Override
    protected void applySettings(RocketControlsBlockEntity controlsBlockEntity) {

    }
}