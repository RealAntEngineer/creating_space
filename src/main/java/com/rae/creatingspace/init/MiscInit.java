package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.engine.design.ExhaustPackType;
import com.rae.creatingspace.content.rocket.engine.design.PowerPackType;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class MiscInit {
    //TODO remove slots from the exhaust and power pack, add allowedPropellants for the exhaust pack
    public static final DeferredRegister<ExhaustPackType> DEFERRED_EXHAUST_PACK_TYPE =
            DeferredRegister.create(Keys.EXHAUST_PACK_TYPE, CreatingSpace.MODID);
    public static final Supplier<IForgeRegistry<ExhaustPackType>> EXHAUST_PACK_TYPE = DEFERRED_EXHAUST_PACK_TYPE.makeRegistry(
            () -> new RegistryBuilder<ExhaustPackType>()
                    .allowModification().disableSaving());
    public static final RegistryObject<ExhaustPackType> BELL_NOZZLE = DEFERRED_EXHAUST_PACK_TYPE.register(
            "bell_nozzle", () -> new ExhaustPackType(2, 100,
                    List.of(CreatingSpace.resource("methalox"))
                    /*,List.of(
                    Couple.create(30, 30),
                    Couple.create(90, 30)*/

            ));
    public static final DeferredRegister<PowerPackType> DEFERRED_POWER_PACK_TYPE =
            DeferredRegister.create(Keys.POWER_PACK_TYPE, CreatingSpace.MODID);
    public static final Supplier<IForgeRegistry<PowerPackType>> POWER_PACK_TYPE = DEFERRED_POWER_PACK_TYPE.makeRegistry(
            () -> new RegistryBuilder<PowerPackType>().allowModification().disableSaving());
    public static final RegistryObject<PowerPackType> OPEN_CYCLE = DEFERRED_POWER_PACK_TYPE.register(
            "open_cycle", () -> new PowerPackType(2, 1,
                    List.of(CreatingSpace.resource("methalox"))
                    /*,
                    List.of(
                            Couple.create(34, 31),
                            Couple.create(75, 30)
                    )*/
            ));

    public static final DeferredRegister<RocketAccessibleDimension> DEFERRED_ROCKET_ACCESSIBLE_DIMENSION =
            DeferredRegister.create(RocketAccessibleDimension.REGISTRY_KEY, CreatingSpace.MODID);
    public static final Supplier<IForgeRegistry<RocketAccessibleDimension>> ROCKET_ACCESSIBLE_DIMENSIONS = DEFERRED_ROCKET_ACCESSIBLE_DIMENSION.makeRegistry(
            () -> new RegistryBuilder<RocketAccessibleDimension>().allowModification().disableSaving());
    /**
     * use the side aware sync registry access instead
     * @return a client side sync version of the DEFERRED_EXHAUST_PACK_TYPE
     */
    @OnlyIn(Dist.CLIENT)
    public static Registry<ExhaustPackType> getSyncedExhaustPackRegistry() {
        return Minecraft.getInstance().getConnection().registryAccess().registry(Keys.EXHAUST_PACK_TYPE)
                .orElseThrow();
    }

    /**
     *
     * @return a client side sync version of the DEFERRED_POWER_PACK_TYPE
     */
    @OnlyIn(Dist.CLIENT)
    public static Registry<PowerPackType> getSyncedPowerPackRegistry() {
        return Minecraft.getInstance().getConnection().registryAccess().registry(Keys.POWER_PACK_TYPE)
                .orElseThrow();
    }

    public static class Keys {
        public static final ResourceKey<Registry<ExhaustPackType>> EXHAUST_PACK_TYPE =
                ResourceKey.createRegistryKey(new ResourceLocation("creatingspace:exhaust_pack_type"));
        public static final ResourceKey<Registry<PowerPackType>> POWER_PACK_TYPE =
                ResourceKey.createRegistryKey(new ResourceLocation("creatingspace:power_pack_type"));


    }
    public static void register(IEventBus modEventBus) {
        DEFERRED_EXHAUST_PACK_TYPE.register(modEventBus);
        DEFERRED_POWER_PACK_TYPE.register(modEventBus);
        DEFERRED_ROCKET_ACCESSIBLE_DIMENSION.register(modEventBus);
    }

}