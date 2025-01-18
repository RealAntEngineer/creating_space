package com.rae.creatingspace.utilities.packet;

import com.rae.creatingspace.server.blockentities.RocketControlsBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

public class RocketControlsSettingsPacket extends BlockEntityConfigurationPacket<RocketControlsBlockEntity> {
    private HashMap<ResourceLocation, BlockPos> initialPosMap;

    public RocketControlsSettingsPacket(BlockPos pos, HashMap<ResourceLocation,BlockPos> initialPosMap) {
        super(pos);
        this.initialPosMap = initialPosMap;
    }


    public RocketControlsSettingsPacket(BlockPos pos) {
        super(pos);
    }

    public RocketControlsSettingsPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }


    public static RocketControlsSettingsPacket sendSettings(BlockPos pos, HashMap<ResourceLocation,BlockPos> initialPosMap) {
        RocketControlsSettingsPacket packet = new RocketControlsSettingsPacket(pos);
        packet.initialPosMap = initialPosMap;
        return packet;
    }


    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeNbt(RocketControlsBlockEntity.putPosMap(initialPosMap));
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
