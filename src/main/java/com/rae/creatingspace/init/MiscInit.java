package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.server.design.ExhaustPackType;
import com.rae.creatingspace.server.design.PowerPackType;
import com.rae.creatingspace.server.design.PropellantType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MiscInit {
    ;
    public static final DeferredRegister<PropellantType> DEFERRED_PROPELLANT_TYPE =
            DeferredRegister.create(Keys.PROPELLANT_TYPE, CreatingSpace.MODID);
    public static final Supplier<IForgeRegistry<PropellantType>> PROPELLANT_TYPE = DEFERRED_PROPELLANT_TYPE.makeRegistry(
            RegistryBuilder::new);
    public static final RegistryObject<PropellantType> METHALOX = DEFERRED_PROPELLANT_TYPE
            .register("methalox", () -> new PropellantType(
                    Map.of(
                            TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag, 3.7f,
                            TagsInit.CustomFluidTags.LIQUID_METHANE.tag, 1f), 459));
    public static final RegistryObject<PropellantType> LH2LOX = DEFERRED_PROPELLANT_TYPE
            .register("lh2lox", () -> new PropellantType(
                    Map.of(
                            TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag, 6f,
                            TagsInit.CustomFluidTags.LIQUID_HYDROGEN.tag, 1f), 532));
    public static final DeferredRegister<ExhaustPackType> DEFERRED_EXHAUST_PACK_TYPE =
            DeferredRegister.create(Keys.EXHAUST_PACK_TYPE, CreatingSpace.MODID);
    public static final Supplier<IForgeRegistry<ExhaustPackType>> EXHAUST_PACK_TYPE = DEFERRED_EXHAUST_PACK_TYPE.makeRegistry(
            RegistryBuilder::new);
    public static final RegistryObject<ExhaustPackType> BELL_NOZZLE = DEFERRED_EXHAUST_PACK_TYPE
            .register("bell_nozzle", () -> new ExhaustPackType(0.7f));
    public static final RegistryObject<ExhaustPackType> AEROSPIKE = DEFERRED_EXHAUST_PACK_TYPE
            .register("aerospike", () -> new ExhaustPackType(0.95f));
    public static final DeferredRegister<PowerPackType> DEFERRED_POWER_PACK_TYPE =
            DeferredRegister.create(Keys.POWER_PACK_TYPE, CreatingSpace.MODID);
    public static final Supplier<IForgeRegistry<PowerPackType>> POWER_PACK_TYPE = DEFERRED_POWER_PACK_TYPE.makeRegistry(
            RegistryBuilder::new);

    public static final RegistryObject<PowerPackType> OPEN_CYCLES = DEFERRED_POWER_PACK_TYPE
            .register("open_cycle", () -> new PowerPackType(0.7f, List.of(TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag), List.of(TagsInit.CustomFluidTags.LIQUID_METHANE.tag)));
    public static final RegistryObject<PowerPackType> OX_RICH_STAGED_CYCLES = DEFERRED_POWER_PACK_TYPE
            .register("ox_rich_staged_cycle", () -> new PowerPackType(0.9f, List.of(TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag), List.of(TagsInit.CustomFluidTags.LIQUID_METHANE.tag)));
    public static final RegistryObject<PowerPackType> FUEL_RICH_STAGED_CYCLES = DEFERRED_POWER_PACK_TYPE
            .register("fuel_rich_staged_cycle", () -> new PowerPackType(0.9f, List.of(TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag), List.of(TagsInit.CustomFluidTags.LIQUID_METHANE.tag)));
    public static final RegistryObject<PowerPackType> FULL_FLOW_STAGED_CYCLES = DEFERRED_POWER_PACK_TYPE
            .register("full_flow_staged_cycle", () -> new PowerPackType(0.98f, List.of(TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag), List.of(TagsInit.CustomFluidTags.LIQUID_METHANE.tag)));

    private static class Keys {
        public static final ResourceKey<Registry<PropellantType>> PROPELLANT_TYPE =
                ResourceKey.createRegistryKey(new ResourceLocation("creatingspace:propellant_type"));
        public static final ResourceKey<Registry<ExhaustPackType>> EXHAUST_PACK_TYPE =
                ResourceKey.createRegistryKey(new ResourceLocation("creatingspace:exhaust_pack_type"));
        public static final ResourceKey<Registry<PowerPackType>> POWER_PACK_TYPE =
                ResourceKey.createRegistryKey(new ResourceLocation("creatingspace:power_pack_type"));


    }

    public static void register(IEventBus modEventBus) {
        DEFERRED_PROPELLANT_TYPE.register(modEventBus);
        DEFERRED_EXHAUST_PACK_TYPE.register(modEventBus);
        DEFERRED_POWER_PACK_TYPE.register(modEventBus);
    }


}
