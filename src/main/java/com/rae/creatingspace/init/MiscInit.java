package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.engine.design.ExhaustPackType;
import com.rae.creatingspace.content.rocket.engine.design.PowerPackType;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static com.rae.creatingspace.content.event.DataEventHandler.getSideAwareRegistry;

public class MiscInit {
    public static final DeferredRegister<ExhaustPackType> DEFERRED_EXHAUST_PACK_TYPE =
            DeferredRegister.create(Keys.EXHAUST_PACK_TYPE, CreatingSpace.MODID);

    public static final DeferredHolder<ExhaustPackType,ExhaustPackType> BELL_NOZZLE = DEFERRED_EXHAUST_PACK_TYPE.register(
            "bell_nozzle", () -> new ExhaustPackType(2, 100,
                    List.of(CreatingSpace.resource("methalox"))

            ));
    public static final DeferredRegister<PowerPackType> DEFERRED_POWER_PACK_TYPE =
            DeferredRegister.create(Keys.POWER_PACK_TYPE, CreatingSpace.MODID);

    public static final DeferredHolder<PowerPackType,PowerPackType> OPEN_CYCLE = DEFERRED_POWER_PACK_TYPE.register(
            "open_cycle", () -> new PowerPackType(2, 1,
                    List.of(CreatingSpace.resource("methalox"))

            ));

    public static final DeferredRegister<RocketAccessibleDimension> DEFERRED_ROCKET_ACCESSIBLE_DIMENSION =
            DeferredRegister.create(RocketAccessibleDimension.REGISTRY_KEY, CreatingSpace.MODID);

    public static Registry<ExhaustPackType> getSyncedExhaustPackRegistry() {
        return getSideAwareRegistry(Keys.EXHAUST_PACK_TYPE);
    }

    public static Registry<PowerPackType> getSyncedPowerPackRegistry() {
        return getSideAwareRegistry(Keys.POWER_PACK_TYPE);
    }

    public static class Keys {
        public static final ResourceKey<Registry<ExhaustPackType>> EXHAUST_PACK_TYPE =
                ResourceKey.createRegistryKey(CreatingSpace.resource("exhaust_pack_type"));
        public static final ResourceKey<Registry<PowerPackType>> POWER_PACK_TYPE =
                ResourceKey.createRegistryKey(CreatingSpace.resource("power_pack_type"));


    }
    public static void register(IEventBus modEventBus) {
        DEFERRED_EXHAUST_PACK_TYPE.register(modEventBus);
        DEFERRED_POWER_PACK_TYPE.register(modEventBus);
        DEFERRED_ROCKET_ACCESSIBLE_DIMENSION.register(modEventBus);
    }

}