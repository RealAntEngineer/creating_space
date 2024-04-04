package com.rae.creatingspace.server.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class RoomShape {
    ArrayList<AABB> listOfBox;

    float xRot;
    float yRot;
    float zRot;

    RoomShape(List<AABB> listOfBox) {
        this.listOfBox = new ArrayList<>(listOfBox);
    }

    public static RoomShape fromNbt(CompoundTag nbt) {
        long[] serialized = nbt.getLongArray("listOfBox");
        ArrayList<AABB> listOfBox = new ArrayList<>(serialized.length / 2);
        for (int i = 0; i < serialized.length / 2; i++) {
            listOfBox.add(new AABB(BlockPos.of(serialized[i]), BlockPos.of(serialized[i + 1])));
        }

        return new RoomShape(listOfBox);
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        ArrayList<Long> listOfPos = new ArrayList<>();
        for (AABB aabb : listOfBox) {
            listOfPos.add((new BlockPos(aabb.minX, aabb.minY, aabb.minZ)).asLong());
            listOfPos.add((new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)).asLong());

        }
        tag.putLongArray("listOfBox", listOfPos);
        return tag;
    }

    public ArrayList<AABB> getListOfBox() {
        return listOfBox;
    }

    public void add(BlockPos pos) {
        AABB firstBlock = new AABB(pos);
        //expand the AABB to the frontier ? ( optimise will merge AABB)
        add(firstBlock);
    }

    public AABB getEncapsulatingBox() {
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

    public double getVolume(AABB aabb) {
        return (aabb.getXsize() * aabb.getYsize() * aabb.getZsize());
    }

    public double getVolume() {
        double volume = 0;
        for (AABB aabb :
                listOfBox) {
            volume += getVolume(aabb);

        }
        return volume;
    }


}