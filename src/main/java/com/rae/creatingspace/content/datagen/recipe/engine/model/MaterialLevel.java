package com.rae.creatingspace.content.datagen.recipe.engine.model;

import net.minecraft.resources.ResourceLocation;

import static com.rae.creatingspace.CreatingSpace.resource;

/**
 * Represents one of the 9 material levels used in Creating Space engine component recipes.
 * Each level defines:
 *  - The integer level index used in engineRecipeData.materialLevel
 *  - A material key (e.g., "iron", "brass")
 *  - A text-safe folder suffix used in recipe paths
 *  - A prefix for material-specific items (e.g., "creatingspace:iron_")
 */
public enum MaterialLevel {
    LEVEL_0_ANDESITE(0, "andesite"),
    LEVEL_1_IRON(1, "iron"),
    LEVEL_2_COPPER(2, "copper"),
    LEVEL_3_BRASS(3, "brass"),
    LEVEL_4_REINFORCED_COPPER(4, "reinforced_copper"),
    LEVEL_5_COPRONICKEL(5, "copronickel"),
    LEVEL_6_MONEL(6, "monel"),
    LEVEL_7_INCONEL(7, "inconel"),
    LEVEL_8_HASTELLOY(8, "hastelloy");

    private final int level;
    private final String key;
    private final String folderName;
    private final ResourceLocation basePrefix;

    MaterialLevel(int level, String key) {
        this.level = level;
        this.key = key;
        this.folderName = "lvl" + level;
        this.basePrefix = resource( key + "_");
    }

    /** The numeric material level (used in engineRecipeData.materialLevel). */
    public int level() {
        return level;
    }

    /** Material key (e.g., "iron"). */
    public String key() {
        return key;
    }

    /** Folder segment (e.g., "level1-iron"). */
    public String folderName() {
        return folderName;
    }

    /** Prefix for item IDs (e.g., "creatingspace:iron_"). */
    public ResourceLocation basePrefix() {
        return basePrefix;
    }

    /** Returns a namespaced item ID with this material’s prefix. */
    public String itemId(String suffix) {
        return basePrefix.getNamespace() + ":" + basePrefix.getPath() + suffix;
    }
}
