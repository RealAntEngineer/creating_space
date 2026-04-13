package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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

        NICKEL_ORE = ore(Items.COBBLESTONE, BlockInit.NICKEL_ORE::get, AllItems.CRUSHED_NICKEL::get, 1.75f, 400),
        DEEPSLATE_NICKEL_ORE = ore(Items.COBBLED_DEEPSLATE, BlockInit.DEEPSLATE_NICKEL_ORE::get, AllItems.CRUSHED_NICKEL::get, 2.25f, 400),
        ALUMINUM_ORE = ore(BlockInit.MOON_STONE.asItem(), BlockInit.MOON_ALUMINUM_ORE::get, AllItems.CRUSHED_BAUXITE::get, 1.75f, 400),
        COBALT_ORE = ore(BlockInit.MOON_STONE.asItem(), BlockInit.MOON_COBALT_ORE::get, ItemInit.CRUSHED_COBALT_ORE::get, 1.75f, 400),
        RAW_NICKEL_ORE = rawOre("nickel", () -> commonItemTag("raw_materials/nickel"), AllItems.CRUSHED_NICKEL::get, 1),
        RAW_ALUMINUM_ORE = rawOre("aluminum", () -> commonItemTag("raw_materials/aluminum"), AllItems.CRUSHED_BAUXITE::get, 1),
        RAW_COBALT_ORE = rawOre("cobalt", () -> commonItemTag("raw_materials/cobalt"), ItemInit.CRUSHED_COBALT_ORE::get, 1),
        RAW_NICKEL_BLOCK = rawOreBlock("nickel", () -> commonItemTag("storage_blocks/raw_nickel"), AllItems.CRUSHED_NICKEL::get, 1),
        RAW_ALUMINUM_BLOCK = rawOreBlock("aluminum", () -> commonItemTag("storage_blocks/raw_aluminum"),AllItems.CRUSHED_BAUXITE::get /*ItemInit.CRUSHED_ALUMINUM_ORE::get*/, 1),
        RAW_COBALT_BLOCK = rawOreBlock("cobalt", () -> commonItemTag("storage_blocks/raw_cobalt"), ItemInit.CRUSHED_COBALT_ORE::get, 1);

protected GeneratedRecipe ore(ItemLike stoneType, Supplier<ItemLike> ore, Supplier<ItemLike> raw,
                              float expectedAmount, int duration) {
    return create(CreatingSpace.MODID, ore, b -> {
        b.duration(duration)
                .output(raw.get(), Mth.floor(expectedAmount));
        float extra = expectedAmount - Mth.floor(expectedAmount);
        if (extra > 0)
            b.output(extra, raw.get(), 1);
        b.output(.75f, AllItems.EXP_NUGGET.get(), raw.get() == AllItems.CRUSHED_GOLD.get() ? 2 : 1);
        return b.output(.125f, stoneType);
    });
}
}
