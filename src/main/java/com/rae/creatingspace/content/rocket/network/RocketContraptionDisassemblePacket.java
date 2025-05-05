package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class RocketContraptionDisassemblePacket extends SimplePacketBase {

    public int entityID;

    public RocketContraptionDisassemblePacket(int entityID) {
        this.entityID = entityID;
    }

    public RocketContraptionDisassemblePacket(FriendlyByteBuf buffer) {
        entityID = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityID);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(
                () -> {
                    ServerPlayer sender = context.getSender();
                    Entity entity = sender.level().getEntity(entityID);
                    if (entity instanceof RocketContraptionEntity ce) {

                        ce.disassemble();
                    }
                });
        return true;
    }
}
