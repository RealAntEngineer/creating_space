package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.CuttingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.rae.creatingspace.CreatingSpace.resource;
import static com.simibubi.create.AllTags.commonItemTag;

@SuppressWarnings("unused")
public class CSCuttingRecipeGen extends CuttingRecipeGen {
    public CSCuttingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, CreatingSpace.MODID);

        GeneratedRecipe

                ANDESITE_ENGINE_WALL = create(AllItems.ANDESITE_ALLOY::get, b -> b
                        .duration(100)
                        .output(BuiltInRegistries.ITEM.get(resource("andesite_engine_wall")))),
                COPPER_ENGINE_WALL = create(Items.COPPER_INGOT::asItem,b -> b
                        .duration(100)
                        .output(BuiltInRegistries.ITEM.get(resource("copper_engine_wall")))),
                IRON_ENGINE_WALL = create(Items.IRON_INGOT::asItem,b -> b
                        .duration(100)
                        .output(BuiltInRegistries.ITEM.get(resource("iron_engine_wall")))),
                BRASS_ENGINE_WALL = create(AllItems.BRASS_SHEET::get,b -> b
                        .duration(100)
                        .output(BuiltInRegistries.ITEM.get(resource("brass_engine_wall")))),
                REINFORCED_COPPER_ENGINE_WALL = create("reinforced_copper_sheet", b -> b
                        .duration(100)
                        .output(BuiltInRegistries.ITEM.get(resource("reinforced_copper_engine_wall")))),
                COPRONICKEL_ENGINE_WALL = create("copronickel_sheet", b -> b
                        .duration(100)
                        .output(BuiltInRegistries.ITEM.get(resource("copronickel_engine_wall")))),
                MONEL_ENGINE_WALL = create("monel_sheet", b -> b
                        .duration(100)
                        .output(BuiltInRegistries.ITEM.get(resource("monel_engine_wall")))),
                INCONEL_ENGINE_WALL = create("inconel_sheet", b -> b
                        .duration(100)
                        .output(BuiltInRegistries.ITEM.get(resource("inconel_engine_wall")))),
                HASTELLOY_ENGINE_WALL = create("hastelloy_sheet", b -> b
                        .duration(100)
                        .output(BuiltInRegistries.ITEM.get(resource("hastelloy_engine_wall"))));
    }
}
