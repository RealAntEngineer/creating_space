package com.rae.creatingspace.mixin.recipe;

import com.mojang.serialization.*;
import com.rae.creatingspace.content.recipes.IMoreNbtConditions;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mixin(value = SequencedAssemblyRecipeSerializer.class)
public abstract class SequencedAssemblyRecipeSerializerMixin {

    @Shadow @Final private MapCodec<SequencedAssemblyRecipe> CODEC;
    final MapCodec<SequencedAssemblyRecipe> NEW_CODEC = new MapCodec<>() {
        @Override
        public <T> RecordBuilder<T> encode(SequencedAssemblyRecipe input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            return cS_1_21_1$encode(input, ops, prefix);
        }

        @Override
        public <T> DataResult<SequencedAssemblyRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
            return cS_1_21_1$decode(ops, input);
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.concat(
                    CODEC.keys(ops),
                    Stream.of(ops.createString("keepNbt"), ops.createString("matchNbt"))
            );
        }
    };


    //Black Magic
    @Unique
    private <T> DataResult<SequencedAssemblyRecipe> cS_1_21_1$decode(DynamicOps<T> ops, MapLike<T> input) {
        return CODEC.decode(ops, input).flatMap(assemblyRecipe -> {

            DataResult<List<String>> keepNbtResult = Codec.STRING.listOf().parse(ops,input.get(ops.createString("keepNbt")));

            DataResult<List<String>> matchNbtResult = Codec.STRING.listOf().parse(ops,input.get(ops.createString("matchNbt")));

            return keepNbtResult.flatMap(keepNbt ->
                    matchNbtResult.map(matchNbt -> {
                        ((IMoreNbtConditions)assemblyRecipe).setKeepNbt(new ArrayList<>(keepNbt));
                        ((IMoreNbtConditions)assemblyRecipe).setMachNbt(new ArrayList<>(matchNbt));
                        return assemblyRecipe;
                    })
            );
        });
    }

    @Unique
    private <T> RecordBuilder<T> cS_1_21_1$encode(SequencedAssemblyRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        return CODEC.encode(recipe, ops, prefix)
                .add("keepNbt", Codec.STRING.listOf().encodeStart(ops, ((IMoreNbtConditions) recipe).getKeepNbt()))
                .add("matchNbt", Codec.STRING.listOf().encodeStart(ops, ((IMoreNbtConditions) recipe).getMachNbt()));
    }

    private static final
    @Unique
    StreamCodec<ByteBuf, List<String>> STRING_LIST_STREAM_CODEC =
            ByteBufCodecs.fromCodec(Codec.list(Codec.STRING));

    @Inject(method = "fromNetwork", at = @At("RETURN"), cancellable = true)
    public void readKeepNbt(RegistryFriendlyByteBuf buffer, CallbackInfoReturnable<SequencedAssemblyRecipe> cir) {
        SequencedAssemblyRecipe recipe = cir.getReturnValue();
        ((IMoreNbtConditions) recipe).setKeepNbt(new ArrayList<>(STRING_LIST_STREAM_CODEC.decode(buffer)));
        ((IMoreNbtConditions) recipe).setMachNbt(new ArrayList<>(STRING_LIST_STREAM_CODEC.decode(buffer)));
        cir.setReturnValue(recipe);
    }

    @Inject(method = "toNetwork", at = @At("RETURN"))
    public void writeKeepNbt(RegistryFriendlyByteBuf buffer, SequencedAssemblyRecipe recipe, CallbackInfo ci) {
        STRING_LIST_STREAM_CODEC.encode(buffer, ((IMoreNbtConditions) recipe).getKeepNbt());
        STRING_LIST_STREAM_CODEC.encode(buffer, ((IMoreNbtConditions) recipe).getMachNbt());
    }

    @Inject(method = "codec", at = @At("RETURN"), cancellable = true)
    public void addToCodec(CallbackInfoReturnable<MapCodec<SequencedAssemblyRecipe>> cir) {
        cir.setReturnValue(NEW_CODEC);
    }
}