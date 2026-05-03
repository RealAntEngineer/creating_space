package com.rae.creatingspace.mixin.recipe;


import com.rae.creatingspace.content.recipes.IMoreNbtConditions;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(RecipeApplier.class)
public class RecipeApplierMixin {

    @Inject(method = "applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;Z)Ljava/util/List;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void copyNbtTagsToOutputs(Level level, ItemStack stackIn, Recipe<?> recipe,
                                             boolean returnProcessingRemainder, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> stacks;

        if (recipe instanceof ProcessingRecipe<?, ?> pr) {
            stacks = new ArrayList<>();
            for (int i = 0; i < stackIn.getCount(); i++) {
                List<ProcessingOutput> outputs =
                        pr instanceof ManualApplicationRecipe mar ? mar.getRollableResults() : pr.getRollableResults();
                for (ItemStack stack : pr.rollResults(outputs, level.random)) {
                    for (ItemStack previouslyRolled : stacks) {
                        if (stack.isEmpty())
                            continue;
                        if (!ItemStack.isSameItemSameComponents(stack, previouslyRolled))
                            continue;
                        int amount = Math.min(previouslyRolled.getMaxStackSize() - previouslyRolled.getCount(),
                                stack.getCount());
                        previouslyRolled.grow(amount);
                        stack.shrink(amount);
                    }

                    if (stack.isEmpty())
                        continue;

                    // Apply NBT copying if recipe supports it
                    if (recipe instanceof IMoreNbtConditions nbtRecipe && nbtRecipe.isKeepNbt()) {
                        copyNbtTags(stackIn, stack, nbtRecipe.getKeepNbt());
                    }

                    stacks.add(stack);
                }
                if (returnProcessingRemainder && stackIn.hasCraftingRemainingItem()) {
                    ItemHelper.addToList(stackIn.getCraftingRemainingItem(), stacks);
                }
            }
        } else {
            ItemStack out = recipe.getResultItem(level.registryAccess())
                    .copy();
            stacks = ItemHelper.multipliedOutput(stackIn, out);

            // Apply NBT copying for non-processing recipes too
            if (recipe instanceof IMoreNbtConditions nbtRecipe && nbtRecipe.isKeepNbt()) {
                for (ItemStack stack : stacks) {
                    copyNbtTags(stackIn, stack, nbtRecipe.getKeepNbt());
                }
            }
        }

        cir.setReturnValue(stacks);
    }

    @Unique
    private static void copyNbtTags(ItemStack input, ItemStack output, List<String> nbtKeys) {
        CustomData outputData = output.get(DataComponents.CUSTOM_DATA);
        CustomData inputData  = input.get(DataComponents.CUSTOM_DATA);

        if (inputData != null) {
            CompoundTag outputTag = outputData != null ? outputData.copyTag() : new CompoundTag();
            CompoundTag inputTag  = inputData.copyTag();

            for (String key : nbtKeys) {
                Tag tag = inputTag.get(key);
                if (tag != null) {
                    outputTag.put(key, tag);
                }
            }

            output.set(DataComponents.CUSTOM_DATA, CustomData.of(outputTag));
        }
    }
}