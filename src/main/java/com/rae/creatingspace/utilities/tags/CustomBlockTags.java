package com.rae.creatingspace.utilities.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CustomBlockTags {
    public static final TagKey<Block> MOON_STONE_REPLACEABLE =
            TagKey.create(Registry.BLOCK_REGISTRY,
                    new ResourceLocation("moon_stone_ore_replaceable"));

    public static final TagKey<Block> MOON_CARVER_REPLACEABLE =
            TagKey.create(Registry.BLOCK_REGISTRY,
                    new ResourceLocation("moon_carver_replaceable"));


}
