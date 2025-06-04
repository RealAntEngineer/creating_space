package com.rae.creatingspace.content.life_support.sealer;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Vector;

public class RoomShapeSerializer implements EntityDataSerializer<RoomShape> {
    public RoomShapeSerializer() {
    }
    private final static StreamCodec<RegistryFriendlyByteBuf, AABB> AABB_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, aabb -> aabb.minX,
            ByteBufCodecs.DOUBLE, aabb -> aabb.minY,
            ByteBufCodecs.DOUBLE, aabb -> aabb.minZ,
            ByteBufCodecs.DOUBLE, aabb -> aabb.maxX,
            ByteBufCodecs.DOUBLE, aabb -> aabb.maxY,
            ByteBufCodecs.DOUBLE, aabb -> aabb.maxZ,
            AABB::new
    );
    private final static StreamCodec<RegistryFriendlyByteBuf, RoomShape> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(Vector::new,AABB_STREAM_CODEC),roomShape -> roomShape.listOfBox,
            ByteBufCodecs.INT, roomShape -> roomShape.volume,
            ByteBufCodecs.BOOL, roomShape -> roomShape.closed,
            RoomShape::new
    );

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, RoomShape> codec() {
        return STREAM_CODEC;
    }

    @Override
    public @NotNull RoomShape copy(@NotNull RoomShape shape) {
        return shape;
    }
}
