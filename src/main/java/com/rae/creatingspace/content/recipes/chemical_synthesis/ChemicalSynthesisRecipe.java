package com.rae.creatingspace.content.recipes.chemical_synthesis;

import com.google.gson.JsonObject;
import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public class ChemicalSynthesisRecipe extends BasinRecipe {
    Ingredient catalyst;
    public ChemicalSynthesisRecipe(ProcessingRecipeParams params) {
        super(RecipeInit.CHEMICAL_SYNTHESIS, params);
    }

    @Override
    public void readAdditional(JsonObject json) {
        super.readAdditional(json);
        if (json.get("catalyst") == null)
            catalyst = Ingredient.EMPTY;
        else
            catalyst = Ingredient.fromJson(json.get("catalyst"));
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        super.readAdditional(buffer);
        catalyst = Ingredient.fromNetwork(buffer);
    }

    @Override
    public void writeAdditional(JsonObject json) {
        super.writeAdditional(json);
        if (catalyst != null)
            json.add("catalyst", catalyst.toJson());
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        super.writeAdditional(buffer);
        if (catalyst != null)
            catalyst.toNetwork(buffer);
    }
}