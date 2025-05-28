package com.rae.creatingspace.init;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.content.life_support.sealer.RoomShapeSerializer;
import com.simibubi.create.Create;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EntityDataSerializersInit {
    private static final DeferredRegister<EntityDataSerializer<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Create.ID);
    public static final RoomShapeSerializer SHAPE_SERIALIZER = new RoomShapeSerializer();
    public static EntityDataSerializer<RocketContraptionEntity.RocketStatus> STATUS_SERIALIZER =
            EntityDataSerializer.forValueType(RocketContraptionEntity.RocketStatus.STREAM_CODEC);

    public static final DeferredHolder<EntityDataSerializer<?>, RoomShapeSerializer> SHAPE_DATA_ENTRY = REGISTER.register("shape_data", () -> SHAPE_SERIALIZER);
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<RocketContraptionEntity.RocketStatus>> STATUS_ENTRY = REGISTER.register("rocket_status", () -> STATUS_SERIALIZER);

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
