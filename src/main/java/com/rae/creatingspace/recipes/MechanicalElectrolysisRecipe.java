package com.rae.creatingspace.recipes;

import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

public class MechanicalElectrolysisRecipe extends BasinRecipe {
    public MechanicalElectrolysisRecipe(ProcessingRecipeParams params) {
        super(RecipeInit.MECHANICAL_ELECTROLYSIS, params);
    }
}