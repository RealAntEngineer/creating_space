package com.rae.creatingspace.legacy.utilities.packet;

import com.rae.creatingspace.legacy.server.blockentities.atmosphere.SealerBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SealerTrySealing extends BlockEntityConfigurationPacket<SealerBlockEntity> {
    public SealerTrySealing(BlockPos pos) {
        super(pos);
    }

    public SealerTrySealing(FriendlyByteBuf buffer) {
        super(buffer);
    }


    public static SealerTrySealing trySealing(BlockPos pos) {
        SealerTrySealing packet = new SealerTrySealing(pos);
        return packet;
    }


    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
    }

    @Override
    protected void applySettings(ServerPlayer player, SealerBlockEntity sealerBlockEntity) {
        sealerBlockEntity.resetRemainingTries();
        sealerBlockEntity.setTrying(true);
    }
    @Override
    protected void applySettings(SealerBlockEntity sealerBlockEntity) {

    }
}
