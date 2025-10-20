package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.recipes.chemical_synthesis.ChemicalSynthesisRecipe;
import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CSChemicalSynthesisRecipeGen extends StandardProcessingRecipeGen<ChemicalSynthesisRecipe> {

    public CSChemicalSynthesisRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, CreatingSpace.MODID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return RecipeInit.CHEMICAL_SYNTHESIS;
    }

    @Override
    protected StandardProcessingRecipe.Builder<ChemicalSynthesisRecipe> getBuilder(ResourceLocation id) {
        return new ChemicalSynthesisRecipe.Builder(id);
    }

    {
        GeneratedRecipe METHANE = create("methane_synthesis", b -> b
                .require(FluidInit.LIQUID_CO2.get(), 100)
                .require(FluidInit.LIQUID_HYDROGEN.get(), 286)
                .output(FluidInit.LIQUID_METHANE.get(), 80)
                .output(Fluids.WATER, 90)
                .requiresHeat(HeatCondition.HEATED)
                .duration(200)
        );
    }
}
