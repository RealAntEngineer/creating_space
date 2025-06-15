package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.datagen.CSMetalSets;
import com.rae.creatingspace.init.EngineMaterialInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.recipe.PressingRecipeGen;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

import static com.rae.creatingspace.CreatingSpace.resource;

public class CSMetalRecipeHelper {

    public static void generateMetalRecipes(RecipeOutput output, CSMetalSets.MetalSet metal) {
        // 9 Nuggets → 1 Ingot
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, metal.ingot())
                .define('#', AllTags.commonItemTag("nuggets/" + metal.name()))
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_nugget", has(metal.nugget()))
                .save(output, resource(metal.name() + "_ingot_from_nuggets"));

        // 1 Ingot → 9 Nuggets
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, metal.nugget(), 9)
                .requires(AllTags.commonItemTag("ingots/" + metal.name()))
                .unlockedBy("has_ingot", has(metal.ingot()))
                .save(output, resource(metal.name() + "_nugget"));

        // 9 Ingots → 1 Block
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, metal.block())
                .define('#', AllTags.commonItemTag("ingots/" + metal.name()))
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_ingot", has(metal.ingot()))
                .save(output, resource(metal.name() + "_block"));

        // 1 Block → 9 Ingots
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, metal.ingot(), 9)
                .requires(AllTags.commonItemTag("storage_blocks/" + metal.name()))
                .unlockedBy("has_block", has(metal.block()))
                .save(output, resource(metal.name() + "_ingot_from_block"));
    }

    public static void generateMetalAlloyRecipes(RecipeOutput output, CSMetalSets.MetalSet metal) {
        // 9 Nuggets → 1 Ingot
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, metal.ingot())
                .define('#', AllTags.commonItemTag("nuggets/" + metal.name()))
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_nugget", has(metal.nugget()))
                .save(output, ResourceLocation.fromNamespaceAndPath(CreatingSpace.MODID, metal.name() + "_ingot_from_nuggets"));

        // 1 Ingot → 9 Nuggets
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, metal.nugget(), 9)
                .requires(AllTags.commonItemTag("ingots/" + metal.name()))
                .unlockedBy("has_ingot", has(metal.ingot()))
                .save(output, ResourceLocation.fromNamespaceAndPath(CreatingSpace.MODID, metal.name() + "_nugget"));

        // 9 Ingots → 1 Block
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, metal.block())
                .define('#', AllTags.commonItemTag("ingots/" + metal.name()))
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_ingot", has(metal.ingot()))
                .save(output, ResourceLocation.fromNamespaceAndPath(CreatingSpace.MODID, metal.name() + "_block"));

        // 1 Block → 9 Ingots
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, metal.ingot(), 9)
                .requires(AllTags.commonItemTag("storage_blocks/" + metal.name()))
                .unlockedBy("has_block", has(metal.block()))
                .save(output, ResourceLocation.fromNamespaceAndPath(CreatingSpace.MODID, metal.name() + "_ingot_from_block"));
    }

    public static void generateMetalPressingRecipes(RecipeOutput output, CSMetalSets.MetalSet metal) {

    }

    // helper for criteria
    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(Item item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    private ResourceLocation createLocation(String recipeName) {
        return ResourceLocation.fromNamespaceAndPath(CreatingSpace.MODID, recipeName);
    }
}

