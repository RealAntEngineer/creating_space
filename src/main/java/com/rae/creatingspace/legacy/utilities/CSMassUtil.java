package com.rae.creatingspace.legacy.utilities;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.IMass;
import com.rae.formicapi.data.managers.FloatMapDataLoader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class CSMassUtil {
    public static final FloatMapDataLoader<Block> MASS_MAP =
            new FloatMapDataLoader<>(CreatingSpace.MODID, "blocks_mass", ForgeRegistries.BLOCKS.getRegistryKey());

    public static int mass(BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof IMass hasAMass) {
            return (int) hasAMass.getMass();
        }
        return (int) MASS_MAP.getValue(state.getBlock(), 1000);
    }


}
