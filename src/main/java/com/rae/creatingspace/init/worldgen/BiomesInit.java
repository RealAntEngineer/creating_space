package com.rae.creatingspace.init.worldgen;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.server.worldgen.dimension.MoonDimension;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;


public class BiomesInit {
    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(ForgeRegistries.BIOMES, CreatingSpace.MODID);

    public static final RegistryObject<Biome> MOON_PLAINS = BIOMES.register("moon_plains", ()-> MoonDimension.moonPlains());

    public static final RegistryObject<Biome> SPACE = BIOMES.register("space",()-> space());

    public static Biome space() {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = new BiomeGenerationSettings.Builder();
        Music music = Musics.createGameMusic(SoundEvents.MUSIC_GAME);
        return biome(Biome.Precipitation.NONE, 0.5F, 0.5F, new MobSpawnSettings.Builder(), biomegenerationsettings$builder, music);
    }

    protected static int calculateSkyColor(float temperature) {
        float $$1 = temperature / 3.0F;
        $$1 = Mth.clamp($$1, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
    }
    private static Biome biome(Biome.Precipitation precipitation, float temperature, float downfall, MobSpawnSettings.Builder mobSpawnBuilder, BiomeGenerationSettings.Builder biomeGenerationBuilder, @Nullable Music music) {
        return biome(precipitation, temperature, downfall, 4159204, 329011, mobSpawnBuilder, biomeGenerationBuilder, music);
    }

    private static Biome biome(Biome.Precipitation precipitation, float temperature, float downfall, int waterColor, int waterFogColor, MobSpawnSettings.Builder mobSpawnBuilder, BiomeGenerationSettings.Builder biomeGenerationBuilder, @Nullable Music music) {
        return
                (new Biome.BiomeBuilder())
                        .precipitation(precipitation)
                        .temperature(temperature)
                        .downfall(downfall)
                        .specialEffects((new BiomeSpecialEffects.Builder())
                                .waterColor(waterColor)
                                .waterFogColor(waterFogColor)
                                .fogColor(16777215)
                                .skyColor(calculateSkyColor(temperature))
                                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                                .backgroundMusic(music).build())
                        .mobSpawnSettings(mobSpawnBuilder.build())
                        .generationSettings(biomeGenerationBuilder.build())
                        .build();
    }

    public static void register(IEventBus bus) {
        BIOMES.register(bus);
    }


}
