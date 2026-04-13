package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.WashingRecipeGen;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class CSWashingRecipeGen extends WashingRecipeGen {
    public CSWashingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, CreatingSpace.MODID);
    }

        GeneratedRecipe MOON_REGOLITH = create(CreatingSpace.MODID, BlockInit.MOON_REGOLITH::asItem, b -> b
                .output(.125f, Items.GOLD_NUGGET, 2)
                .output(.08f, ItemInit.NICKEL_NUGGET, 2)
                .output(.04f, ItemInit.ALUMINUM_NUGGET)
                .output(.04f, ItemInit.COBALT_NUGGET));

        GeneratedRecipe MARS_REGOLITH = create(CreatingSpace.MODID, BlockInit.MARS_REGOLITH::asItem, b -> b
                .output(.125f, ItemInit.COBALT_NUGGET, 3)
                .output(.08f, ItemInit.NICKEL_NUGGET, 3)
                .output(.04f, ItemInit.ALUMINUM_NUGGET, 3)),

            CRUSHED_ALUMINUM = crushedOreNoSecondary(AllItems.CRUSHED_BAUXITE, ItemInit.ALUMINUM_NUGGET::get),
            CRUSHED_NICKEL = crushedOreNoSecondary(AllItems.CRUSHED_NICKEL, ItemInit.NICKEL_NUGGET::get),
            CRUSHED_COBALT = crushedOreNoSecondary(ItemInit.CRUSHED_COBALT_ORE, ItemInit.COBALT_NUGGET::get);


    public GeneratedRecipe crushedOreNoSecondary(ItemEntry<? extends Item> crushed, Supplier<ItemLike> nugget) {
        return create(CreatingSpace.MODID, crushed::get, b -> b.output(nugget.get(), 9));
    }

}
