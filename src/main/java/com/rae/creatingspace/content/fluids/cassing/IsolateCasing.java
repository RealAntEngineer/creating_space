package com.rae.creatingspace.content.fluids.cassing;

import com.simibubi.create.content.decoration.encasing.EncasedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class IsolateCasing extends Block implements EncasedBlock {

    public IsolateCasing(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public Block getCasing() {
        // Return the current block itself, or a related block if necessary
        return this;
    }
}
