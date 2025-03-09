package com.rae.creatingspace.content.recipes.air_liquefying;

import com.google.gson.JsonObject;
import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AirLiquefyingRecipe extends ProcessingRecipe<SmartInventory> {

	private ResourceLocation blockInFront;
	private ResourceLocation dimension;

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
			Block block = ForgeRegistries.BLOCKS.getValue(airLiquefyingRecipe.getBlockInFront());
			if (block != null && !targetedState.is(block)) {
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

	protected AirLiquefyingRecipe(IRecipeTypeInfo type, ProcessingRecipeParams params) {
		super(type, params);
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

	public AirLiquefyingRecipe(ProcessingRecipeParams params) {
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
	public boolean matches(SmartInventory inv, @Nonnull Level worldIn) {
		return false;
	}

	@Override
	public void readAdditional(JsonObject json) {
		super.readAdditional(json);
		if (json.get("blockInFront") != null) {
			blockInFront = new ResourceLocation(String.valueOf(json.get("blockInFront")).replaceAll(String.valueOf('"'), ""));
		} else {
			blockInFront = null;
		}
		if (json.get("dimension") != null) {
			dimension = new ResourceLocation(String.valueOf(json.get("dimension")).replaceAll(String.valueOf('"'), ""));
		} else {
			dimension = null;
		}

	}

	@Override
	public void readAdditional(FriendlyByteBuf buffer) {
		super.readAdditional(buffer);
		blockInFront = buffer.readResourceLocation();
		if (blockInFront.equals(new ResourceLocation("minecraft:_"))) {
			blockInFront = null;
		}
		dimension = buffer.readResourceLocation();
		if (dimension.equals(new ResourceLocation("minecraft:_"))) {
			dimension = null;
		}
	}

	@Override
	public void writeAdditional(JsonObject json) {
		super.writeAdditional(json);
		if (blockInFront != null)
			json.addProperty("blockInFront", blockInFront.toString());
		if (dimension != null)
			json.addProperty("dimension", dimension.toString());

	}

	@Override
	public void writeAdditional(FriendlyByteBuf buffer) {
		super.writeAdditional(buffer);
		if (blockInFront != null)
			buffer.writeResourceLocation(blockInFront);
		else {
			buffer.writeResourceLocation(new ResourceLocation("minecraft:_"));
		}
		if (dimension != null)
			buffer.writeResourceLocation(dimension);
		else {
			buffer.writeResourceLocation(new ResourceLocation("minecraft:_"));
		}
	}
}
