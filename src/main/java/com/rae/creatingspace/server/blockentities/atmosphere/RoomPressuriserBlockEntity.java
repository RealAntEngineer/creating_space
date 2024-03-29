package com.rae.creatingspace.server.blockentities.atmosphere;

import com.rae.creatingspace.init.ingameobject.EntityInit;
import com.rae.creatingspace.server.blocks.atmosphere.RoomPressuriserBlock;
import com.rae.creatingspace.server.entities.RoomAtmosphere;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RoomPressuriserBlockEntity extends KineticBlockEntity {
    public RoomPressuriserBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    RoomAtmosphere room;

    public void tryRoom() {
        if (room != null && room.isAlive()) {
            room.regenerateRoom(getBlockPos().relative(getBlockState().getValue(RoomPressuriserBlock.FACING)));
        } else {
            room = new RoomAtmosphere(EntityInit.ATMOSPHERE_ENTITY.get(), getLevel());
            room.setPos(Vec3.atCenterOf(this.getBlockPos()));
            if (level != null) {
                level.addFreshEntity(room);
            }
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        int id = compound.getInt("room");
        if (level != null) {
            room = id == Integer.MIN_VALUE ? null : (RoomAtmosphere) level.getEntity(id);
        }
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("room", room == null ? Integer.MIN_VALUE : room.getId());
        super.write(compound, clientPacket);
    }
}