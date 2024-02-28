package com.rae.creatingspace.recipes;

import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

public class ChemicalSynthesisRecipe extends BasinRecipe {
    public ChemicalSynthesisRecipe(ProcessingRecipeParams params) {
        super(RecipeInit.CHEMICAL_SYNTHESIS, params);
    }
}