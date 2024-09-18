package com.rae.creatingspace.compat.jei;

import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;
import com.simibubi.create.foundation.utility.NBTHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

/* From JEI's Potion item subtype interpreter */
public class CryoSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {

	@Override
	public String apply(ItemStack ingredient, UidContext context) {
		CompoundTag tank = ingredient.getOrCreateTag().getCompound(FLUID_NBT_KEY);
		FluidStack fluid = FluidStack.loadFluidStackFromNBT(tank);
		return fluid.getTranslationKey();

	}

}
