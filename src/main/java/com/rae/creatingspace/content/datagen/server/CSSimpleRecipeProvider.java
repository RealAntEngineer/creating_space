package com.rae.creatingspace.content.datagen.server;

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
        super(generator.getPackOutput());
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // Example: Generate recipes for a hypothetical item or block
    }



    private static ResourceLocation modLoc(String path) {
        return new ResourceLocation(CreatingSpace.MODID, path);
    }

}
