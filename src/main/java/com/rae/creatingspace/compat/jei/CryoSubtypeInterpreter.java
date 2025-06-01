package com.rae.creatingspace.compat.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


/* From JEI's Potion item subtype interpreter */
public class CryoSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	//TODO use ISubtypeInterpreter instead

	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		return null;
	}

	@Override
	public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
		return "";
	}

	/*@Override
	public String apply(ItemStack ingredient, UidContext context) {
		CompoundTag tank = ingredient.getOrCreateTag().getCompound(FLUID_NBT_KEY);
		FluidStack fluid = FluidStack.loadFluidStackFromNBT(tank);
		return fluid.getTranslationKey();

	}*/

}
