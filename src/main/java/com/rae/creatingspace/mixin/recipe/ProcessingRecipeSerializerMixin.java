package com.rae.creatingspace.mixin.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.content.recipes.IMoreNbtConditions;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.*;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ProcessingRecipeSerializer.class)
public abstract class ProcessingRecipeSerializerMixin {

    @Inject(method = "codec(Lcom/simibubi/create/AllRecipeTypes;)Lcom/mojang/serialization/MapCodec;", at = @At("RETURN"), remap = false, cancellable = true)
    private static <T extends ProcessingRecipe<?>> void  readKeepNbtJson(AllRecipeTypes recipeTypes, CallbackInfoReturnable<MapCodec<T>> cir) {
        //TODO add a keepNbt to the expected CODEC ? orr completely replacing it if that's not possible.
        cir.setReturnValue(RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.list(Codec.STRING).optionalFieldOf("keepNbt",List.of()).forGetter(
                        i -> {
                            if (i instanceof IMoreNbtConditions moreNbtConditions) {
                                return moreNbtConditions.getKeepNbt();
                            }
                            return List.of();
                        }
                ),
                Codec.list(Codec.STRING).optionalFieldOf("matchNbt",List.of()).forGetter(
                        i -> {
                            if (i instanceof IMoreNbtConditions moreNbtConditions) {
                                return moreNbtConditions.getMachNbt();
                            }
                            return List.of();
                        }
                ),
                Codec.either(Ingredient.CODEC, FluidIngredient.CODEC).listOf().fieldOf("ingredients").forGetter(i -> {
                    List<Either<Ingredient, FluidIngredient>> list = new ArrayList<>();
                    i.getIngredients().forEach(o -> list.add(Either.left(o)));
                    i.getFluidIngredients().forEach(o -> list.add(Either.right(o)));
                    return list;
                }),
                Codec.either(FluidStack.CODEC, ProcessingOutput.CODEC).listOf().fieldOf("results").forGetter(i -> {
                    List<Either<FluidStack, ProcessingOutput>> list = new ArrayList<>();
                    i.getFluidResults().forEach(o -> list.add(Either.left(o)));
                    i.getRollableResults().forEach(o -> list.add(Either.right(o)));
                    return list;
                }), // Fluid and item outputs both using "id" as key, try deserializing as fluid first
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("processing_time", 0).forGetter(T::getProcessingDuration),
                HeatCondition.CODEC.optionalFieldOf("heat_requirement", HeatCondition.NONE).forGetter(T::getRequiredHeat)
        ).apply(instance, (keepNbt, matchNbt,ingredients, results, processingTime, heatRequirement) -> {
            if (!(recipeTypes.serializerSupplier.get() instanceof ProcessingRecipeSerializer processingRecipeSerializer))
                throw new RuntimeException("Not a processing recipe serializer " + recipeTypes.serializerSupplier.get());

            ProcessingRecipeBuilder<T> builder = new ProcessingRecipeBuilder<T>(processingRecipeSerializer.getFactory(), recipeTypes.id);

            NonNullList<Ingredient> ingredientList = NonNullList.create();
            NonNullList<FluidIngredient> fluidIngredientList = NonNullList.create();

            NonNullList<ProcessingOutput> processingOutputList = NonNullList.create();
            NonNullList<FluidStack> fluidStackOutputList = NonNullList.create();

            for (Either<Ingredient, FluidIngredient> either : ingredients) {
                either.left().ifPresent(ingredientList::add);
                either.right().ifPresent(fluidIngredientList::add);
            }

            for (Either<FluidStack, ProcessingOutput> either : results) {
                either.left().ifPresent(fluidStackOutputList::add);
                either.right().ifPresent(processingOutputList::add);
            }

            builder.withItemIngredients(ingredientList)
                    .withItemOutputs(processingOutputList)
                    .withFluidIngredients(fluidIngredientList)
                    .withFluidOutputs(fluidStackOutputList)
                    .duration(processingTime)
                    .requiresHeat(heatRequirement);
            T recipe = builder.build();
            if (recipe instanceof IMoreNbtConditions moreNbtConditions){
                moreNbtConditions.setKeepNbt(new ArrayList<>(keepNbt));
                moreNbtConditions.setMachNbt(new ArrayList<>(matchNbt));
            }
            return recipe;
        })));
    }
}