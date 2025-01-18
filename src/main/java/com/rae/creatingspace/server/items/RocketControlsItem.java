package com.rae.creatingspace.server.items;

import com.rae.creatingspace.server.blockentities.RocketControlsBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.HashMap;
import java.util.List;

public class RocketControlsItem extends BlockItem {

    public RocketControlsItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }
    @NonnullDefault
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        HashMap<ResourceLocation,BlockPos> initialBlockPos = RocketControlsBlockEntity.getPosMap(stack.getOrCreateTagElement("initialPosMap"));
        components.add(Component.literal("Stored dimension entry point :").withStyle(ChatFormatting.GOLD));
        for (ResourceLocation dimension:initialBlockPos.keySet()) {
            components.add(
                    Component.translatable(dimension.toString())
                            .append(" : ").withStyle(ChatFormatting.DARK_BLUE)
                            .append("x = ")
                            .append(String.valueOf(initialBlockPos.get(dimension).getX())).append(" ")
                            .append("z = ").append(String.valueOf(initialBlockPos.get(dimension).getZ())).withStyle(ChatFormatting.GRAY)
            );
        }

    }
}
