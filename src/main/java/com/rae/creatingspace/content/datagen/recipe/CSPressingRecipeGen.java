package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.recipe.PressingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CSPressingRecipeGen extends PressingRecipeGen {
    public CSPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);

        create(createLocation("aluminum_sheet"), b -> b.require(AllTags.commonItemTag("ingots/aluminum"))
                .output(ItemInit.ALUMINUM_SHEET.get()));
        create(createLocation( "nickel_sheet"), b -> b.require(AllTags.commonItemTag("ingots/nickel"))
                .output(ItemInit.NICKEL_SHEET.get()));
        create(createLocation("cobalt_sheet"), b -> b.require(AllTags.commonItemTag("ingots/cobalt"))
                .output(ItemInit.COBALT_SHEET.get()));
    }
    private ResourceLocation createLocation(String recipeName) {
        return ResourceLocation.fromNamespaceAndPath(CreatingSpace.MODID, recipeName);
    }
}
