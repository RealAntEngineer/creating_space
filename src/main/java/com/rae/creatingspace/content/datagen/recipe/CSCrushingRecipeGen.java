package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static com.simibubi.create.AllTags.commonItemTag;

@SuppressWarnings("unused")
public class CSCrushingRecipeGen extends CrushingRecipeGen {
    public CSCrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, CreatingSpace.MODID);
    }

    GeneratedRecipe

        COAL_DUST = create("coal_dust", b -> b
            .duration(400)
            .require(Items.COAL)
            .output(ItemInit.COAL_DUST, 2)),

        NICKEL_DUST = create("nickel_dust", b -> b
                .duration(400)
                .require(commonItemTag("ingots/nickel"))
                .output(ItemInit.NICKEL_DUST, 2)
                .output(0.5F, ItemInit.NICKEL_DUST, 1)),

        MOON_REGOLITH = create("moon_stone", b -> b
            .duration(400)
            .require(BlockInit.MOON_STONE)
            .output(BlockInit.MOON_REGOLITH, 1)),

        MARS_REGOLITH = create("mars_stone", b -> b
                .duration(400)
                .require(BlockInit.MARS_STONE)
                .output(BlockInit.MARS_REGOLITH, 1)),

        NICKEL_ORE = stoneOre(() -> BlockInit.NICKEL_ORE, ItemInit.CRUSHED_NICKEL_ORE::get, 1.75f, 400),
        DEEPSLATE_NICKEL_ORE = deepslateOre(() -> BlockInit.DEEPSLATE_NICKEL_ORE, ItemInit.CRUSHED_NICKEL_ORE::get, 2.25f, 400),
        ALUMINUM_ORE = ore(() -> BlockInit.MOON_STONE.asItem(), BlockInit.MOON_ALUMINUM_ORE::get, ItemInit.CRUSHED_ALUMINUM_ORE::get, 1.75f, 400),
        COBALT_ORE = ore(() -> BlockInit.MOON_STONE.asItem(), BlockInit.MOON_COBALT_ORE::get, ItemInit.CRUSHED_COBALT_ORE::get, 1.75f, 400),
        RAW_NICKEL_ORE = rawOre("nickel", () -> commonItemTag("raw_materials/nickel"), ItemInit.CRUSHED_NICKEL_ORE::get, 1),
        RAW_ALUMINUM_ORE = rawOre("aluminum", () -> commonItemTag("raw_materials/aluminum"), ItemInit.CRUSHED_ALUMINUM_ORE::get, 1),
        RAW_COBALT_ORE = rawOre("cobalt", () -> commonItemTag("raw_materials/cobalt"), ItemInit.CRUSHED_COBALT_ORE::get, 1),
        RAW_NICKEL_BLOCK = rawOreBlock("nickel", () -> commonItemTag("storage_blocks/raw_nickel"), ItemInit.CRUSHED_NICKEL_ORE::get, 1),
        RAW_ALUMINUM_BLOCK = rawOreBlock("aluminum", () -> commonItemTag("storage_blocks/raw_aluminum"), ItemInit.CRUSHED_ALUMINUM_ORE::get, 1),
        RAW_COBALT_BLOCK = rawOreBlock("cobalt", () -> commonItemTag("storage_blocks/raw_cobalt"), ItemInit.CRUSHED_COBALT_ORE::get, 1);
}
