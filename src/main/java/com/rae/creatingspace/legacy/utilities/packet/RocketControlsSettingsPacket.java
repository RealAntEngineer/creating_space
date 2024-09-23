package com.rae.creatingspace.legacy.utilities.packet;

import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

public class RocketControlsSettingsPacket extends BlockEntityConfigurationPacket<RocketControlsBlockEntity> {
    private HashMap<String, BlockPos> initialPosMap;

    public RocketControlsSettingsPacket(BlockPos pos, HashMap<String,BlockPos> initialPosMap) {
        super(pos);
        this.initialPosMap = initialPosMap;
    }


    public RocketControlsSettingsPacket(BlockPos pos) {
        super(pos);
    }

    public RocketControlsSettingsPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }


    public static RocketControlsSettingsPacket sendSettings(BlockPos pos, HashMap<String,BlockPos> initialPosMap) {
        RocketControlsSettingsPacket packet = new RocketControlsSettingsPacket(pos);
        packet.initialPosMap = initialPosMap;
        return packet;
    }


    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeNbt(RocketControlsBlockEntity.putPosMap(initialPosMap, new CompoundTag()));
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        initialPosMap = RocketControlsBlockEntity.getPosMap(buffer.readNbt());
    }

    @Override
    protected void applySettings(ServerPlayer player, RocketControlsBlockEntity sealerBlockEntity) {

        sealerBlockEntity.setInitialPosMap(initialPosMap);
    }
    @Override
    protected void applySettings(RocketControlsBlockEntity sealerBlockEntity) {

    }
}
