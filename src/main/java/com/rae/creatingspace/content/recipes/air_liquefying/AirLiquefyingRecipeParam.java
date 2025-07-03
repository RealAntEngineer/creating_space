package com.rae.creatingspace.content.recipes.air_liquefying;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class AirLiquefyingRecipeParam extends ProcessingRecipeParams {
    private static final ResourceLocation DEFAULT_EMPTY = ResourceLocation.fromNamespaceAndPath("minecraft", "empy");
    public static MapCodec<AirLiquefyingRecipeParam> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(AirLiquefyingRecipeParam::new).forGetter(Function.identity()),
            ResourceLocation.CODEC.optionalFieldOf("blockInFront", DEFAULT_EMPTY)
                    .forGetter( r -> r.blockInFront!=null?r.blockInFront: DEFAULT_EMPTY),
            ResourceLocation.CODEC.optionalFieldOf("dimension", DEFAULT_EMPTY)
                    .forGetter( r -> r.dimension!=null?r.blockInFront: DEFAULT_EMPTY)
    ).apply(instance, (params, blockInFront, dimension) -> {
        params.blockInFront = blockInFront;
        params.dimension = dimension;
        return params;
    }));
    public static StreamCodec<RegistryFriendlyByteBuf, AirLiquefyingRecipeParam> STREAM_CODEC = streamCodec(AirLiquefyingRecipeParam::new);

    ResourceLocation blockInFront;
    ResourceLocation dimension;

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        blockInFront = buffer.readResourceLocation();
        if (blockInFront.equals(DEFAULT_EMPTY)) {
            blockInFront = null;
        }
        dimension = buffer.readResourceLocation();
        if (dimension.equals(DEFAULT_EMPTY)) {
            dimension = null;
        }
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        if (blockInFront != null)
            buffer.writeResourceLocation(blockInFront);
        else {
            buffer.writeResourceLocation(ResourceLocation.parse("minecraft:empty"));
        }
        if (dimension != null)
            buffer.writeResourceLocation(dimension);
        else {
            buffer.writeResourceLocation(ResourceLocation.parse("minecraft:empty"));
        }
    }
}
