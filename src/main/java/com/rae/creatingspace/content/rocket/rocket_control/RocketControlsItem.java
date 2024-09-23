package com.rae.creatingspace.content.rocket.rocket_control;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;

public class RocketControlsItem extends BlockItem {
    public HashMap<String, BlockPos> initialPosMap = new HashMap<>();

    public RocketControlsItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }
}
