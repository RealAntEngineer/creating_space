package com.rae.creatingspace.mixin.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.content.recipes.IMoreNbtConditions;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SequencedAssemblyRecipeSerializer.class)
public abstract class SequencedAssemblyRecipeSerializerMixin {

    @Inject(method = "codec", at = @At("RETURN"), cancellable = true, remap = false)
    public void addToCodec(CallbackInfoReturnable<MapCodec<SequencedAssemblyRecipe>> cir) {
        MapCodec<SequencedAssemblyRecipe> originalCodec = cir.getReturnValue();

        MapCodec<SequencedAssemblyRecipe> extendedCodec = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        originalCodec.forGetter(recipe -> recipe),
                        Codec.list(Codec.STRING).optionalFieldOf("keepNbt", List.of()).forGetter(recipe -> {
                            if (recipe instanceof IMoreNbtConditions conditions) {
                                return conditions.getKeepNbt();
                            }
                            return List.of();
                        }),
                        Codec.list(Codec.STRING).optionalFieldOf("matchNbt", List.of()).forGetter(recipe -> {
                            if (recipe instanceof IMoreNbtConditions conditions) {
                                return conditions.getMachNbt();
                            }
                            return List.of();
                        })
                ).apply(instance, (recipe, keepNbt, matchNbt) -> {
                    if (recipe instanceof IMoreNbtConditions conditions) {
                        conditions.setKeepNbt(new ArrayList<>(keepNbt));
                        conditions.setMachNbt(new ArrayList<>(matchNbt));
                    }
                    return recipe;
                })
        );

        cir.setReturnValue(extendedCodec);
    }
}