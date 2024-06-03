package com.rae.creatingspace.server.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class RoomShapeSerializer implements EntityDataSerializer<RoomShape> {
    public RoomShapeSerializer() {
    }

    @Override
    public void write(FriendlyByteBuf byteBuf, RoomShape roomShape) {
        byteBuf.writeBoolean(roomShape.closed);
        byteBuf.writeInt(roomShape.volume);
        byteBuf.writeCollection(roomShape.listOfBox, ((byteBuf1, aabb) -> {
            byteBuf1.writeBlockPos(new BlockPos(aabb.minX, aabb.minY, aabb.minZ));
            byteBuf1.writeBlockPos(new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ));
        }));
    }

    @Override
    public RoomShape read(FriendlyByteBuf byteBuf) {
        boolean closed = byteBuf.readBoolean();
        int volume = byteBuf.readInt();
        List<AABB> aabbList = byteBuf.readCollection(NonNullList::createWithCapacity, byteBuf1 -> new AABB(byteBuf1.readBlockPos(), byteBuf1.readBlockPos()));
        return new RoomShape(aabbList, volume, closed);
    }

    @Override
    public RoomShape copy(RoomShape shape) {
        return shape;
    }
}
