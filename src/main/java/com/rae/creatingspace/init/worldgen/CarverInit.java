package com.rae.creatingspace.init.worldgen;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.planets.worldgen.CraterCarver;
import com.rae.creatingspace.content.planets.worldgen.CraterCarverConfig;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CarverInit {
    private static final DeferredRegister<WorldCarver<?>> CARVERS =
            DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, CreatingSpace.MODID);
    //private static final DeferredRegister<ConfiguredWorldCarver<?>> CONFIGURED_CARVERS =
    //        DeferredRegister.create(Registries.CONFIGURED_CARVER, CreatingSpace.MODID);

    public static final RegistryObject<WorldCarver<CraterCarverConfig>> CRATERS_CARVER =
            CARVERS.register("crater",
            () -> new CraterCarver(CraterCarverConfig.CRATER_CODEC));

    public static void register(IEventBus bus) {
        CARVERS.register(bus);
    //    CONFIGURED_CARVERS.register(bus);
    }
}
