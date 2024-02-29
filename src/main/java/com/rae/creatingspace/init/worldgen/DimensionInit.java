package com.rae.creatingspace.init.worldgen;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionInit {

    //TODO should not be needed (generated in datapack)
    // only usage in dimension effect
    public static final ResourceKey<DimensionType> EARTH_ORBIT_TYPE =
            ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
                    CreatingSpace.resource("earth_orbit"));
    public static final ResourceKey<DimensionType> MOON_ORBIT_TYPE =
            ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
                    CreatingSpace.resource("moon_orbit"));

    public static final ResourceKey<DimensionType> MOON_TYPE =
            ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
                    CreatingSpace.resource("the_moon"));
    public static final ResourceKey<DimensionType> MARS_ORBIT_TYPE =
            ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
                    CreatingSpace.resource("mars_orbit"));
    public static final ResourceKey<DimensionType> MARS_TYPE =
            ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
                    CreatingSpace.resource("mars"));
}


