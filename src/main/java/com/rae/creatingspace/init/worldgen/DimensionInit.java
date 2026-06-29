package com.rae.creatingspace.init.worldgen;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionInit {

    public static final ResourceKey<Level> EARTH_ORBIT_LEVEL =
            ResourceKey.create(Registries.DIMENSION,
                    CreatingSpace.resource("earth_orbit"));

    public static final ResourceKey<Level> MOON_ORBIT_LEVEL =
            ResourceKey.create(Registries.DIMENSION,
                    CreatingSpace.resource("moon_orbit"));

    public static final ResourceKey<Level> MOON_LEVEL =
            ResourceKey.create(Registries.DIMENSION,
                    CreatingSpace.resource("the_moon"));

    public static final ResourceKey<Level> MARS_LEVEL =
    ResourceKey.create(Registries.DIMENSION,
            CreatingSpace.resource("mars"));

    public static final ResourceKey<DimensionType> EARTH_ORBIT_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                   CreatingSpace.resource("earth_orbit"));

    public static final ResourceKey<DimensionType> MOON_ORBIT_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    CreatingSpace.resource("moon_orbit"));

    public static final ResourceKey<DimensionType> MOON_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    CreatingSpace.resource("the_moon"));

    public static final ResourceKey<DimensionType> MARS_ORBIT_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    CreatingSpace.resource("mars_orbit"));

    public static final ResourceKey<DimensionType> MARS_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    CreatingSpace.resource("mars"));
}


