package com.rae.creatingspace.utilities.packet;

import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import static com.rae.creatingspace.server.entities.RocketContraptionEntity.RUNNING_ENTITY_DATA_ACCESSOR;

public class RocketContraptionLaunchPacket extends SimplePacketBase {

    public int entityID;
    public ResourceKey<Level> destination;

    public RocketContraptionLaunchPacket(int entityID, ResourceKey<Level> destination) {
        this.entityID = entityID;
        this.destination = destination;
    }

    public RocketContraptionLaunchPacket(FriendlyByteBuf buffer) {
        entityID = buffer.readInt();
        destination = buffer.readResourceKey(Registry.DIMENSION_REGISTRY);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityID);
        buffer.writeResourceKey(destination);

    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(
                () -> {
                    ServerPlayer sender = context.getSender();
                    Entity entity = sender.level.getEntity(entityID);
                    System.out.println(entity.level.isClientSide);
                    if (entity instanceof RocketContraptionEntity ce) {
                        ce.getEntityData().set(RUNNING_ENTITY_DATA_ACCESSOR, true);
                        ce.destination = destination;
                        ce.setShouldHandleCalculation(true);
                        //handelTrajectoryCalculation(ce);
                    }
                });
        return true;
    }
}
