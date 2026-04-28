package com.rae.creatingspace.mixin.recipe;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Mixin(DataComponentIngredient.class)
public class DataComponentIngredientMixin {

    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void lessStrictTest(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        DataComponentIngredient ingredient = (DataComponentIngredient) (Object) this;

        // Only modify behavior when not strict
        if (!ingredient.isStrict()) {
            // Check if item matches
            if (!ingredient.items().contains(stack.getItemHolder())) {
                cir.setReturnValue(false);
                return;
            }

            // Check if all required components are present and contained
            DataComponentMap   stackComponents = stack.getComponents();
            DataComponentPatch requiredPatch   = ingredient.components().asPatch();

            // Iterate through the patch to check each required component
            for (Map.Entry<DataComponentType<?>, Optional<?>> entry : requiredPatch.entrySet()) {
                if (entry.getValue().isPresent()) {
                    DataComponentType<?> type = entry.getKey();
                    Object requiredValue = entry.getValue().get();
                    Object stackValue = stackComponents.get(type);

                    // Component must exist in stack
                    if (stackValue == null) {
                        cir.setReturnValue(false);
                        return;
                    }

                    // Check containment (for NBT/complex data)
                    if (!isContainedIn(requiredValue, stackValue)) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }

            cir.setReturnValue(true);
        }
    }

    private static boolean isContainedIn(Object required, Object actual) {
        if (required == null) return true;
        if (actual == null) return false;

        // For NbtComponent or similar nested data structures
        if (required instanceof CustomData requiredNbt && actual instanceof CustomData actualNbt) {
            return isNbtContainedIn(requiredNbt.copyTag(), actualNbt.copyTag());
        }

        // Default to equality for simple types
        return Objects.equals(required, actual);
    }

    private static boolean isNbtContainedIn(Tag required, Tag actual) {
        if (required instanceof CompoundTag requiredCompound && actual instanceof CompoundTag actualCompound) {
            // Check all required keys are present with matching values
            for (String key : requiredCompound.getAllKeys()) {
                Tag requiredValue = requiredCompound.get(key);
                Tag actualValue   = actualCompound.get(key);

                if (!isNbtContainedIn(requiredValue, actualValue)) {
                    return false;
                }
            }
            return true;
        } else if (required instanceof NumericTag requiredNum && actual instanceof NumericTag actualNum){
            return requiredNum.getAsFloat() == actualNum.getAsFloat();
        }

        // For other tag types, use equality
        return Objects.equals(required, actual);
    }
}
