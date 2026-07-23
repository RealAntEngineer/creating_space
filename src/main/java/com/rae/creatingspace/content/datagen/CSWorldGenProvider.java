package com.rae.creatingspace.content.datagen;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.content.datagen.worldgen.CSDimensionGen;
import com.rae.creatingspace.content.datagen.worldgen.CSDimensionTypeGen;
import com.rae.creatingspace.content.datagen.worldgen.CSRocketAccessibleDimensionGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CSWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, CSDimensionTypeGen::bootstrap)
            .add(Registries.LEVEL_STEM, CSDimensionGen::bootstrap)
            .add(RocketAccessibleDimension.REGISTRY_KEY, CSRocketAccessibleDimensionGen::bootstrap);

    public CSWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(CreatingSpace.MODID));
    }
}