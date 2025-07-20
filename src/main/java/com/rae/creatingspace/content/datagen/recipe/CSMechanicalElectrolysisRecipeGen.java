package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.recipes.electrolysis.MechanicalElectrolysisRecipe;
import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CSMechanicalElectrolysisRecipeGen extends ProcessingRecipeGen<
        ProcessingRecipeParams,
        MechanicalElectrolysisRecipe,
        MechanicalElectrolysisRecipe.Builder<MechanicalElectrolysisRecipe>
        > {

    public CSMechanicalElectrolysisRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, CreatingSpace.MODID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return RecipeInit.MECHANICAL_ELECTROLYSIS;
    }

    @Override
    protected MechanicalElectrolysisRecipe.Builder<MechanicalElectrolysisRecipe> getBuilder(ResourceLocation id) {
        return new MechanicalElectrolysisRecipe.Builder<>(MechanicalElectrolysisRecipe::new, id);
    }

    {
        GeneratedRecipe SPLIT_WATER = create("water_electrolysis", b -> b
                .require(Fluids.WATER, 100)
                .output(FluidInit.LIQUID_HYDROGEN.get(), 160)
                .output(FluidInit.LIQUID_OXYGEN.get(), 80)
                .requiresHeat(HeatCondition.NONE)
                .duration(200)
        );
    }
}
