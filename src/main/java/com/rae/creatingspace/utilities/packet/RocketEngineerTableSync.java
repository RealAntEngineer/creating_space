package com.rae.creatingspace.utilities.packet;

import com.rae.creatingspace.server.blockentities.RocketEngineerTableBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class RocketEngineerTableSync extends BlockEntityConfigurationPacket<RocketEngineerTableBlockEntity> {
    private CompoundTag syncData;

    public RocketEngineerTableSync(BlockPos pos, CompoundTag syncData) {
        super(pos);
        this.syncData = syncData;
    }


    public RocketEngineerTableSync(BlockPos pos) {
        super(pos);
    }

    public RocketEngineerTableSync(FriendlyByteBuf buffer) {
        super(buffer);
    }


    public static RocketEngineerTableSync sendSettings(BlockPos pos, CompoundTag syncData) {
        RocketEngineerTableSync packet = new RocketEngineerTableSync(pos);
        packet.syncData = syncData;
        return packet;
    }


    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeNbt((CompoundTag) syncData);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        syncData = buffer.readNbt();
    }

    @Override
    protected void applySettings(ServerPlayer player, RocketEngineerTableBlockEntity be) {
        System.out.println("sync on server : " + syncData);
        be.readScreenData(syncData);
    }

    @Override
    protected void applySettings(RocketEngineerTableBlockEntity be) {
    }
}