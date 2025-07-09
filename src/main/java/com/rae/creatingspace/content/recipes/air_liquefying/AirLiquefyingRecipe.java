package com.rae.creatingspace.content.recipes.air_liquefying;

import com.mojang.serialization.MapCodec;
import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AirLiquefyingRecipe extends ProcessingRecipe<RecipeWrapper, AirLiquefyingRecipeParam> {

	private final ResourceLocation blockInFront;
	private final ResourceLocation dimension;

	public static boolean match(AirLiquefierBlockEntity airLiquefierBlockEntity, Recipe<?> recipe) {
		return apply(airLiquefierBlockEntity, recipe, true);
	}

	public static boolean apply(AirLiquefierBlockEntity airLiquefierBlockEntity, Recipe<?> recipe) {
		return apply(airLiquefierBlockEntity, recipe, false);
	}

	private static boolean apply(AirLiquefierBlockEntity airLiquefierBlockEntity, Recipe<?> recipe, boolean test) {

		if (recipe instanceof AirLiquefyingRecipe airLiquefyingRecipe) {
			BlockState state = airLiquefierBlockEntity.getBlockState();
			BlockState targetedState = airLiquefierBlockEntity.getLevel().getBlockState(airLiquefierBlockEntity.getBlockPos().relative(state.getValue(AirLiquefierBlock.FACING)));
			Block block = BuiltInRegistries.BLOCK.get(airLiquefyingRecipe.getBlockInFront());
			if (!targetedState.is(block)) {
				return false;
			}
			ResourceLocation currentDimension = airLiquefierBlockEntity.getLevel().dimension().location();
			if (airLiquefyingRecipe.getDimension() != null && !currentDimension.equals(airLiquefyingRecipe.getDimension())) {
				return false;
			}
		} else {
			return false;
		}

		List<FluidStack> recipeOutputFluids = new ArrayList<>();

		for (boolean simulate : Iterate.trueAndFalse) {

			if (!simulate && test)
				return true;


			if (simulate) {
				AirLiquefyingRecipe basinRecipe = (AirLiquefyingRecipe) recipe;
				recipeOutputFluids.addAll(basinRecipe.getFluidResults());
			}
			if (!airLiquefierBlockEntity.acceptOutputs(recipeOutputFluids, simulate))
				return false;
		}

		return true;
	}
	public AirLiquefyingRecipe(AirLiquefyingRecipeParam params) {
		this(RecipeInit.AIR_LIQUEFYING, params);
	}
	protected AirLiquefyingRecipe(IRecipeTypeInfo type, AirLiquefyingRecipeParam params) {
		super(type, params);
		blockInFront = params.blockInFront;
		dimension = params.dimension;
	}

	public ResourceLocation getBlockInFront() {
		return blockInFront;
	}

	public ResourceLocation getDimension() {
		return dimension;
	}

	@Override
	protected int getMaxInputCount() {
		return 0;
	}

	@Override
	protected int getMaxOutputCount() {
		return 0;
	}



	@Override
	protected int getMaxFluidOutputCount() {
		return 2;
	}

	@Override
	protected boolean canSpecifyDuration() {
		return true;
	}

	@Override
	public boolean matches(@NotNull RecipeWrapper smartInventory, @NotNull Level level) {
		return false;
	}
	@FunctionalInterface
	public interface Factory<R extends AirLiquefyingRecipe> extends ProcessingRecipe.Factory<AirLiquefyingRecipeParam, R> {
		R create(AirLiquefyingRecipeParam params);
	}

	public static class Builder<R extends AirLiquefyingRecipe>
			extends ProcessingRecipeBuilder<AirLiquefyingRecipeParam, R, Builder<R>> {

		public Builder(AirLiquefyingRecipe.Factory<R> factory, ResourceLocation recipeId) {
			super(factory, recipeId);
		}

		@Override
		protected AirLiquefyingRecipeParam createParams() {
			return new AirLiquefyingRecipeParam();
		}

		@Override
		public Builder<R> self() {
			return this;
		}

		public Builder<R> blockInFront(ResourceLocation loc) {
			params.blockInFront = loc;
			return this;
		}

		public Builder<R> dimension(ResourceLocation dim) {
			params.dimension = dim;
			return this;
		}
	}


	public static class Serializer<R extends AirLiquefyingRecipe> implements RecipeSerializer<R> {
		private final MapCodec<R> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

		public Serializer(ProcessingRecipe.Factory<AirLiquefyingRecipeParam, R> factory) {
			this.codec = ProcessingRecipe.codec(factory, AirLiquefyingRecipeParam.CODEC);
			this.streamCodec = ProcessingRecipe.streamCodec(factory, AirLiquefyingRecipeParam.STREAM_CODEC);
		}

		@Override
		public MapCodec<R> codec() {
			return codec;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
			return streamCodec;
		}

	}


}
