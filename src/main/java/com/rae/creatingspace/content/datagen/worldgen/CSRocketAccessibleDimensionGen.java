package com.rae.creatingspace.content.datagen.worldgen;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class CSRocketAccessibleDimensionGen {

    public static void bootstrap(BootstrapContext<RocketAccessibleDimension> context) {
        // --- Earth Orbit ---
        context.register(
                ResourceKey.create(RocketAccessibleDimension.REGISTRY_KEY, CreatingSpace.resource("earth_orbit")),
                new RocketAccessibleDimension(
                        100,
                        ResourceLocation.withDefaultNamespace("overworld"),
                        64,
                        0f,
                        Map.of(
                                CreatingSpace.resource("moon_orbit"), new RocketAccessibleDimension.AccessibilityParameter(600, 64),
                                ResourceLocation.withDefaultNamespace("overworld"), new RocketAccessibleDimension.AccessibilityParameter(200, 200),
                                CreatingSpace.resource("mars_orbit"), new RocketAccessibleDimension.AccessibilityParameter(1000, 64),
                                CreatingSpace.resource("venus"), new RocketAccessibleDimension.AccessibilityParameter(2200, 200)
                        )
                )
        );

        // --- The Moon ---
        context.register(
                ResourceKey.create(RocketAccessibleDimension.REGISTRY_KEY, CreatingSpace.resource("the_moon")),
                new RocketAccessibleDimension(
                        400,
                        ResourceLocation.withDefaultNamespace("overworld"),
                        300,
                        1.6f,
                        Map.of(
                                CreatingSpace.resource("moon_orbit"), new RocketAccessibleDimension.AccessibilityParameter(300, 64)
                        )
                )
        );

        // --- Moon Orbit ---
        context.register(
                ResourceKey.create(RocketAccessibleDimension.REGISTRY_KEY, CreatingSpace.resource("moon_orbit")),
                new RocketAccessibleDimension(
                        100,
                        CreatingSpace.resource("the_moon"),
                        64,
                        0f,
                        Map.of(
                                CreatingSpace.resource("earth_orbit"), new RocketAccessibleDimension.AccessibilityParameter(200, 64),
                                CreatingSpace.resource("mars_orbit"), new RocketAccessibleDimension.AccessibilityParameter(1000, 64),
                                CreatingSpace.resource("venus"), new RocketAccessibleDimension.AccessibilityParameter(2200, 200),
                                CreatingSpace.resource("the_moon"), new RocketAccessibleDimension.AccessibilityParameter(300, 200)
                        )
                )
        );

        // --- Mars ---
        context.register(
                ResourceKey.create(RocketAccessibleDimension.REGISTRY_KEY, CreatingSpace.resource("mars")),
                new RocketAccessibleDimension(
                        2000,
                        CreatingSpace.resource("sun"),
                        300,
                        3.71f,
                        Map.of(
                                CreatingSpace. resource("mars_orbit"), new RocketAccessibleDimension.AccessibilityParameter(400, 64)
                        )
                )
        );

        // --- Mars Orbit ---
        context.register(
                ResourceKey.create(RocketAccessibleDimension.REGISTRY_KEY, CreatingSpace.resource("mars_orbit")),
                new RocketAccessibleDimension(
                        100,
                        CreatingSpace.resource("mars"),
                        64,
                        0f,
                        Map.of(
                                CreatingSpace.resource("earth_orbit"), new RocketAccessibleDimension.AccessibilityParameter(500, 64),
                                CreatingSpace.resource("moon_orbit"), new RocketAccessibleDimension.AccessibilityParameter(300, 64),
                                CreatingSpace.resource("mars"), new RocketAccessibleDimension.AccessibilityParameter(300, 200)
                        )
                )
        );

        // --- Venus ---
        context.register(
                ResourceKey.create(RocketAccessibleDimension.REGISTRY_KEY, CreatingSpace.resource("venus")),
                new RocketAccessibleDimension(
                        400,
                        CreatingSpace.resource("sun"),
                        300,
                        8.87f,
                        Map.of(
                                CreatingSpace.resource("earth_orbit"), new RocketAccessibleDimension.AccessibilityParameter(1800, 64),
                                CreatingSpace.resource("moon_orbit"), new RocketAccessibleDimension.AccessibilityParameter(2000, 64)
                        )
                )
        );
    }
}