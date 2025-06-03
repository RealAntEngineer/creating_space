package com.rae.creatingspace.legacy.utilities.packet;

import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlockEntity;
import com.rae.creatingspace.init.PacketInit;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class RocketControlsSettingsPacket extends BlockEntityConfigurationPacket<RocketControlsBlockEntity> {
    public static final StreamCodec<? super RegistryFriendlyByteBuf, RocketControlsSettingsPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, packet -> packet.pos,
                    ByteBufCodecs.fromCodec(RocketControlsBlockEntity.POS_MAP_CODEC),packet -> packet.initialPosMap,
                    RocketControlsSettingsPacket::new
            );
    private Map<ResourceLocation, BlockPos> initialPosMap;

    public RocketControlsSettingsPacket(BlockPos pos, Map<ResourceLocation,BlockPos> initialPosMap) {
        super(pos);
        this.initialPosMap = initialPosMap;
    }


    public RocketControlsSettingsPacket(BlockPos pos) {
        super(pos);
    }


    public static RocketControlsSettingsPacket sendSettings(BlockPos pos, HashMap<ResourceLocation,BlockPos> initialPosMap) {
        RocketControlsSettingsPacket packet = new RocketControlsSettingsPacket(pos);
        packet.initialPosMap = initialPosMap;
        return packet;
    }



    @Override
    protected void applySettings(ServerPlayer player, RocketControlsBlockEntity sealerBlockEntity) {

        sealerBlockEntity.setInitialPosMap(new HashMap<>(initialPosMap));
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.ROCKET_CONTROLS_SETTING;
    }
}
