package com.rae.creatingspace.content.fluids.storage;

import com.rae.creatingspace.init.DataComponentsInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.foundation.ICapabilityProvider;
import net.createmod.catnip.platform.CatnipClientServices;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;


public class CryogenicTankItem extends BlockItem {
    public CryogenicTankItem(Block block, Properties properties) {
        super(block, properties);
    }
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (itemStack, item)->
                        new FluidHandlerItemStack(() ->DataComponentsInit.SIMPLE_FLUID_CONTENT,itemStack, 4000) {
                    @Override
                    public boolean canDrainFluidType(FluidStack fluid) {
                        return fluid.getFluid().getFluidType().getTemperature() < 200;
                    }

                    @Override
                    public boolean canFillFluidType(FluidStack fluid) {
                        return fluid.getFluid().getFluidType().getTemperature() < 200;
                    }
                },
                BlockInit.CRYOGENIC_TANK.get().asItem()

                );
    }

    @Override
    public Component getName(ItemStack itemStack) {

        FluidHandlerItemStack fluidTank = (FluidHandlerItemStack) itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidTank != null) {
            FluidStack fluid = fluidTank.getFluid();
            return Component.empty().append(super.getName(itemStack)).append(" (").append(
                    Component.translatable(fluid.getTranslationKey())
            ).append(")");
        }
        return super.getName(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        //TODO make a string constant somewhere for "Fluid"
        //TODO this should be the standard way to register a tank in an item. -> replace each call to custom data and
        // parsing of fluid stack to a call to capability
        FluidHandlerItemStack fluidTank = (FluidHandlerItemStack) stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidTank != null) {
            FluidStack fluid = fluidTank.getFluid();

            if (!fluid.isEmpty()) {
                components.add(
                        Component.translatable(fluid.getTranslationKey())
                                .append("  ")
                                .append(String.valueOf(fluid.getAmount()))
                                .append(" / 4000mb")
                                //.withColor(CatnipClientServices.FLUID_HELPER.getColor(fluid.getFluid()))
                                .withStyle(ChatFormatting.AQUA)
                );
            } else {
                components.add(Component.literal("empty").withStyle(ChatFormatting.GRAY));
            }
        }

        super.appendHoverText(stack, context, components, tooltipFlag);
    }
}
