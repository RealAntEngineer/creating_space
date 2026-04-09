package com.rae.creatingspace.mixin.recipe;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.content.recipes.IMoreNbtConditions;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SequencedAssemblyRecipeSerializer.class)
public abstract class SequencedAssemblyRecipeSerializerMixin {

    @Unique
    private static final StreamCodec<ByteBuf, List<String>> STRING_LIST_STREAM_CODEC =
            ByteBufCodecs.fromCodec(Codec.list(Codec.STRING));

    @Inject(method = "streamCodec", at = @At("RETURN"), cancellable = true)
    public void addToStreamCodec(CallbackInfoReturnable<StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe>> cir) {
        StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> sc = StreamCodec.composite(
                cir.getReturnValue(), r -> r,
                STRING_LIST_STREAM_CODEC, r -> {
                    if (r instanceof IMoreNbtConditions moreNbtConditions) {
                        return moreNbtConditions.getKeepNbt();
                    }
                    return  List.of();
                },
                STRING_LIST_STREAM_CODEC, r -> {
                    if (r instanceof IMoreNbtConditions moreNbtConditions) {
                        return moreNbtConditions.getMachNbt();
                    }
                    return  List.of();
                },
                 (recipe, mach, keep) -> {
                    ((IMoreNbtConditions) recipe).setKeepNbt(new ArrayList<>(keep));
                    ((IMoreNbtConditions) recipe).setMachNbt(new ArrayList<>(mach));
                    return recipe;
                }
        );
        cir.setReturnValue(sc);

    }

    @Inject(method = "codec", at = @At("RETURN"), cancellable = true)
    public void addToCodec(CallbackInfoReturnable<MapCodec<SequencedAssemblyRecipe>> cir) {
        cir.setReturnValue(RecordCodecBuilder.mapCodec(
                i -> i.group(
                        cir.getReturnValue().forGetter(r -> r),
                        Codec.list(Codec.STRING).optionalFieldOf("keepNbt",List.of()).forGetter(
                                r -> {
                                    if (r instanceof IMoreNbtConditions moreNbtConditions) {
                                        return moreNbtConditions.getKeepNbt();
                                    }
                                    return  List.of();
                                }
                        ),
                        Codec.list(Codec.STRING).optionalFieldOf("matchNbt",List.of()).forGetter(
                                r -> {
                                    if (r instanceof IMoreNbtConditions moreNbtConditions) {
                                        return moreNbtConditions.getMachNbt();
                                    }
                                    return List.of();
                                }
                        )
                ).apply(i, (recipe, mach, keep) -> {
                            ((IMoreNbtConditions) recipe).setKeepNbt(new ArrayList<>(keep));
                            ((IMoreNbtConditions) recipe).setMachNbt(new ArrayList<>(mach));
                            return recipe;
                        }
                )
        ));

    }
}