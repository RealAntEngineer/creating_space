package com.rae.creatingspace.mixin.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.content.recipes.IMoreNbtConditions;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = StandardProcessingRecipe.Serializer.class)
public abstract class ProcessingRecipeSerializerMixin {

    //should target ProcessingRecipeParams::codec
    @Inject(method = "codec", at = @At("RETURN"), remap = false, cancellable = true)
    private <R extends StandardProcessingRecipe<?>> void  readKeepNbtJson(CallbackInfoReturnable<MapCodec<R>> cir) {
        //TODO add a keepNbt to the expected CODEC ? orr completely replacing it if that's not possible.
        MapCodec<R> OLD_CODEC = cir.getReturnValue();
        cir.setReturnValue(RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.list(Codec.STRING).optionalFieldOf("keepNbt",List.of()).forGetter(
                        i -> {
                            if (i instanceof IMoreNbtConditions moreNbtConditions) {
                                return moreNbtConditions.getKeepNbt();
                            }
                            return  List.of("");
                        }
                ),
                Codec.list(Codec.STRING).optionalFieldOf("matchNbt",List.of()).forGetter(
                        i -> {
                            if (i instanceof IMoreNbtConditions moreNbtConditions) {
                                return moreNbtConditions.getMachNbt();
                            }
                            return List.of("");
                        }
                ),
                //we can avoid this with the 			RecipeSerializer.SHAPED_RECIPE.codec().forGetter(t -> t), thingy
                OLD_CODEC.forGetter(t -> t)
        ).apply(instance, (keepNbt, matchNbt,recipe) -> {
            if (recipe instanceof IMoreNbtConditions moreNbtConditions){
                moreNbtConditions.setKeepNbt(new ArrayList<>(keepNbt));
                moreNbtConditions.setMachNbt(new ArrayList<>(matchNbt));
            }
            return recipe;
        })));
    }

    @Inject(method = "streamCodec", at = @At("RETURN"), remap = false, cancellable = true)
    private <R extends StandardProcessingRecipe<?>> void  readKeepNbtJsonStream(CallbackInfoReturnable<StreamCodec<RegistryFriendlyByteBuf, R>> cir) {
        StreamCodec<RegistryFriendlyByteBuf, R> old_stream_codec = cir.getReturnValue();

        StreamCodec<RegistryFriendlyByteBuf, R> wrapped = StreamCodec.of(
                (buf, recipe) -> {
                    old_stream_codec.encode(buf, recipe);
                    if (recipe instanceof IMoreNbtConditions moreNbtConditions) {
                        buf.writeWithCodec(NbtOps.INSTANCE,Codec.list(Codec.STRING), moreNbtConditions.getKeepNbt());
                        buf.writeWithCodec(NbtOps.INSTANCE,Codec.list(Codec.STRING), moreNbtConditions.getMachNbt());

                    }
                },
                buf -> {
                    // Read extra fields first

                    R recipe = old_stream_codec.decode(buf);

                    if (recipe instanceof IMoreNbtConditions moreNbtConditions) {
                        List<String> keepNbt = buf.readWithCodecTrusted(NbtOps.INSTANCE,Codec.list(Codec.STRING));
                        List<String> matchNbt =  buf.readWithCodecTrusted(NbtOps.INSTANCE,Codec.list(Codec.STRING));
                        moreNbtConditions.setKeepNbt(new ArrayList<>(keepNbt));
                        moreNbtConditions.setMachNbt(new ArrayList<>(matchNbt));
                    }
                    return recipe;
                }
        );

        cir.setReturnValue(wrapped);

    }
}