package com.rae.creatingspace.content.planets.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.init.TagsInit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class CraterCarverConfig  extends CarverConfiguration {
    public static final Codec<CraterCarverConfig> CRATER_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("probability").forGetter(i -> i.probability),
            HeightProvider.CODEC.fieldOf("y").forGetter(i -> i.y),
            FloatProvider.CODEC.fieldOf("y_scale").forGetter(i -> i.yScale),
            CarverDebugSettings.CODEC.fieldOf("debug_settings").forGetter(i -> i.debugSettings),
            Codec.INT.fieldOf("max_radius").forGetter(i -> i.maxRadius),
            Codec.INT.fieldOf("min_radius").forGetter(i -> i.minRadius),
            Codec.INT.fieldOf("ideal_range_offset").forGetter(i -> i.idealRangeOffset)
    ).apply(instance, CraterCarverConfig::new));

    public final int maxRadius;
    public final int minRadius;
    public final int idealRangeOffset;

    public CraterCarverConfig(float probability, HeightProvider y, FloatProvider yScale, CarverDebugSettings carverDebugConfig, int maxRadius, int minRadius, int idealRangeOffset) {
        super(probability, y, yScale, VerticalAnchor.absolute(-64), carverDebugConfig, BuiltInRegistries.BLOCK.getOrCreateTag(TagsInit.CustomBlockTags.MOON_CARVER_REPLACEABLES.tag));
        this.maxRadius = maxRadius;
        this.minRadius = minRadius;
        this.idealRangeOffset = idealRangeOffset;
    }
}
