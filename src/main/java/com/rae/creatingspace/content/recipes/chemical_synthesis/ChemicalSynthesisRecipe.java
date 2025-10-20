package com.rae.creatingspace.content.recipes.chemical_synthesis;

import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;


public class ChemicalSynthesisRecipe extends BasinRecipe {
    final Ingredient catalyst;
    public ChemicalSynthesisRecipe(ProcessingRecipeParams params) {
        super(RecipeInit.CHEMICAL_SYNTHESIS, params);
        if (params instanceof ChemicalSynthesisParams chemicalSynthesisParams) {
            catalyst = chemicalSynthesisParams.catalyst;
        } else {
            catalyst = Ingredient.EMPTY;
        }
    }

    /*@FunctionalInterface
    public interface Factory<R extends BasinRecipe> extends StandardProcessingRecipe.Factory<R> {
        //@NotNull R create(@NotNull ChemicalSynthesisParams params);
    }*/

    public static class Builder extends StandardProcessingRecipe.Builder<ChemicalSynthesisRecipe> {
        public Builder(ResourceLocation recipeId) {
            super((params) -> new ChemicalSynthesisRecipe((ChemicalSynthesisParams) params), recipeId);
        }

        @Override
        protected @NotNull ChemicalSynthesisParams createParams() {
            return new ChemicalSynthesisParams();
        }

        @Override
        public @NotNull Builder self() {
            return this;
        }

        public ChemicalSynthesisRecipe.Builder toolNotConsumed() {
            //params.catalyst = true;
            return this;
        }
    }
}