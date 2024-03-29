package com.rae.creatingspace.server.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class RoomShape {
    ArrayList<AABB> listOfBox;//should be a list of points that define a frontier

    float xRot;
    float yRot;
    float zRot;

    RoomShape(List<AABB> listOfBox) {
        this.listOfBox = new ArrayList<>(listOfBox);
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
    //TODO what is the temporal complexity of getVolume ?

    //TODO use Pointcaré

    /**
     * return the volume defined by the intersection between the head and the tail ( union of every of its elements )
     */
    public double intersectedVolume(AABB head, List<AABB> tail) {
        if (tail.isEmpty()) {
            return getVolume(head);
        }
        while (!head.intersects(tail.get(tail.size() - 1))) {
            tail.remove(tail.size() - 1);
            if (tail.isEmpty()) {
                return getVolume(head);
            }
        }
        return getVolume(head.intersect(tail.get(tail.size() - 1))) + intersectedVolume(head, tail.subList(0, tail.size() - 1)) - intersectedVolume(head.intersect(tail.get(tail.size() - 1)), tail.subList(0, tail.size() - 1));
    }

    public double getVolume(AABB head, List<AABB> tail) {
        if (tail.isEmpty()) {
            return getVolume(head);
        }
        return getVolume(tail.get(tail.size() - 1), tail.subList(0, tail.size() - 1)) + getVolume(head) - intersectedVolume(head, tail);
    }

    public double getVolume() {
        long prevTime = System.currentTimeMillis();
        double volume = volumePoincare(listOfBox);
        System.out.println("poincaré" + (prevTime - System.currentTimeMillis()));
        prevTime = System.currentTimeMillis();
        volume = getVolume(listOfBox.get(listOfBox.size() - 1), listOfBox.subList(0, listOfBox.size() - 1));
        System.out.println("optimised ? poincaré" + (prevTime - System.currentTimeMillis()));
        return volume;
        //return getVolume(listOfBox.get(listOfBox.size()-1), listOfBox.subList(0,listOfBox.size()-1));
    }

    //https://stackoverflow.com/questions/54242442/is-there-a-method-returns-all-possible-combinations-for-n-choose-k
    static <T> Set<Set<T>> subsets(Set<T> set, int size) {
        if (size < 1) {
            return Collections.singleton(Collections.emptySet());
        }
        return set.stream()
                .flatMap(e -> subsets(remove(set, e), size - 1).stream().map(s -> add(s, e)))
                .collect(toSet());
    }

    static <T> Set<T> add(Set<T> set, T elem) {
        Set<T> newSet = new LinkedHashSet<>(set);
        newSet.add(elem);
        return newSet;
    }

    static <T> Set<T> remove(Set<T> set, T elem) {
        Set<T> newSet = new LinkedHashSet<>(set);
        newSet.remove(elem);
        return newSet;
    }

    //factorial complexity. Bro you're dumb
    public double volumePoincare(List<AABB> toUnify) {
        double unionWithoutIntersect = 0;
        for (AABB a : toUnify) {
            unionWithoutIntersect += getVolume(a);
        }
        double intersectedVolume = 0;
        int n = 0;
        for (int k = 2; k <= toUnify.size(); k++) {
            double partialVolume = 0;
            //maybe a while ?
            Set<Integer> fullIndexList = new LinkedHashSet<>(new ArrayList<>(toUnify.size()));
            for (int i = 0; i < toUnify.size(); i++) {
                fullIndexList.add(i);
            }
            Set<Set<Integer>> AllIndexList = subsets(fullIndexList, k);

            //adding all the possible combinaison
            //select k index in the toUnify list
            for (Set<Integer> indexList : AllIndexList) {
                AABB toIntersect = null;
                for (int i : indexList) {
                    n += 1;
                    if (toIntersect == null) {
                        toIntersect = toUnify.get(i);
                    } else {
                        toIntersect = toIntersect.intersect(toUnify.get(i));
                    }
                }
                partialVolume += toIntersect != null ? getVolume(toIntersect) : 0;
            }
            intersectedVolume += Math.pow(-1, k + 1) * partialVolume;
        }
        System.out.println("complexity : " + n);
        return unionWithoutIntersect + intersectedVolume;
    }

    //should be faster for small but complex room ( nbrBlocks**3 < nbrOfBox! )
    public double getVolumeCube() {
        ArrayList<BlockPos> blockPos = new ArrayList<>();
        for (AABB aabb : listOfBox) {
            BlockPos.betweenClosedStream(aabb.contract(1, 1, 1)).forEach(
                    blockPos::add
            );
        }
        return blockPos.stream().distinct().toList().size();
    }
}