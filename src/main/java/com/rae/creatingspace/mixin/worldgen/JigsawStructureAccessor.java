package com.rae.creatingspace.mixin.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(JigsawStructure.class)
public interface JigsawStructureAccessor {
    @Accessor("startPool")
    Holder<StructureTemplatePool> getStartPool();

    @Accessor("startJigsawName")
    Optional<ResourceLocation> getStartJigsawName();

    @Accessor("maxDepth")
    int getMaxDepth();

    @Accessor("startHeight")
    HeightProvider getStartHeight();

    @Accessor("useExpansionHack")
    boolean getUseExpansionHack();

    @Accessor("projectStartToHeightmap")
    Optional<Heightmap.Types> getProjectStartToHeightmap();

    @Accessor("maxDistanceFromCenter")
    int getMaxDistanceFromCenter();
}

