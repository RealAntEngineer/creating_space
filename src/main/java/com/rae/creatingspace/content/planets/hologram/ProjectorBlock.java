package com.rae.creatingspace.content.planets.hologram;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ProjectorBlock extends Block implements IBE<ProjectorBlockEntity> {
    public ProjectorBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Class<ProjectorBlockEntity> getBlockEntityClass() {
        return ProjectorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ProjectorBlockEntity> getBlockEntityType() {
        return BlockEntityInit.PROJECTOR.get();
    }
}
