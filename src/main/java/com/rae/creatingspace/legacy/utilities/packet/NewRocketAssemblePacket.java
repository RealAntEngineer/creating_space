package com.rae.creatingspace.legacy.utilities.packet;

import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlockEntity;
import com.rae.creatingspace.init.PacketInit;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class NewRocketAssemblePacket extends BlockEntityConfigurationPacket<RocketControlsBlockEntity> {
    private Boolean assembleNextTick;
    private ResourceLocation destination;
    public static final StreamCodec<RegistryFriendlyByteBuf, NewRocketAssemblePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.BOOL, packet -> packet.assembleNextTick,
            NewRocketAssemblePacket::new
    );

    public NewRocketAssemblePacket(BlockPos pos, Boolean assembleNextTick) {
        super(pos);
        this.assembleNextTick = assembleNextTick;
    }

    public static NewRocketAssemblePacket tryAssemble(BlockPos pos) {
        NewRocketAssemblePacket packet = new NewRocketAssemblePacket(pos, true);
        packet.assembleNextTick = true;
        return packet;
    }

    @Override
    protected void applySettings(ServerPlayer player, RocketControlsBlockEntity controlsBlockEntity) {
        Level level = controlsBlockEntity.getLevel();
        BlockPos blockPos = controlsBlockEntity.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);

        controlsBlockEntity.queueAssembly(null);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.ASSEMBLE_ROCKET_2;
    }
}
