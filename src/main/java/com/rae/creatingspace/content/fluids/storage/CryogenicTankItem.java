package com.rae.creatingspace.content.fluids.storage;

import com.simibubi.create.foundation.ICapabilityProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;


public class CryogenicTankItem extends BlockItem {
    public CryogenicTankItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        return Component.empty().append(super.getName(itemStack)).append(" (").append(
                FluidStack.loadFluidStackFromNBT(itemStack.getOrCreateTagElement(FLUID_NBT_KEY))
                        .getDisplayName()).append(")");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        //TODO make a string constant somewhere for "Fluid"
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data!=null) {
            CompoundTag tank = (CompoundTag) data.copyTag().get("Fluid");

            if (tank == null) tank = new CompoundTag();

            FluidStack fluid = FluidStack.parseOptional(Objects.requireNonNull(context.registries()),tank);

            if (!fluid.isEmpty()) {
                components.add(
                        Component.translatable(fluid.getTranslationKey())
                                .append("  ")
                                .append(String.valueOf(fluid.getAmount()))
                                .append(" / 4000mb")
                                .withStyle(ChatFormatting.AQUA)
                );
            } else {
                components.add(Component.literal("empty").withStyle(ChatFormatting.GRAY));
            }
        }

        super.appendHoverText(stack, context, components, tooltipFlag);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return super.getMaxStackSize(stack);
    }


    //TODO capabilities  -> register capabilities in EventHandler with an item fluid capability
    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidHandlerItemStack(stack, 4000) {
            @Override
            public boolean canDrainFluidType(FluidStack fluid) {
                return fluid.getFluid().getFluidType().getTemperature() < 200;
            }

            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return fluid.getFluid().getFluidType().getTemperature() < 200;
            }
        };
    }
}
