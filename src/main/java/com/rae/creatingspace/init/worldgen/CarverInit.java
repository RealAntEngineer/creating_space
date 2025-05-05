package com.rae.creatingspace.init.worldgen;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.planets.worldgen.CraterCarver;
import com.rae.creatingspace.content.planets.worldgen.CraterCarverConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CarverInit {
    private static final DeferredRegister<WorldCarver<?>> CARVERS =
            DeferredRegister.create(Registries.CARVER, CreatingSpace.MODID);
    //private static final DeferredRegister<ConfiguredWorldCarver<?>> CONFIGURED_CARVERS =
    //        DeferredRegister.create(Registries.CONFIGURED_CARVER, CreatingSpace.MODID);

    public static final DeferredHolder<WorldCarver<?>, CraterCarver> CRATERS_CARVER =
            CARVERS.register("crater",
            () -> new CraterCarver(CraterCarverConfig.CRATER_CODEC));

    public static void register(IEventBus bus) {
        CARVERS.register(bus);
    //    CONFIGURED_CARVERS.register(bus);
    }
}
