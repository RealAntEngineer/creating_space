package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllRecipeTypes;

import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;


import static com.rae.creatingspace.CreatingSpace.resource;
import static com.simibubi.create.AllTags.commonItemTag;

@SuppressWarnings("unused")
public class CSPressingRecipeGen extends PressingRecipeGen {

    GeneratedRecipe

            ALUMINUM_SHEET = create(resource("aluminum_sheet"), b -> b
                        .require(commonItemTag("ingots/aluminum"))
                        .output(ItemInit.ALUMINUM_SHEET)),
            NICKEL_SHEET = create(resource( "nickel_sheet"), b -> b.require(commonItemTag("ingots/nickel"))
                    .output(ItemInit.NICKEL_SHEET)),
            COBALT_SHEET = create(resource("cobalt_sheet"), b -> b.require(commonItemTag("ingots/cobalt"))
                .output(ItemInit.COBALT_SHEET)),
            COPRONICKEL_SHEET = create(resource("copronickel_sheet"), b -> b
                    .require(commonItemTag("ingots/copronickel"))
                    .output(resource("copronickel_sheet"))),
            REINFORCED_COPPER_SHEET = create(resource("reinforced_copper_sheet"), b -> b
                    .require(commonItemTag("ingots/reinforced_copper"))
                    .output(resource("reinforced_copper_sheet"))),
            MONEL_SHEET = create(resource("monel_sheet"), b -> b
                    .require(commonItemTag("ingots/monel"))
                    .output(resource("monel_sheet"))),
            INCONEL_SHEET = create(resource("inconel_sheet"), b -> b
                    .require(commonItemTag("ingots/inconel"))
                    .output(resource("inconel_sheet"))),
            HASTELLOY_SHEET = create(resource("hastelloy_sheet"), b -> b
                    .require(commonItemTag("ingots/hastelloy"))
                    .output(resource("hastelloy_sheet")));

    public CSPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.PRESSING;
    }
}
