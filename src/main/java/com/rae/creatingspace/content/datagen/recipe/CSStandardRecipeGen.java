package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CSStandardRecipeGen extends RecipeProvider {
    final List<CreateRecipeProvider.GeneratedRecipe> all = new ArrayList<>();
    private String path;
    private String suffix;

    public CSStandardRecipeGen(PackOutput output, CompletableFuture
            <HolderLookup.Provider> helper) {
        super(output, helper);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        Marker RESOURCES = enterFolder("resources");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.ALUMINUM_NUGGET.get(), 9)
                .requires(AllTags.commonItemTag("ingots/aluminum"))
                .unlockedBy("has_aluminum_ingot", has(ItemInit.ALUMINUM_INGOT)).save(recipeOutput, createSimpleLocation("aluminum_nugget"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.ALUMINUM_INGOT.get(), 9)
                .requires(AllTags.commonItemTag("storage_blocks/aluminum"))
                .unlockedBy("has_aluminum_ingot", has(ItemInit.ALUMINUM_INGOT)).save(recipeOutput, withSuffix("_from_block").createLocation("aluminum_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemInit.ALUMINUM_INGOT.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("nuggets/aluminum"))
                .unlockedBy("has_aluminum_ingot", has(ItemInit.ALUMINUM_INGOT))
                .save(recipeOutput, withSuffix("_from_nuggets").createLocation("aluminum_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.ALUMINUM_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("ingots/aluminum"))
                .unlockedBy("has_aluminum_ingot", has(ItemInit.ALUMINUM_INGOT))
                .save(recipeOutput, createSimpleLocation("aluminum_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.NICKEL_NUGGET.get(), 9)
                .requires(AllTags.commonItemTag("ingots/nickel"))
                .unlockedBy("has_nickel_ingot", has(ItemInit.NICKEL_INGOT)).save(recipeOutput, createSimpleLocation("nickel_nugget"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.NICKEL_INGOT.get(), 9)
                .requires(AllTags.commonItemTag("storage_blocks/nickel"))
                .unlockedBy("has_nickel_ingot", has(ItemInit.NICKEL_INGOT)).save(recipeOutput, withSuffix("_from_block").createLocation("nickel_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemInit.NICKEL_INGOT.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("nuggets/nickel"))
                .unlockedBy("has_nickel_ingot", has(ItemInit.NICKEL_INGOT))
                .save(recipeOutput, withSuffix("_from_nuggets").createLocation("nickel_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.NICKEL_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("ingots/nickel"))
                .unlockedBy("has_nickel_ingot", has(ItemInit.NICKEL_INGOT))
                .save(recipeOutput, createSimpleLocation("nickel_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.COBALT_NUGGET.get(), 9)
                .requires(AllTags.commonItemTag("ingots/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT)).save(recipeOutput, createSimpleLocation("cobalt_nugget"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.COBALT_INGOT.get(), 9)
                .requires(AllTags.commonItemTag("storage_blocks/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT)).save(recipeOutput, withSuffix("_from_block").createLocation("cobalt_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemInit.COBALT_INGOT.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("nuggets/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT))
                .save(recipeOutput, withSuffix("_from_nuggets").createLocation("cobalt_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.COBALT_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("ingots/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT))
                .save(recipeOutput, createSimpleLocation("cobalt_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.COBALT_NUGGET.get(), 9)
                .requires(AllTags.commonItemTag("ingots/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT)).save(recipeOutput, createSimpleLocation("cobalt_nugget"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.COBALT_INGOT.get(), 9)
                .requires(AllTags.commonItemTag("storage_blocks/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT)).save(recipeOutput, withSuffix("_from_block").createLocation("cobalt_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemInit.COBALT_INGOT.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("nuggets/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT))
                .save(recipeOutput, withSuffix("_from_nuggets").createLocation("cobalt_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.COBALT_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("ingots/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT))
                .save(recipeOutput, createSimpleLocation("cobalt_block"));
    }

    CSStandardRecipeGen withSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    String currentFolder = "";

    Marker enterFolder(String folder) {
        currentFolder = folder;
        return new Marker();
    }

    protected static class Marker {
    }
    private ResourceLocation createSimpleLocation(String recipeName){
        return ResourceLocation.fromNamespaceAndPath(CreatingSpace.MODID,currentFolder + "/" + recipeName);
    }

    private ResourceLocation createLocation(String recipeName) {
        return ResourceLocation.fromNamespaceAndPath(CreatingSpace.MODID, currentFolder + "/" + recipeName + suffix);
    }
}