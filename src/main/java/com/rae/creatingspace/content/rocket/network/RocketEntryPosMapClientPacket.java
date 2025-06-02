package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlockEntity;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.init.PacketInit;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class RocketEntryPosMapClientPacket implements ServerboundPacketPayload {
    public static final StreamCodec<? super RegistryFriendlyByteBuf, RocketEntryPosMapClientPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, packet -> packet.id,
                    ByteBufCodecs.fromCodec(RocketControlsBlockEntity.POS_MAP_CODEC),packet -> packet.initialPosMap,
                    RocketEntryPosMapClientPacket::new
            );
    private Map<ResourceLocation, BlockPos> initialPosMap;
    private int id;

    public RocketEntryPosMapClientPacket(int id, Map<ResourceLocation,BlockPos> initialPosMap) {
        this.id = id;
        this.initialPosMap = initialPosMap;
    }


    @Override
    public void handle(ServerPlayer player) {
        Entity entity = player.level().getEntity(id);
        if (entity instanceof RocketContraptionEntity ce) {
            ce.setInitialPosMap(new HashMap<>(initialPosMap));
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.SYNC_POSMAP_CLIENT;
    }
}
