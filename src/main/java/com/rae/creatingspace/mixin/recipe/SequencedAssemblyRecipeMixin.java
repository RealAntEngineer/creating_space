package com.rae.creatingspace.mixin.recipe;

import com.rae.creatingspace.content.recipes.IMoreNbtConditions;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Objects;

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
    public void addTagBack(ResourceLocation id, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        if (isKeepNbt()) {
            ItemStack advancedItem = cir.getReturnValue();
            CustomData itemData = advancedItem.get(DataComponents.CUSTOM_DATA);
            CustomData toKeepData = input.get(DataComponents.CUSTOM_DATA);
            if (itemData != null && toKeepData != null) {
                CompoundTag itemTag = itemData.copyTag();
                CompoundTag toKeepTag = toKeepData.copyTag();
                for (String key : nbtKeys) {
                    Tag tag = toKeepTag.get(key);
                    if (tag != null) {
                        itemTag.put(key, Objects.requireNonNull(tag));
                    }
                }
                advancedItem.set(DataComponents.CUSTOM_DATA,CustomData.of(itemTag));
                cir.setReturnValue(advancedItem);
            }
        }
    }

    /*@Inject(method = "getRecipe(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/crafting/RecipeType;Ljava/lang/Class;Ljava/util/function/Predicate;)Ljava/util/Optional;", at = @At(value = "RETURN"),remap = false )
    private static <C extends Container, R extends ProcessingRecipe<C>> void debugInfo(Level world, C inv, RecipeType<R> type, Class<R> recipeClass, Predicate<? super R> recipeFilter, CallbackInfoReturnable<Optional<R>> cir) {
        if (CSConfigs.COMMON.additionalLogInfo.get()) {
            try {
                if (world.getGameTime() % 10 == 0) {
                    CreatingSpace.LOGGER.info("getting possible recipe for :");
                    CreatingSpace.LOGGER.info("input : " + inv.getItem(0).serializeNBT());
                    CreatingSpace.LOGGER.info("adding : " + inv.getItem(1).serializeNBT());
                    for (R optional : getRecipes(world, inv.getItem(0), type, recipeClass).filter(recipeFilter).toList()) {
                        CreatingSpace.LOGGER.info("recipe id : " + optional.getId());
                    }
                }
            }
            catch (Exception ignored){
                CreatingSpace.LOGGER.error("crash while trying to print recipe log");
            }
        }
    }*/

}
