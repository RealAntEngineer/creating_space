package com.rae.creatingspace.server.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class RoomAtmosphere extends Entity {

    Frontier frontier;

    public RoomAtmosphere(EntityType<?> entityType, Level level) {
        super(entityType, level);
        noPhysics = true;
    }


    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return null;
    }

    public void addBlockToFrontier(BlockPos pos) {
        frontier.add(pos);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<RoomAtmosphere> entityBuilder =
                (EntityType.Builder<RoomAtmosphere>) builder;
        return entityBuilder.sized(1, 1);
    }

    @Override
    public void tick() {
        super.tick();
        List<Entity> entitiesInside = frontier.getEntitiesInside(this, level);
        for (Entity entity :
                entitiesInside) {
            if (entity instanceof LivingEntity living) {
                //cancel Oxygen suffocation event if present ??

            }
        }
    }

    private class Frontier {
        ArrayList<AABB> listOfBox;//should be a list of points that define a frontier

        float xRot;
        float yRot;
        float zRot;

        Frontier(List<AABB> listOfBox) {
            this.listOfBox = new ArrayList<>(listOfBox);
        }

        public void add(BlockPos pos) {
            AABB firstBlock = new AABB(pos);
            //expand the AABB to the frontier ? ( optimise will merge AABB)
            add(firstBlock);
        }

        //TODO optimise
        public void addAll(List<BlockPos> posList) {
            for (BlockPos pos :
                    posList) {
                add(pos);
            }
        }

        private void add(AABB aabbs) {
            add(aabbs);
        }

        private void add(List<AABB> aabbs) {
            listOfBox.addAll(aabbs);
        }

        /**
         * optimise the frontier to delete any unused part
         */
        public void optimise() {

        }

        public List<Entity> getEntitiesInside(RoomAtmosphere parent, Level level) {
            ArrayList<Entity> entities = new ArrayList<>();
            for (AABB box :
                    listOfBox) {
                entities.addAll(level.getEntities(parent, box));
            }
            return entities;
        }

    }
}
