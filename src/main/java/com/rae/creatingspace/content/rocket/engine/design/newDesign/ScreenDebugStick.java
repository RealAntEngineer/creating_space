package com.rae.creatingspace.content.rocket.engine.design.newDesign;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class ScreenDebugStick extends Item {
    public ScreenDebugStick(Properties p) {
        super(p);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        ScreenOpener.open(new NewDesignerScreen());
        return super.onItemUseFirst(stack, context);
    }
}