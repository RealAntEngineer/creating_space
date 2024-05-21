package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.rae.creatingspace.server.design.ExhaustPackType;
import com.rae.creatingspace.server.design.PowerPackType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class MiscInit {
    public static final DeferredRegister<ExhaustPackType> DEFERRED_EXHAUST_PACK_TYPE =
            DeferredRegister.create(Keys.EXHAUST_PACK_TYPE, CreatingSpace.MODID);
    public static final Supplier<IForgeRegistry<ExhaustPackType>> EXHAUST_PACK_TYPE = DEFERRED_EXHAUST_PACK_TYPE.makeRegistry(
            RegistryBuilder::new);
    public static final RegistryObject<ExhaustPackType> BELL_NOZZLE = DEFERRED_EXHAUST_PACK_TYPE
            .register("bell_nozzle", () -> new ExhaustPackType(0.7f, CreatingSpace.resource("bell_nozzle")));
    public static final RegistryObject<ExhaustPackType> AEROSPIKE = DEFERRED_EXHAUST_PACK_TYPE
            .register("aerospike", () -> new ExhaustPackType(0.95f, CreatingSpace.resource("aerospike")));
    public static final DeferredRegister<PowerPackType> DEFERRED_POWER_PACK_TYPE =
            DeferredRegister.create(Keys.POWER_PACK_TYPE, CreatingSpace.MODID);
    public static final Supplier<IForgeRegistry<PowerPackType>> POWER_PACK_TYPE = DEFERRED_POWER_PACK_TYPE.makeRegistry(
            RegistryBuilder::new);

    public static final RegistryObject<PowerPackType> OPEN_CYCLES = DEFERRED_POWER_PACK_TYPE
            .register("open_cycle", () -> new PowerPackType(0.7f, List.of(PropellantTypeInit.MH.getId())));
    public static final RegistryObject<PowerPackType> OX_RICH_STAGED_CYCLES = DEFERRED_POWER_PACK_TYPE
            .register("ox_rich_staged_cycle", () -> new PowerPackType(0.9f, List.of(PropellantTypeInit.MH.getId())));
    public static final RegistryObject<PowerPackType> FUEL_RICH_STAGED_CYCLES = DEFERRED_POWER_PACK_TYPE
            .register("fuel_rich_staged_cycle", () -> new PowerPackType(0.9f, List.of(PropellantTypeInit.MH.getId())));
    public static final RegistryObject<PowerPackType> FULL_FLOW_STAGED_CYCLES = DEFERRED_POWER_PACK_TYPE
            .register("full_flow_staged_cycle", () -> new PowerPackType(0.98f, List.of(PropellantTypeInit.MH.getId())));

    public static class Keys {
        public static final ResourceKey<Registry<ExhaustPackType>> EXHAUST_PACK_TYPE =
                ResourceKey.createRegistryKey(new ResourceLocation("creatingspace:exhaust_pack_type"));
        public static final ResourceKey<Registry<PowerPackType>> POWER_PACK_TYPE =
                ResourceKey.createRegistryKey(new ResourceLocation("creatingspace:power_pack_type"));


    }

    public static void register(IEventBus modEventBus) {
        DEFERRED_EXHAUST_PACK_TYPE.register(modEventBus);
        DEFERRED_POWER_PACK_TYPE.register(modEventBus);
    }

}
