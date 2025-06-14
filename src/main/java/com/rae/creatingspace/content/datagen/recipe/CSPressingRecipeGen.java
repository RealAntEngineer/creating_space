package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.content.datagen.CSRecipeProvider;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.rae.creatingspace.CreatingSpace.resource;

@SuppressWarnings("unused")
public class CSPressingRecipeGen extends ProcessingRecipeGen {

    GeneratedRecipe

            ALUMINUM_SHEET = create(resource("aluminum_sheet"), b -> b
                        .require(AllTags.commonItemTag("ingots/aluminum"))
                        .output(ItemInit.ALUMINUM_SHEET)),
            NICKEL_SHEET = create(resource( "nickel_sheet"), b -> b.require(AllTags.commonItemTag("ingots/nickel"))
                    .output(ItemInit.NICKEL_SHEET)),
            COBALT_SHEET = create(resource("cobalt_sheet"), b -> b.require(AllTags.commonItemTag("ingots/cobalt"))
                .output(ItemInit.COBALT_SHEET)),
            COPRONICKEL_SHEET = create(resource("copronickel_sheet"), b -> b
                    .require(AllTags.commonItemTag("ingots/copronickel"))
                    .output(resource("copronickel_sheet"))),
            REINFORCED_COPPER_SHEET = create(resource("reinforced_copper_sheet"), b -> b
                    .require(AllTags.commonItemTag("ingots/reinforced_copper"))
                    .output(resource("reinforced_copper_sheet"))),
            MONEL_SHEET = create(resource("monel_sheet"), b -> b
                    .require(AllTags.commonItemTag("ingots/monel"))
                    .output(resource("monel_sheet"))),
            INCONEL_SHEET = create(resource("inconel_sheet"), b -> b
                    .require(AllTags.commonItemTag("ingots/inconel"))
                    .output(resource("inconel_sheet"))),
            HASTELLOY_SHEET = create(resource("hastelloy_sheet"), b -> b
                    .require(AllTags.commonItemTag("ingots/hastelloy"))
                    .output(resource("hastelloy_sheet")));

    public CSPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.PRESSING;
    }
}
