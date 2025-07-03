package com.rae.creatingspace.content.datagen;

import com.rae.creatingspace.content.datagen.recipe.CSMetalRecipeHelper;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllTags;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.List;

public class CSMetalSets {

    public static final MetalSet ALUMINUM = new MetalSet(
            "aluminum",
            true,
            ItemInit.ALUMINUM_NUGGET.get(),
            ItemInit.ALUMINUM_INGOT.get(),
            BlockInit.ALUMINUM_BLOCK.asItem(),
            ItemInit.RAW_ALUMINUM.get(),
            BlockInit.MOON_ALUMINUM_ORE.asItem(),
            null
            );

    public static final MetalSet COBALT = new MetalSet(
            "cobalt",
            true,
            ItemInit.COBALT_NUGGET.get(),
            ItemInit.COBALT_INGOT.get(),
            BlockInit.COBALT_BLOCK.asItem(),
            ItemInit.RAW_COBALT.get(),
            BlockInit.MOON_COBALT_ORE.asItem(),
            null
            );

    public static final MetalSet NICKEL = new MetalSet(
            "nickel",
            true,
            ItemInit.NICKEL_NUGGET.get(),
            ItemInit.NICKEL_INGOT.get(),
            BlockInit.NICKEL_BLOCK.asItem(),
            ItemInit.RAW_NICKEL.get(),
            BlockInit.NICKEL_ORE.asItem(),
            BlockInit.MOON_NICKEL_ORE.asItem()
            );

    public static final List<String> ALL = List.of("aluminum","cobalt", "nickel");

    public record MetalSet(
            String name,
            boolean generateSmelting,
            Item nugget,
            Item ingot,
            Item block,
            @Nullable Item rawOre,
            @Nullable Item ore,
            @Nullable Item deepslateOre
    ){}

    protected void buildRecipes(RecipeOutput recipeOutput) {
    }

}
