package com.rae.creatingspace.content.datagen.recipe;

import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static com.rae.creatingspace.CreatingSpace.MODID;

@SuppressWarnings("unused")
public class CSSequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {
    public CSSequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }

    GeneratedRecipe

    BASIC_SPACESUIT_FABRIC = create("basic_spacesuit_fabric",
            b -> b.require(AllItems.COPPER_SHEET)
                    .transitionTo(AllItems.COPPER_SHEET.get())
                    .addOutput(ItemInit.BASIC_SPACESUIT_FABRIC.get(),120)
                    .addOutput(Items.GOLD_NUGGET, 8)
                    .loops(1)
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.GOLD_NUGGET))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.RED_WOOL))
                    .addStep(PressingRecipe::new, rb -> rb)),

    ADVANCED_SPACESUIT_FABRIC = create("advanced_spacesuit_fabric",
            b -> b.require(Items.NETHERITE_INGOT)
                    .transitionTo(Items.NETHERITE_INGOT)
                    .addOutput(ItemInit.ADVANCED_SPACESUIT_FABRIC.get(),120)
                    .addOutput(ItemInit.COBALT_INGOT.get(), 8)
                    .loops(1)
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(ItemInit.COBALT_NUGGET.get()))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.WHITE_WOOL))
                    .addStep(PressingRecipe::new, rb -> rb));
}
