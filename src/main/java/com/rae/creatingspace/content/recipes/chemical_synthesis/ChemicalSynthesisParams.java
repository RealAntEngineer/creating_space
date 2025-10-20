package com.rae.creatingspace.content.recipes.chemical_synthesis;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ChemicalSynthesisParams extends ProcessingRecipeParams {
    public static MapCodec<ChemicalSynthesisParams> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
            codec(ChemicalSynthesisParams::new).forGetter(Function.identity()),
                            Ingredient.CODEC.optionalFieldOf("catalyst", Ingredient.EMPTY)
                    .forGetter( r -> r.catalyst)
    ).apply(instance, (params, catalyst) -> {
        params.catalyst = catalyst;
        return params;
    }));
    public static StreamCodec<RegistryFriendlyByteBuf, ChemicalSynthesisParams> STREAM_CODEC = streamCodec(ChemicalSynthesisParams::new);

    Ingredient catalyst;

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer,catalyst);
    }
}
