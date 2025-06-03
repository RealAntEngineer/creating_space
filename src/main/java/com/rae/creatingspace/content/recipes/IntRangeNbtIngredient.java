package com.rae.creatingspace.content.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.init.IngredientInit;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class IntRangeNbtIngredient implements ICustomIngredient {
    public static final MapCodec<IntRangeNbtIngredient> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    HolderSetCodec.create(Registries.ITEM, BuiltInRegistries.ITEM.holderByNameCodec(), false).fieldOf("items").forGetter( i -> i.items),
                    CompoundTag.CODEC.fieldOf("range_data").forGetter(i -> i.range_data)
            ).apply(instance, IntRangeNbtIngredient::new)
    );
    public final HolderSet<Item> items;
    ArrayList<String> path;
    int min;
    int max;
    public CompoundTag range_data;//there

    public IntRangeNbtIngredient(HolderSet<Item> items, CompoundTag range_data) {
        this.items = items;
        this.range_data = range_data;
        path = new ArrayList<>(List.of(range_data.getString("path").split("/")));
        min = range_data.contains("min") ? range_data.getInt("min") : Integer.MIN_VALUE;
        max = range_data.contains("max") ? range_data.getInt("max") : Integer.MAX_VALUE;
        //.setFirst(.t);

    }

    //private final NbtPredicate predicate;
    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null)
            return false;
        boolean flag1 = items.contains(input.getItemHolder());
        CustomData data = input.get(DataComponents.CUSTOM_DATA);
        boolean flag2 = matches(data!=null ?data.copyTag():new CompoundTag());
        return flag1 && flag2;
    }

    private boolean matches(CompoundTag shareTag) {
        try {
            CompoundTag tag = shareTag.copy();
            for (String partialPath : path.subList(0, path.size() - 1)) {
                assert tag != null;
                tag = (CompoundTag) tag.get(partialPath);
            }
            assert tag != null;
            int value = tag.getInt(path.getLast());
            if (value >= min || value <= max) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public @NotNull IngredientType<?> getType() {
        return IngredientInit.INT_RANGE.get();
    }

    @Override
    public @NotNull Stream<ItemStack> getItems() {
        //make an iterator to collect the items ?
        ItemStack[] acc = new ItemStack[2 * items.size()];
        int i = 0;
        if (!path.isEmpty()) {
            for (Holder<Item> item : items) {
                ItemStack firstLimit = item.value().getDefaultInstance();
                ItemStack lastLimit = item.value().getDefaultInstance();
                CompoundTag minTag = new CompoundTag();
                CompoundTag maxTag = new CompoundTag();
                minTag.putInt(path.get(path.size()-1), min);
                maxTag.putInt(path.get(path.size()-1), max);
                if (path.size() > 1) {
                    List<String> shallowCopy = path.subList(1, path.size());
                    Collections.reverse(shallowCopy);
                    for (String partialPath : shallowCopy) {
                        CompoundTag tempMin = new CompoundTag();
                        CompoundTag tempMax = new CompoundTag();
                        tempMin.put(partialPath, minTag.copy());
                        tempMax.put(partialPath, maxTag.copy());
                        minTag = tempMin.copy();
                        maxTag = tempMax.copy();
                    }
                }
                firstLimit.set(DataComponents.CUSTOM_DATA,CustomData.of(minTag));
                lastLimit.set(DataComponents.CUSTOM_DATA,CustomData.of(maxTag));
                acc[i] = firstLimit;
                acc[i + 1] = lastLimit;
                i++;
            }
        }
        return Arrays.stream(acc);
    }



}
