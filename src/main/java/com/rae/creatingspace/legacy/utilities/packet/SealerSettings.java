package com.rae.creatingspace.legacy.utilities.packet;

import com.rae.creatingspace.legacy.server.blockentities.atmosphere.SealerBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SealerSettings extends BlockEntityConfigurationPacket<SealerBlockEntity> {
    private int range;
    private boolean isAutomaticRetry;

    public SealerSettings(BlockPos pos, int range, boolean isAutomaticRetry) {
        super(pos);
        this.range = range;
        this.isAutomaticRetry = isAutomaticRetry;
    }


    public SealerSettings(BlockPos pos) {
        super(pos);
    }

    public SealerSettings(FriendlyByteBuf buffer) {
        super(buffer);
    }


    public static SealerSettings sendSettings(BlockPos pos, int range, boolean isAutomaticRetry) {
        SealerSettings packet = new SealerSettings(pos);
        packet.range = range;
        packet.isAutomaticRetry = isAutomaticRetry;
        return packet;
    }


    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeBoolean(isAutomaticRetry);
        buffer.writeInt(range);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        isAutomaticRetry = buffer.readBoolean();
        range = buffer.readInt();
    }

    @Override
    protected void applySettings(ServerPlayer player, SealerBlockEntity sealerBlockEntity) {

        sealerBlockEntity.setSettings(range,isAutomaticRetry);
    }
    @Override
    protected void applySettings(SealerBlockEntity sealerBlockEntity) {

    }
}
