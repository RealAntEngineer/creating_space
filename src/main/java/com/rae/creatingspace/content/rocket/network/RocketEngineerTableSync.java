package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.content.rocket.engine.table.RocketEngineerTableBlockEntity;
import com.rae.creatingspace.init.PacketInit;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class RocketEngineerTableSync extends BlockEntityConfigurationPacket<RocketEngineerTableBlockEntity> {
    private CompoundTag syncData;
    public static final StreamCodec<RegistryFriendlyByteBuf, RocketEngineerTableSync> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.COMPOUND_TAG, packet -> packet.syncData,
            RocketEngineerTableSync::new
    );
    public RocketEngineerTableSync(BlockPos pos, CompoundTag syncData) {
        super(pos);
        this.syncData = syncData;
    }

    public static RocketEngineerTableSync sendSettings(BlockPos pos, CompoundTag syncData) {
        return new RocketEngineerTableSync(pos,syncData);
    }

    @Override
    protected void applySettings(ServerPlayer player, RocketEngineerTableBlockEntity be) {
        be.readScreenData(syncData);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.SYNC_ROCKET_ENGINEER_BE;
    }
}