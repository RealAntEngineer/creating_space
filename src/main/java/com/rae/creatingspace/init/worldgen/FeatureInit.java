package com.rae.creatingspace.init.worldgen;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.worldgen.FloatingIsland;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class FeatureInit {
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, CreatingSpace.MODID);

    public static final RegistryObject<FloatingIsland> FLOATING_ISLAND= FEATURES.register("floating_island", () -> new FloatingIsland(BlockPileConfiguration.CODEC));
    public static void register(IEventBus bus){
        FEATURES.register(bus);
    }
}
