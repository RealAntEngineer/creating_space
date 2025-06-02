package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;


public class RocketContraptionUpdatePacket  implements ClientboundPacketPayload {
    public static final StreamCodec<ByteBuf, RocketContraptionUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, packet -> packet.entityID,
            ByteBufCodecs.DOUBLE, packet -> packet.coord,
            ByteBufCodecs.DOUBLE, packet -> packet.speed,
            RocketContraptionUpdatePacket::new
    );
    public int entityID;
    public double coord;
    public double speed;

    public RocketContraptionUpdatePacket(int entityID, double coord, double speed) {
        this.entityID = entityID;
        this.coord = coord;
        this.speed = speed;
    }

    @Override
    public void handle(LocalPlayer player) {
        RocketContraptionEntity.handlePacket(this);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return null;
    }
}
