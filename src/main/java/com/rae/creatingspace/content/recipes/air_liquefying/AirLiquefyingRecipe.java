package com.rae.creatingspace.content.recipes.air_liquefying;

import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AirLiquefyingRecipe extends ProcessingRecipe<SmartInventory, AirLiquefyingRecipeParam> {

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

	public AirLiquefyingRecipe(AirLiquefyingRecipeParam params) {
		this(RecipeInit.AIR_LIQUEFYING, params);
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
	public boolean matches(@NotNull SmartInventory smartInventory, @NotNull Level level) {
		return false;
	}

	@Override
	public @NotNull AirLiquefyingRecipeParam getParams() {
		return super.getParams();
	}


}
