package com.rae.creatingspace.utilities.packet;

import com.rae.creatingspace.server.blockentities.RocketControlsBlockEntity;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;

public class RocketEntryPosMapClientPacket extends SimplePacketBase {
    private HashMap<String, BlockPos> initialPosMap;
    private int id;

    public RocketEntryPosMapClientPacket(int id, HashMap<String,BlockPos> initialPosMap) {
        this.id = id;
        this.initialPosMap = initialPosMap;
    }

    public RocketEntryPosMapClientPacket(FriendlyByteBuf buffer) {
        id = buffer.readInt();
        initialPosMap = RocketControlsBlockEntity.getPosMap(buffer.readNbt());
    }
    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeNbt(RocketControlsBlockEntity.putPosMap(initialPosMap, new CompoundTag()));

    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(
                () -> {
                    ServerPlayer sender = context.getSender();
                    assert sender != null;
                    Entity entity = sender.level().getEntity(id);
                    if (entity instanceof RocketContraptionEntity ce) {
                        ce.setInitialPosMap(initialPosMap);
                    }
                }
        );
        return true;
    }
}
