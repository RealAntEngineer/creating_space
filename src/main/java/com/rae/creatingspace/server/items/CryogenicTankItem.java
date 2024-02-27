package com.rae.creatingspace.server.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

public class CryogenicTankItem extends BlockItem {
    public CryogenicTankItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        CompoundTag tank = stack.getOrCreateTag().getCompound(FLUID_NBT_KEY);
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(tank);

        if (!fluid.isEmpty()){
            components.add(
                    Component.translatable(fluid.getTranslationKey())
                            .append("  ")
                            .append(String.valueOf(fluid.getAmount()))
                            .append(" / 4000mb")
                            .withStyle(ChatFormatting.AQUA)
            );
        }
        else {
            components.add(Component.literal("empty").withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, level, components, tooltipFlag);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return super.getMaxStackSize(stack);
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab creativeModeTab, @NotNull NonNullList<ItemStack> itemStacks) {
        if (this.allowedIn(creativeModeTab)) {
            for (Fluid fluid : Registry.FLUID) {
                String fluidName = ForgeRegistries.FLUIDS.getKey(fluid).toString();
                if (fluid.getFluidType().getTemperature() < 200 && !fluidName.contains("flowing")) {
                    ItemStack itemStack = this.getDefaultInstance();
                    CompoundTag tag = itemStack.getOrCreateTag();
                    FluidStack fluidStack = new FluidStack(fluid, 4000);
                    tag.put(FLUID_NBT_KEY, fluidStack.writeToNBT(new CompoundTag()));
                    itemStack.setTag(tag);
                    itemStacks.add(itemStack);
                }
            }
        }
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidHandlerItemStack(stack, 4000);
    }
}
