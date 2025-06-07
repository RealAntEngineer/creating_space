package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.data.recipe.PressingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class CSPressingRecipeGen extends PressingRecipeGen {
    public CSPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);

        //GeneratedRecipe

        //ALUMINUM = create("aluminum_ingot", b -> b.require(AllTags.commonItemTag("c:ingots/aluminum"))
        //        .output(ItemInit.ALUMINUM_SHEET.get()));
        //NICKEL = create("aluminum_ingot", b -> b.require(AllTags.commonItemTag("c:ingots/nickel"))
        //        .output(ItemInit.NICKEL_SHEET.get()));

    }
}
