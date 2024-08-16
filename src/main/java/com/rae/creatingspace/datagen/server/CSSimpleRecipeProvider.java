package com.rae.creatingspace.datagen.server;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class CSSimpleRecipeProvider extends RecipeProvider {

    public CSSimpleRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // Add your recipes here
        // Example: Simple crafting recipe
        Object MyModItems = CreatingSpace.MODID;
    }

    private void simpleCraftingRecipe(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike... ingredients) {
        ShapedRecipeBuilder.shaped(result)
                .define('X', ingredients[0])
                .define('Y', ingredients[1])
                .pattern(" X ")
                .pattern("XYX")
                .pattern(" Y ")
                .unlockedBy("has_item", has(ingredients[0]))
                .save(consumer);
    }


    private void simpleSmeltingRecipe(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike input, float experience, int cookingTime) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), result, experience, cookingTime)
                .unlockedBy("has_item", has(input))
                .save(consumer, new ResourceLocation(CreatingSpace.MODID, "smelting_" + ForgeRegistries.ITEMS.getKey(result.asItem()).getPath()));
    }
}

