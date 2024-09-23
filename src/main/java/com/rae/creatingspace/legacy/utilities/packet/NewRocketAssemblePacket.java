package com.rae.creatingspace.legacy.utilities.packet;

import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class NewRocketAssemblePacket extends BlockEntityConfigurationPacket<RocketControlsBlockEntity> {
    private Boolean assembleNextTick;
    private ResourceLocation destination;

    public NewRocketAssemblePacket(BlockPos pos, Boolean assembleNextTick) {
        super(pos);
        this.assembleNextTick = assembleNextTick;
    }

    public NewRocketAssemblePacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public NewRocketAssemblePacket(BlockPos pos) {
        super(pos);
    }

    public static NewRocketAssemblePacket tryAssemble(BlockPos pos) {
        NewRocketAssemblePacket packet = new NewRocketAssemblePacket(pos, true);
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
        Level level = controlsBlockEntity.getLevel();
        BlockPos blockPos = controlsBlockEntity.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);

        controlsBlockEntity.queueAssembly(this.destination);
    }

    @Override
    protected void applySettings(RocketControlsBlockEntity controlsBlockEntity) {

    }
}
