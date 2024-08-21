package com.rae.creatingspace.datagen.server;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class CSSimpleRecipeProvider extends RecipeProvider {

    public CSSimpleRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        // Example: Generate recipes for a hypothetical item or block
        shapedRecipe(consumer, Items.ICE, "###", "#X#", "###", Items.IRON_INGOT, Items.GLASS, "ice_block");
    }

    // Shaped Recipe
    public void shapedRecipe(Consumer<FinishedRecipe> consumer, Item result, String pattern1, String pattern2, String pattern3, Item material1, Item material2, String name) {
        ShapedRecipeBuilder.shaped(result)
                .pattern(pattern1)
                .pattern(pattern2)
                .pattern(pattern3)
                .define('#', material1)
                .define('X', material2)
                .unlockedBy("has_material", has(material2))
                .save(consumer, modLoc(name + "_shaped"));
    }

    // Shapeless Recipe
    public void shapelessRecipe(Consumer<FinishedRecipe> consumer, Item result, Item material1, Item material2, String name) {
        ShapelessRecipeBuilder.shapeless(result)
                .requires(material1)
                .requires(material2)
                .unlockedBy("has_material", has(material2))
                .save(consumer, modLoc(name + "_shapeless"));
    }

    // Smelting Recipe
    public void smeltingRecipe(Consumer<FinishedRecipe> consumer, Block input, Item result, float experience, int cookingTime, String name) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), result, experience, cookingTime)
                .unlockedBy("has_" + ForgeRegistries.BLOCKS.getKey(input).getPath(), has(input))
                .save(consumer, modLoc(name + "_smelting"));
    }

    // Blasting Recipe
    public void blastingRecipe(Consumer<FinishedRecipe> consumer, Block input, Item result, float experience, int cookingTime, String name) {
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), result, experience, cookingTime)
                .unlockedBy("has_" + ForgeRegistries.BLOCKS.getKey(input).getPath(), has(input))
                .save(consumer, modLoc(name + "_blasting"));
    }

    // Stonecutting Recipe
    public void stonecuttingRecipe(Consumer<FinishedRecipe> consumer, Block input, Item result, String name) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), result)
                .unlockedBy("has_" + ForgeRegistries.BLOCKS.getKey(input).getPath(), has(input))
                .save(consumer, modLoc(name + "_stonecutting"));
    }

    // Smoking Recipe
    public void smokingRecipe(Consumer<FinishedRecipe> consumer, Block input, Item result, float experience, int cookingTime, String name) {
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), result, experience, cookingTime)
                .unlockedBy("has_" + ForgeRegistries.BLOCKS.getKey(input).getPath(), has(input))
                .save(consumer, modLoc(name + "_smoking"));
    }

    // Campfire Cooking Recipe
    public void campfireCookingRecipe(Consumer<FinishedRecipe> consumer, Block input, Item result, float experience, int cookingTime, String name) {
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), result, experience, cookingTime)
                .unlockedBy("has_" + ForgeRegistries.BLOCKS.getKey(input).getPath(), has(input))
                .save(consumer, modLoc(name + "_campfire_cooking"));
    }

    private static ResourceLocation modLoc(String path) {
        return new ResourceLocation(CreatingSpace.MODID, path);
    }
}
