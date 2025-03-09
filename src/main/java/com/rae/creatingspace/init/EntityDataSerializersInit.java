package com.rae.creatingspace.init;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.content.life_support.sealer.RoomShapeSerializer;
import com.simibubi.create.Create;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityDataSerializersInit {
    private static final DeferredRegister<EntityDataSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Create.ID);
    public static final RoomShapeSerializer SHAPE_SERIALIZER = new RoomShapeSerializer();
    public static EntityDataSerializer<RocketContraptionEntity.RocketStatus> STATUS_SERIALIZER =
            EntityDataSerializer.simpleEnum(RocketContraptionEntity.RocketStatus.class);

    public static final RegistryObject<RoomShapeSerializer> SHAPE_DATA_ENTRY = REGISTER.register("shape_data", () -> SHAPE_SERIALIZER);
    public static final RegistryObject<EntityDataSerializer<RocketContraptionEntity.RocketStatus>> STATUS_ENTRY = REGISTER.register("rocket_status", () -> STATUS_SERIALIZER);

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
