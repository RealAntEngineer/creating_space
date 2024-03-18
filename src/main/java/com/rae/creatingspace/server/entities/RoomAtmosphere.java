package com.rae.creatingspace.server.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class RoomAtmosphere extends Entity {

    Frontier frontier;

    public RoomAtmosphere(EntityType<?> entityType, Level level) {
        super(entityType, level);
        noPhysics = true;
    }

    //the tick method will be called by the sealer be
    static int searchPerTick = 200;
    //TODO first test with only 1 sealer then try with several the system to merge and separate rooms

    //TODO compatibility with contraptions
    //TODO make a search for every sealer inside -> list of sealer

    ArrayList<BlockPos> roomSealers = new ArrayList<>();

    public void regenerateRoom(BlockPos originalPos) {
        Queue<BlockPos> toVist = new ArrayDeque<>();
        ArrayList<BlockPos> visited = new ArrayList<>();
        toVist.add(originalPos);

        //if it ends without length condition then it's a closed room
        while (!toVist.isEmpty() && visited.size() < 200) {
            System.out.println("coucou");
            BlockPos pos = toVist.poll();
            frontier.add(pos);
            visited.add(pos);
            for (Direction dir : Direction.values()) {
                if (!visited.contains(pos.relative(dir)) && canGoThrough(level.getBlockState(pos.relative(dir)), dir)) {
                    toVist.add(pos.relative(dir));
                }
            }
        }
        AABB potentialBound = frontier.getEncapsulingBox();
        if (potentialBound != null) {
            setBoundingBox(potentialBound);
        }
    }

    private boolean canGoThrough(BlockState blockState, Direction dir) {
        return blockState.isAir();
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

    public boolean hasFrontier() {
        return !frontier.listOfBox.isEmpty();
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

        public AABB getEncapsulingBox() {
            Double minX = null;
            Double minY = null;
            Double minZ = null;
            Double maxX = null;
            Double maxY = null;
            Double maxZ = null;
            for (AABB aabb : listOfBox) {
                if (minX == null || minX > aabb.minX) {
                    minX = aabb.minX;
                }
                if (minY == null || minY > aabb.minY) {
                    minY = aabb.minY;
                }
                if (minZ == null || minZ > aabb.minZ) {
                    minZ = aabb.minZ;
                }
                if (maxX == null || maxX > aabb.maxX) {
                    maxX = aabb.maxX;
                }
                if (maxY == null || maxY > aabb.maxY) {
                    maxY = aabb.maxY;
                }
                if (maxZ == null || maxZ > aabb.maxZ) {
                    maxZ = aabb.maxZ;
                }
            }
            if (listOfBox.isEmpty()) {
                return null;
            }
            return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        }

        //TODO optimise
        public void addAll(List<BlockPos> posList) {
            for (BlockPos pos :
                    posList) {
                add(pos);
            }
        }

        private void add(AABB aabbs) {
            add(List.of(aabbs));
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
