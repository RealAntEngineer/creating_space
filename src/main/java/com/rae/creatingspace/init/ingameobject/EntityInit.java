package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.RocketContraptionEntityRenderer;
import com.rae.creatingspace.content.life_support.sealer.RoomAtmosphereRenderer;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.content.life_support.sealer.RoomAtmosphere;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityInit {
    public static final EntityEntry<RocketContraptionEntity> ROCKET_CONTRAPTION =
            contraption("rocket_contraption", RocketContraptionEntity::new,
                    () -> RocketContraptionEntityRenderer::new, 15, 1, true)
                    .register();

    public static final EntityEntry<RoomAtmosphere> ATMOSPHERE_ENTITY =
            register("room_atmosphere", RoomAtmosphere::new,
                    () -> RoomAtmosphereRenderer::new,
                    MobCategory.MISC, 3, 10,
                    false, true, RoomAtmosphere::build)
                    .register();



    private static <T extends Entity> CreateEntityBuilder<T, ?> contraption(String name, EntityType.EntityFactory<T> factory,
                                                                            NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer, int range,
                                                                            int updateFrequency, boolean sendVelocity) {
        return register(name, factory, renderer, MobCategory.MISC, range, updateFrequency, sendVelocity, true,
                AbstractContraptionEntity::build);
    }

    private static <T extends Entity> CreateEntityBuilder<T, ?> register(String name, EntityType.EntityFactory<T> factory,
                                                                         NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer,
                                                                         MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire,
                                                                         NonNullConsumer<EntityType.Builder<T>> propertyBuilder) {
        String id = Lang.asId(name);
        return (CreateEntityBuilder<T, ?>) CreatingSpace.REGISTRATE
                .entity(id, factory, group)
                .properties(b -> b.setTrackingRange(range)
                        .setUpdateInterval(updateFrequency)
                        .setShouldReceiveVelocityUpdates(sendVelocity))
                .properties(propertyBuilder)
                .properties(b -> {
                    if (immuneToFire)
                        b.fireImmune();
                })
                .renderer(renderer);
    }

    public static void register() {}

}
