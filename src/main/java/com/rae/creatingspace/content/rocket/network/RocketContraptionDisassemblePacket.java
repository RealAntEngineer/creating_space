package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.init.PacketInit;
import com.simibubi.create.content.contraptions.sync.ClientMotionPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class RocketContraptionDisassemblePacket implements ServerboundPacketPayload {
    public static final StreamCodec<ByteBuf, RocketContraptionDisassemblePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, packet -> packet.entityID,
            RocketContraptionDisassemblePacket::new
    );
    public int entityID;

    public RocketContraptionDisassemblePacket(int entityID) {
        this.entityID = entityID;
    }


    @Override
    public void handle(ServerPlayer player) {
        Entity entity = player.level().getEntity(entityID);
        if (entity instanceof RocketContraptionEntity ce) {

            ce.disassemble();
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.DISASSEMBLE_ROCKET;
    }
}
