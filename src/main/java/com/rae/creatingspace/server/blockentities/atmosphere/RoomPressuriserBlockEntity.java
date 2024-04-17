package com.rae.creatingspace.server.blockentities.atmosphere;

import com.rae.creatingspace.init.ingameobject.EntityInit;
import com.rae.creatingspace.server.blocks.atmosphere.RoomPressuriserBlock;
import com.rae.creatingspace.server.entities.RoomAtmosphere;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RoomPressuriserBlockEntity extends KineticBlockEntity {
    public RoomPressuriserBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }


    public void tryRoom() {
        assert level != null;
        if (!level.isClientSide) {
            boolean initialized = false;
            for (RoomAtmosphere room : level.getEntitiesOfClass(RoomAtmosphere.class,
                    new AABB(getBlockPos().relative(getBlockState()
                            .getValue(RoomPressuriserBlock.FACING))))) {
                if (room != null) {
                    initialized = true;
                    room.regenerateRoom(getBlockPos().relative(getBlockState().getValue(RoomPressuriserBlock.FACING)));
                }
            }
            if (!initialized) {
                RoomAtmosphere room = new RoomAtmosphere(EntityInit.ATMOSPHERE_ENTITY.get(), getLevel());
                room.setPos(Vec3.atCenterOf(this.getBlockPos()));
                if (level != null) {
                    level.addFreshEntity(room);
                    room.regenerateRoom(getBlockPos().relative(getBlockState().getValue(RoomPressuriserBlock.FACING)));
                }
            }
        }
    }

    @Override
    public void remove() {
        if (level != null) {
            for (RoomAtmosphere room : level.getEntitiesOfClass(RoomAtmosphere.class,
                    new AABB(getBlockPos().relative(getBlockState().getValue(RoomPressuriserBlock.FACING))))) {
                if (room != null)
                    room.kill();
            }
        }
        super.remove();
    }
}