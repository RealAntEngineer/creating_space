package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.init.PacketInit;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;


public class RocketContraptionLaunchPacket implements ServerboundPacketPayload {

    public int entityID;
    public ResourceLocation destination;
    public static final StreamCodec<RegistryFriendlyByteBuf, RocketContraptionLaunchPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, packet -> packet.entityID,
                    ResourceLocation.STREAM_CODEC, packet -> packet.destination,
                    RocketContraptionLaunchPacket::new
            );

    public RocketContraptionLaunchPacket(int entityID, ResourceLocation destination) {
        this.entityID = entityID;
        this.destination = destination;
    }

    @Override
    public void handle(ServerPlayer player) {
        Entity entity = player.level().getEntity(entityID);
        if (entity instanceof RocketContraptionEntity ce) {
            ce.destination = destination;
            RocketContraptionEntity.handelTrajectoryCalculation(ce);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.LAUNCH_ROCKET;
    }
}
