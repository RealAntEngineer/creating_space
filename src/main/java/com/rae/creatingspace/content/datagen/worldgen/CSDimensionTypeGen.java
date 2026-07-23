package com.rae.creatingspace.content.datagen.worldgen;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.data.worldgen.BootstrapContext;

import java.util.OptionalLong;

import static com.rae.creatingspace.CreatingSpace.resource;
import static com.rae.creatingspace.init.worldgen.DimensionInit.*;

public class CSDimensionTypeGen {
    private static final int minY = -64;
    private static final int dimHeight = 384;
    private static final int dimLogicalHeight = 384;

    public static DimensionType earthOrbit() {
        return new DimensionType(
                OptionalLong.empty(), // normal day/night cycle
                true,  // has skylight
                false, // no ceiling
                false, // not ultrawarm (Nether-like)
                true,  // natural
                1.0,   // coordinate scale
                true,  // beds work
                true,  // respawn anchor works
                minY,   // minY
                dimHeight,   // height
                dimLogicalHeight,   // logical height
                BlockTags.INFINIBURN_OVERWORLD,
                resource("earth_orbit"),
                0,
                new DimensionType.MonsterSettings(true, false, net.minecraft.util.valueproviders.ConstantInt.of(0), 7)
        );
    }

    public static DimensionType mars() {
        return new DimensionType(
                OptionalLong.empty(), // normal day/night cycle
                true,  // has skylight
                false, // no ceiling
                false, // not ultrawarm (Nether-like)
                true,  // natural
                1.0,   // coordinate scale
                true,  // beds work
                true,  // respawn anchor works
                minY,   // minY
                dimHeight,   // height
                dimLogicalHeight,   // logical height
                BlockTags.INFINIBURN_OVERWORLD,
                resource("mars"),
                0,
                new DimensionType.MonsterSettings(true, false, net.minecraft.util.valueproviders.ConstantInt.of(0), 7)
        );
    }

    public static DimensionType marsOrbit() {
        return new DimensionType(
                OptionalLong.empty(), // normal day/night cycle
                true,  // has skylight
                false, // no ceiling
                false, // not ultrawarm (Nether-like)
                true,  // natural
                1.0,   // coordinate scale
                true,  // beds work
                true,  // respawn anchor works
                minY,   // minY
                dimHeight,   // height
                dimLogicalHeight,   // logical height
                BlockTags.INFINIBURN_OVERWORLD,
                resource("mars_orbit"),
                0,
                new DimensionType.MonsterSettings(true, false, net.minecraft.util.valueproviders.ConstantInt.of(0), 7)
        );
    }

    public static DimensionType moonOrbit() {
        return new DimensionType(
                OptionalLong.empty(), // normal day/night cycle
                true,  // has skylight
                false, // no ceiling
                false, // not ultrawarm (Nether-like)
                true,  // natural
                1.0,   // coordinate scale
                true,  // beds work
                true,  // respawn anchor works
                minY,   // minY
                dimHeight,   // height
                dimLogicalHeight,   // logical height
                BlockTags.INFINIBURN_OVERWORLD,
                resource("moon_orbit"),
                0,
                new DimensionType.MonsterSettings(true, false, net.minecraft.util.valueproviders.ConstantInt.of(0), 7)
        );
    }

    public static DimensionType theMoon() {
        return new DimensionType(
                OptionalLong.empty(), // normal day/night cycle
                true,  // has skylight
                false, // no ceiling
                false, // not ultrawarm (Nether-like)
                true,  // natural
                1.0,   // coordinate scale
                true,  // beds work
                true,  // respawn anchor works
                minY,   // minY
                dimHeight,   // height
                dimLogicalHeight,   // logical height
                BlockTags.INFINIBURN_OVERWORLD,
                resource("the_moon"),
                0,
                new DimensionType.MonsterSettings(true, false, net.minecraft.util.valueproviders.ConstantInt.of(0), 7)
        );
    }

    public static DimensionType venus() {
        return new DimensionType(
                OptionalLong.empty(), // normal day/night cycle
                true,  // has skylight
                false, // no ceiling
                false, // not ultrawarm (Nether-like)
                true,  // natural
                1.0,   // coordinate scale
                true,  // beds work
                true,  // respawn anchor works
                minY,   // minY
                dimHeight,   // height
                dimLogicalHeight,   // logical height
                BlockTags.INFINIBURN_OVERWORLD,
                resource("venus"),
                0,
                new DimensionType.MonsterSettings(true, false, net.minecraft.util.valueproviders.ConstantInt.of(0), 7)
        );
    }

    public static void bootstrap(BootstrapContext<DimensionType> context) {
        context.register(EARTH_ORBIT_TYPE, earthOrbit());
        context.register(MARS_TYPE, mars());
        context.register(MARS_ORBIT_TYPE, marsOrbit());
        context.register(MOON_ORBIT_TYPE, moonOrbit());
        context.register(MOON_TYPE, theMoon());
        context.register(VENUS_TYPE, venus());
    }
}
