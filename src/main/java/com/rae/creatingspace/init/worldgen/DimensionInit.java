package com.rae.creatingspace.init.worldgen;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;

public class DimensionInit {

    public static final ResourceKey<Level> EARTH_ORBIT_KEY =
            ResourceKey.create(Registries.DIMENSION,
                    new ResourceLocation(CreatingSpace.MODID,"earth_orbit"));

    public static final ResourceKey<Level> MOON_ORBIT_KEY =
            ResourceKey.create(Registries.DIMENSION,
                    new ResourceLocation(CreatingSpace.MODID,"moon_orbit"));

    public static final ResourceKey<Level> MOON_KEY =
            ResourceKey.create(Registries.DIMENSION,
                    new ResourceLocation(CreatingSpace.MODID,"the_moon"));

    public static final ResourceKey<DimensionType> EARTH_ORBIT_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    new ResourceLocation(CreatingSpace.MODID,"earth_orbit"));
    public static final ResourceKey<DimensionType> MOON_ORBIT_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    new ResourceLocation(CreatingSpace.MODID,"moon_orbit"));

    public static final ResourceKey<DimensionType> MOON_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    new ResourceLocation(CreatingSpace.MODID,"the_moon"));
    public static double SpaceSpawnHeight = 64; //make the rocket came from the top rather than hard tp -> for future version
    public static double PlanetSpawnHeight = 200;

    public static void register(IEventBus bus) {
        System.out.println("Registering Dimension for : "+ CreatingSpace.MODID);
    }

    public static float gravity(ResourceKey<DimensionType> dimensionType) {
        if(dimensionType == EARTH_ORBIT_TYPE){
            return 0f;
        }
        if (dimensionType ==MOON_ORBIT_TYPE){
            return 0f;
        }
        if (dimensionType == MOON_TYPE){
             return 0.2f;
        }
        return 1f;
    }

    public static List<ResourceKey<Level>> accessibleFrom(ResourceKey<Level> currentDimension) {
        if (currentDimension == EARTH_ORBIT_KEY){

            return List.of(Level.OVERWORLD,MOON_ORBIT_KEY);
        }
        if (currentDimension == MOON_ORBIT_KEY){
            return List.of(EARTH_ORBIT_KEY,MOON_KEY);
        }
        if (currentDimension == MOON_KEY){
            return List.of(MOON_ORBIT_KEY);
        }
        return List.of(EARTH_ORBIT_KEY);
    }
}


