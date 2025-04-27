package com.rae.creatingspace.content.fluids.storage;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

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

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CryogenicTankItemRenderer()));
    }
}
