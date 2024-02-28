package com.rae.creatingspace.recipes;

import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

public class GasExtractionRecipe extends BasinRecipe {
    //TODO build a recipe that takes proximity arguments like the biomes, the tag of the biomes or a block
    public GasExtractionRecipe(ProcessingRecipeParams params) {
        super(RecipeInit.GAS_EXTRACTION, params);
    }
}