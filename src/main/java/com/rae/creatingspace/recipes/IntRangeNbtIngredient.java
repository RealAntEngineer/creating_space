package com.rae.creatingspace.recipes;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IntRangeNbtIngredient extends AbstractIngredient {
    public final Set<Item> items;
    ArrayList<String> path = null;
    int min;
    int max;
    public CompoundTag range_data;//there

    public IntRangeNbtIngredient(Set<Item> items, CompoundTag range_data) {
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
        boolean flag1 = items.contains(input.getItem());
        boolean flag2 = matches(input.getShareTag());
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
            int value = tag.getInt(path.get(path.size() - 1));
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
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        return null;
    }

    public static class Serializer implements IIngredientSerializer<IntRangeNbtIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public IntRangeNbtIngredient parse(JsonObject json) {
            // parse items
            Set<Item> items;
            if (json.has("item"))
                items = Set.of(CraftingHelper.getItem(GsonHelper.getAsString(json, "item"), true));
            else if (json.has("items")) {
                ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
                JsonArray itemArray = GsonHelper.getAsJsonArray(json, "items");
                for (int i = 0; i < itemArray.size(); i++) {
                    builder.add(CraftingHelper.getItem(GsonHelper.convertToString(itemArray.get(i), "items[" + i + ']'), true));
                }
                items = builder.build();
            } else
                throw new JsonSyntaxException("Must set either 'item' or 'items'");

            // parse NBT
            if (!json.has("range_data"))
                throw new JsonSyntaxException("Missing range_data, expected to find a JsonObject");
            CompoundTag nbt = CraftingHelper.getNBT(json.get("range_data"));

            return new IntRangeNbtIngredient(items, nbt);
        }

        @Override
        public IntRangeNbtIngredient parse(FriendlyByteBuf buffer) {
            Set<Item> items = Stream.generate(() -> buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS)).limit(buffer.readVarInt()).collect(Collectors.toSet());
            CompoundTag nbt = buffer.readNbt();
            return new IntRangeNbtIngredient(items, Objects.requireNonNull(nbt));
        }

        @Override
        public void write(FriendlyByteBuf buffer, IntRangeNbtIngredient ingredient) {
            buffer.writeVarInt(ingredient.items.size());
            for (Item item : ingredient.items)
                buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
            buffer.writeNbt(ingredient.range_data);
        }

    }

}
