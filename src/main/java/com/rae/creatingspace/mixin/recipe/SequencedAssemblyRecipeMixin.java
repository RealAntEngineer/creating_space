package com.rae.creatingspace.mixin.recipe;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.recipes.IMoreNbtConditions;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe.getRecipes;

@Mixin(value = SequencedAssemblyRecipe.class)
public class SequencedAssemblyRecipeMixin implements IMoreNbtConditions {
    @Unique
    public ArrayList<String> nbtKeys = new ArrayList<>();
    @Unique
    public ArrayList<String> matchNbtList = new ArrayList<>();

    public void setKeepNbt(ArrayList<String> nbtKeys) {
        this.nbtKeys = nbtKeys;
    }

    @Override
    public ArrayList<String> getKeepNbt() {
        return nbtKeys;
    }

    @Override
    public void setMachNbt(ArrayList<String> machNbtList) {
        matchNbtList = machNbtList;
    }

    @Override
    public ArrayList<String> getMachNbt() {
        return matchNbtList;
    }

    @Override
    public boolean isKeepNbt() {
        return !nbtKeys.isEmpty();
    }

    @Override
    public boolean isMachNbt() {
        return !matchNbtList.isEmpty();
    }
    //issue will using either creatingspace:range_int or forge:intersection and a transitional item different that the first ingredient
    //possibly related to getRecipes

    /*@ModifyVariable(method = "advance", at = @At(value = "LOAD", ordinal = 0),name = "itemTag")
    private CompoundTag addNbt(CompoundTag value){
        return value;
    }*/
    @Inject(method = "advance", at = @At(value = "RETURN"), cancellable = true, remap = false)
    public void addTagBack(ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        if (isKeepNbt()) {
            ItemStack advancedItem = cir.getReturnValue();
            CompoundTag itemTag = advancedItem.getOrCreateTag();
            CompoundTag toKeepTag = input.getOrCreateTag();
            for (String key : nbtKeys) {
                Tag tag = toKeepTag.get(key);
                if (tag != null) {
                    itemTag.put(key, Objects.requireNonNull(tag));
                }
            }
            advancedItem.setTag(itemTag);
            cir.setReturnValue(advancedItem);
        }
    }
    @Inject(method = "getRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/crafting/RecipeType;Ljava/lang/Class;Ljava/util/function/Predicate;)Ljava/util/Optional;", at = @At(value = "RETURN"),remap = false )
    private static <C extends Container, R extends ProcessingRecipe<C>> void debugInfo(Level world, C inv, RecipeType<R> type, Class<R> recipeClass, Predicate<? super R> recipeFilter, CallbackInfoReturnable<Optional<R>> cir) {
        if (CSConfigs.COMMON.additionalLogInfo.get()) {
            if (world.getGameTime() % 10 == 0) {
                CreatingSpace.LOGGER.info("getting possible recipe for :");
                CreatingSpace.LOGGER.info("input : " + inv.getItem(0).serializeNBT());
                CreatingSpace.LOGGER.info("adding : " + inv.getItem(1).serializeNBT());
                for (R optional : getRecipes(world, inv.getItem(0), type, recipeClass).filter(recipeFilter).toList()) {
                    CreatingSpace.LOGGER.info("recipe id : " + optional.getId());
                }
            }
        }
    }

}
