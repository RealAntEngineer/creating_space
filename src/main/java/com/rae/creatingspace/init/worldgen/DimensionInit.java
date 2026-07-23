package com.rae.creatingspace.init.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

import static com.rae.creatingspace.CreatingSpace.resource;

public class DimensionInit {

    public static final ResourceKey<Level> EARTH_ORBIT_KEY =
            ResourceKey.create(Registries.DIMENSION,
                    resource("earth_orbit"));

    public static final ResourceKey<Level> MOON_ORBIT_KEY =
            ResourceKey.create(Registries.DIMENSION,
                    resource("moon_orbit"));

    public static final ResourceKey<Level> MOON_KEY =
            ResourceKey.create(Registries.DIMENSION,
                    resource("the_moon"));

    public static final ResourceKey<Level> MARS_KEY =
            ResourceKey.create(Registries.DIMENSION,
                    resource("mars"));

    public static final ResourceKey<DimensionType> EARTH_ORBIT_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                   resource("earth_orbit"));
    public static final ResourceKey<DimensionType> MOON_ORBIT_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, resource("moon_orbit"));

    public static final ResourceKey<DimensionType> MOON_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    resource("the_moon"));
    public static final ResourceKey<DimensionType> MARS_ORBIT_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    resource("mars_orbit"));
    public static final ResourceKey<DimensionType> MARS_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, resource("mars"));

    public static final ResourceKey<DimensionType> VENUS_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, resource("venus"));

    //LevelStem Keys (used in DataGen)
    public static final ResourceKey<LevelStem> EARTH_ORBIT_STEM_KEY =
            ResourceKey.create(Registries.LEVEL_STEM, resource("earth_orbit"));

    public static final ResourceKey<LevelStem> MOON_ORBIT_STEM_KEY =
            ResourceKey.create(Registries.LEVEL_STEM, resource("moon_orbit"));

    public static final ResourceKey<LevelStem> MARS_ORBIT_STEM_KEY =
            ResourceKey.create(Registries.LEVEL_STEM, resource("mars_orbit"));

    public static final ResourceKey<LevelStem> VENUS_ORBIT_STEM_KEY =
            ResourceKey.create(Registries.LEVEL_STEM, resource("venus_orbit"));

    public static final ResourceKey<LevelStem> MOON_STEM_KEY =
            ResourceKey.create(Registries.LEVEL_STEM, resource("the_moon"));

    public static final ResourceKey<LevelStem> MARS_STEM_KEY =
            ResourceKey.create(Registries.LEVEL_STEM, resource("mars"));

    public static final ResourceKey<LevelStem> VENUS_STEM_KEY =
            ResourceKey.create(Registries.LEVEL_STEM, resource("venus"));
}


