package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.recipes.air_liquefying.AirLiquefyingRecipe;
import com.rae.creatingspace.content.recipes.air_liquefying.AirLiquefyingRecipeParam;
import com.rae.creatingspace.init.RecipeInit;
import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class CSAirLiquefyingRecipeGen
        extends ProcessingRecipeGen<
        AirLiquefyingRecipeParam,
        AirLiquefyingRecipe,
        AirLiquefyingRecipe.Builder<AirLiquefyingRecipe>
        > {

    public CSAirLiquefyingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, CreatingSpace.MODID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return RecipeInit.AIR_LIQUEFYING;
    }

    @Override
    protected AirLiquefyingRecipe.Builder getBuilder(ResourceLocation id) {
        return new AirLiquefyingRecipe.Builder<AirLiquefyingRecipe>(AirLiquefyingRecipe::new, id);
    }

    {   // instance‐initializer: declare your recipes here
        GeneratedRecipe

        LIQUID_O2 = create("o2_liquifying", b -> b
                .blockInFront(ResourceLocation.parse("minecraft:air"))
                .dimension(ResourceLocation.parse("minecraft:overworld"))
                .output(FluidInit.LIQUID_OXYGEN.get(), 100)
                .duration(200)
        ),

        LIQUID_CO2 = create("co2_liquifying", b -> b
                .blockInFront(ResourceLocation.parse("minecraft:campfire"))
                .dimension(ResourceLocation.parse("minecraft:overworld"))
                .output(FluidInit.LIQUID_CO2.get(), 100)
                .duration(200)
        );
        // …more air‐liquefying recipes…
    }
}
