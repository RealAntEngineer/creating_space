package com.rae.creatingspace.server.items;

import com.rae.creatingspace.utilities.CSUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.List;

public abstract class RocketEngineItem extends BlockItem {
    public RocketEngineItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }

    public void appendEngineDependentText(List<Component> components,int ISP,int Trust){
        components.add(Component.translatable("creatingspace.science.isp")
                .append(Component.literal(" : " + ISP))
                .append(Component.translatable("creatingspace.science.unit.second")));
        components.add(Component.translatable("creatingspace.science.thrust")
                .append(Component.literal(" : "+ CSUtil.scientificNbrFormatting(Trust,10)))
                .append(Component.translatable("creatingspace.science.unit.newton")));

    }
}
