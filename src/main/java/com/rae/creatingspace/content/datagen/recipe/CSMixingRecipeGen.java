package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static com.rae.creatingspace.CreatingSpace.resource;
import static com.simibubi.create.AllTags.commonItemTag;

@SuppressWarnings("unused")
public class CSMixingRecipeGen extends MixingRecipeGen {

    GeneratedRecipe

            COPRONICKEL_INGOT = create("copronickel_ingot", b -> b
                    .require(Items.COPPER_INGOT)
                    .require(Items.COPPER_INGOT)
                    .require(commonItemTag("ingots/nickel"))
                    .output(BuiltInRegistries.ITEM.get(resource("copronickel_ingot")), 2)
                    .requiresHeat(HeatCondition.HEATED)),

            HASTELLOY_INGOT = create("hastelloy_ingot", b -> b
                    .require(Items.COPPER_INGOT)
                    .require(Items.IRON_INGOT)
                    .require(commonItemTag("ingots/nickel"))
                    .require(commonItemTag("ingots/nickel"))
                    .require(commonItemTag("ingots/cobalt"))
                    .require(commonItemTag("ingots/cobalt"))
                    .output(BuiltInRegistries.ITEM.get(resource("copronickel_ingot")), 1)
                    .requiresHeat(HeatCondition.SUPERHEATED)),

            INCONEL_INGOT = create("inconel_ingot", b -> b
                    .require(Items.IRON_INGOT)
                    .require(commonItemTag("ingots/nickel"))
                    .require(commonItemTag("ingots/nickel"))
                    .output(BuiltInRegistries.ITEM.get(resource("inconel_ingot")), 1)
                    .requiresHeat(HeatCondition.SUPERHEATED)),

            MONEL_INGOT = create("monel_ingot", b -> b
                    .require(Items.COPPER_INGOT)
                    .require(commonItemTag("ingots/nickel"))
                    .require(commonItemTag("ingots/nickel"))
                    .output(BuiltInRegistries.ITEM.get(resource("monel_ingot")), 1)
                    .requiresHeat(HeatCondition.SUPERHEATED)),

            REINFORCED_COPPER_INGOT = create("inconel_ingot", b -> b
                    .require(Items.IRON_INGOT)
                    .require(Items.COPPER_INGOT)
                    .require(Items.COPPER_INGOT)
                    .output(BuiltInRegistries.ITEM.get(resource("reinforced_copper_ingot")), 1)
                    .requiresHeat(HeatCondition.HEATED));

    public CSMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, CreatingSpace.MODID);
    }


}
