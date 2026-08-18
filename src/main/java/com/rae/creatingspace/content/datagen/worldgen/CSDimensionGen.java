package com.rae.creatingspace.content.datagen.worldgen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Optional;

import static com.rae.creatingspace.CreatingSpace.resource;
import static com.rae.creatingspace.init.worldgen.DimensionInit.*;

record BiomeParams(
    String name,
    float tempMin, float tempMax,
    float humidMin, float humidMax,
    float contMin, float contMax,
    float erosionMin, float erosionMax,
    float weirdMin, float weirdMax,
    float depthMin, float depthMax,
    float offset
) {
    public Climate.ParameterPoint toParameterPoint() {
        return Climate.parameters(
            Climate.Parameter.span(tempMin, tempMax),
            Climate.Parameter.span(humidMin, humidMax),
            Climate.Parameter.span(contMin, contMax),
            Climate.Parameter.span(erosionMin, erosionMax),
            Climate.Parameter.span(depthMin, depthMax),
            Climate.Parameter.span(weirdMin, weirdMax),
            offset
        );
    }
}

public class CSDimensionGen {

    private static ResourceKey<LevelStem> key(String name) {
        return ResourceKey.create(Registries.LEVEL_STEM, resource(name));
    }

    public static void bootstrap(BootstrapContext<LevelStem> context) {
        HolderGetter<DimensionType> dimTypeGetter = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGetter = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);

        // Orbits (flat)
        registerFlatOrbit(context, EARTH_ORBIT_STEM_KEY, EARTH_ORBIT_TYPE, biomeGetter);
        registerFlatOrbit(context, MARS_ORBIT_STEM_KEY, MARS_ORBIT_TYPE, biomeGetter);
        registerFlatOrbit(context, MOON_ORBIT_STEM_KEY, MOON_ORBIT_TYPE, biomeGetter);
//        registerFlatOrbit(context, VENUS_ORBIT_STEM_KEY, VENUS_ORBIT_TYPE, biomeGetter);

        // Terrain worlds (noise-based)
        registerMultiNoiseWorld(context, MARS_STEM_KEY, MARS_TYPE, "mars_noise",
                List.of(
                    new BiomeParams("mars_plains",-1F, 1F, -1F, 1F, -1F, 1F, -1F, 1F, -1F, 1F, 0.0F, 0.0F, 0.0F),
                    new BiomeParams("mars_cave",-1F, 1F, -1F, 1F, -1F, 1F, -1F, 1F, -1F, 1F, 0.2F, 0.9F, 0.0F)
                ),
                noiseGetter, biomeGetter);
        registerMultiNoiseWorld(context, MOON_STEM_KEY, MOON_TYPE, "moon_noise",
                List.of(
                    new BiomeParams("moon_plains", -1F, 1F, -1F, 1F, -1F, 1F, -1F, 1F, -1F, 1F, 0.0F, 0.0F, 0.0F),
                    new BiomeParams("moon_cave", -1F, 1F, -1F, 1F, -1F, 1F, -1F, 1F, -1F, 1F, 0.2F, 0.9F, 0.0F)
                ),
                noiseGetter, biomeGetter);
        registerMultiNoiseWorld(context, VENUS_STEM_KEY, VENUS_TYPE, "venus",
                List.of(
                    new BiomeParams("venus", 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                    new BiomeParams("venus_hellground", 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F)
                ),
                noiseGetter, biomeGetter);
    }

    private static void registerFlatOrbit(BootstrapContext<LevelStem> context,
                                          ResourceKey<LevelStem> stemKey,
                                          ResourceKey<DimensionType> typeKey,
                                          HolderGetter<Biome> biomeGetter) {

        var biomeHolder = biomeGetter.getOrThrow(ResourceKey.create(Registries.BIOME, resource("space")));

        // --- Create an empty flat world (no structures, no layers, no features) ---
        var flatSettings = new FlatLevelGeneratorSettings(
                Optional.empty(),      // no structures
                biomeHolder,           // space biome
                List.of()             // no layers
        );

        var flatGenerator = new FlatLevelSource(flatSettings);

        context.register(stemKey, new LevelStem(
                context.lookup(Registries.DIMENSION_TYPE).getOrThrow(typeKey),
                flatGenerator
        ));
    }

    private static void registerFixedNoiseWorld(BootstrapContext<LevelStem> context,
                                                ResourceKey<LevelStem> stemKey,
                                                ResourceKey<DimensionType> typeKey,
                                                String noise, String biomeName,
                                                HolderGetter<NoiseGeneratorSettings> noiseGetter,
                                                HolderGetter<Biome> biomeGetter) {
        var biomeSource = new FixedBiomeSource(
                biomeGetter.getOrThrow(ResourceKey.create(Registries.BIOME, resource(biomeName)))
        );

        context.register(stemKey, new LevelStem(
                context.lookup(Registries.DIMENSION_TYPE).getOrThrow(typeKey),
                new NoiseBasedChunkGenerator(
                        biomeSource,
                        noiseGetter.getOrThrow(ResourceKey.create(Registries.NOISE_SETTINGS, resource(noise)))
                )
        ));
    }

    private static void registerMultiNoiseWorld(
            BootstrapContext<LevelStem> context,
            ResourceKey<LevelStem> stemKey,
            ResourceKey<DimensionType> typeKey,
            String noise,
            List<BiomeParams> biomeParams,
            HolderGetter<NoiseGeneratorSettings> noiseGetter,
            HolderGetter<Biome> biomeGetter) {

        var biomeEntries = biomeParams.stream()
            .map(params -> {
                var biomeKey = ResourceKey.create(Registries.BIOME, resource(params.name()));
                var biomeHolder = biomeGetter.getOrThrow(biomeKey);
                return Pair.of(params.toParameterPoint(), biomeHolder);
            })
            .toList();

        var parameterList = new Climate.ParameterList<Holder<Biome>>(
                biomeEntries.stream()
                        .map(entry -> Pair.of(entry.getFirst(), (Holder<Biome>) entry.getSecond()))
                        .toList()
        );
        var biomeSource = MultiNoiseBiomeSource.createFromList(parameterList);
        var noiseSettings = noiseGetter.getOrThrow(ResourceKey.create(Registries.NOISE_SETTINGS, resource(noise)));

        context.register(stemKey, new LevelStem(
            context.lookup(Registries.DIMENSION_TYPE).getOrThrow(typeKey),
            new NoiseBasedChunkGenerator(biomeSource, noiseSettings)
        ));
    }
}