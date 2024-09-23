package com.rae.creatingspace.content.recipes.electrolysis;

import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MechanicalElectrolysisRecipe extends BasinRecipe {
    public MechanicalElectrolysisRecipe(ProcessingRecipeParams params) {
        super(RecipeInit.MECHANICAL_ELECTROLYSIS, params);
    }

    @Override
    public boolean matches(SmartInventory inv, @NotNull Level worldIn) {
        return super.matches(inv, worldIn);
    }
}