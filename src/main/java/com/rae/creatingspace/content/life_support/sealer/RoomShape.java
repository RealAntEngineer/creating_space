package com.rae.creatingspace.content.life_support.sealer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RoomShape {
    ArrayList<AABB> listOfBox;
    boolean closed = true;
    float xRot;
    float yRot;
    float zRot;
    int volume;
    // cached encapsulating box, invalidated whenever the list is modified through this class
    private AABB cachedBounds;
    private boolean boundsValid;
    static Codec<List<AABB>> LIST_CODEC = Codec.list(
            RecordCodecBuilder.create(
                    instance ->
                            instance.group(
                                    Codec.DOUBLE.fieldOf("minX").forGetter(i-> i.minX),
                                    Codec.DOUBLE.fieldOf("minY").forGetter(i-> i.minY),
                                    Codec.DOUBLE.fieldOf("minZ").forGetter(i-> i.minZ),
                                    Codec.DOUBLE.fieldOf("maxX").forGetter(i-> i.maxX),
                                    Codec.DOUBLE.fieldOf("maxY").forGetter(i-> i.maxY),
                                    Codec.DOUBLE.fieldOf("maxZ").forGetter(i-> i.maxZ)
                                    ).apply(instance,AABB::new)
            )
    );
    RoomShape(List<AABB> listOfBox) {
        this.listOfBox = new ArrayList<>(listOfBox);
        calculateVolume();
    }

    RoomShape(List<AABB> listOfBox, int volume) {
        this.listOfBox = new ArrayList<>(listOfBox);
        this.volume = volume;
    }

    RoomShape(List<AABB> listOfBox, int volume, boolean closed) {
        this(listOfBox, volume);
        this.closed = closed;
    }

    /**
     * Takes ownership of {@code listOfBox} (no defensive copy). The caller must not touch the list afterwards.
     */
    static RoomShape adopt(ArrayList<AABB> listOfBox, int volume) {
        return new RoomShape(volume, listOfBox);
    }

    private RoomShape(int volume, ArrayList<AABB> ownedList) {
        this.listOfBox = ownedList;
        this.volume = volume;
    }

    /**
     * A published shape is never modified: {@code RoomAtmosphere.searchTopology} builds a new list and a new
     * shape on every rebuild and only sets {@code closed} before publishing. Entity-data packets are encoded
     * later on a Netty thread, and sharing an immutable shape needs neither a lock nor a copy per sync.
     * Do not call the mutators (add/remove/...) on a shape that has already been set on the entity.
     */
    public RoomShape copy() {
        return this;
    }

    public static RoomShape fromNbt(CompoundTag nbt) {

        return new RoomShape(LIST_CODEC.decode(NbtOps.INSTANCE, nbt.get("listOfBox")).result().orElseThrow().getFirst(), nbt.getInt("volume"), nbt.getBoolean("closed"));
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.put("listOfBox", LIST_CODEC.encodeStart(NbtOps.INSTANCE,listOfBox).result().orElse(new CompoundTag()));
        tag.putInt("volume", volume);
        tag.putBoolean("closed", closed);
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

    /**
     * @return the box enclosing every box of the room, or null if the room is empty. Cached.
     */
    public AABB getEncapsulatingBox() {
        if (!boundsValid) {
            cachedBounds = computeBounds();
            boundsValid = true;
        }
        return cachedBounds;
    }

    private AABB computeBounds() {
        if (listOfBox.isEmpty()) {
            return null;
        }
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        for (AABB aabb : listOfBox) {
            if (aabb.minX < minX) minX = aabb.minX;
            if (aabb.minY < minY) minY = aabb.minY;
            if (aabb.minZ < minZ) minZ = aabb.minZ;
            if (aabb.maxX > maxX) maxX = aabb.maxX;
            if (aabb.maxY > maxY) maxY = aabb.maxY;
            if (aabb.maxZ > maxZ) maxZ = aabb.maxZ;
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

    private void add(AABB aabb) {
        add(List.of(aabb));
    }
    private void add(List<AABB> aabbs) {
        listOfBox.addAll(aabbs);
        boundsValid = false;
        for (AABB aabb : aabbs) {
            volume += (int) getVolume(aabb);
        }
    }
    /**
     * Kept for compatibility. Prefer {@link #getLivingInside(Level)}.
     */
    public List<Entity> getEntitiesInside(Level level) {
        ArrayList<Entity> entities = new ArrayList<>();
        for (AABB box :
                listOfBox) {
            entities.addAll(level.getEntities(null, box));
        }
        return entities.stream().distinct().toList();
    }

    /**
     * Living entities whose bounding box touches the room. Uses one typed query on the room's
     * bounding box instead of one all-entity query per box, then filters against the boxes.
     */
    public List<LivingEntity> getLivingInside(Level level) {
        AABB bounds = getEncapsulatingBox();
        if (bounds == null) {
            return List.of();
        }
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, bounds);
        if (candidates.isEmpty()) {
            return candidates;
        }
        List<LivingEntity> inside = new ArrayList<>(candidates.size());
        for (LivingEntity entity : candidates) {
            AABB entityBox = entity.getBoundingBox();
            for (AABB box : listOfBox) {
                if (box.intersects(entityBox)) {
                    inside.add(entity);
                    break;
                }
            }
        }
        return inside;
    }

    public void calculateVolume() {
        double volume = 0;
        for (AABB aabb :
                listOfBox) {
            volume += getVolume(aabb);

        }
        this.volume = (int) volume;
    }

    public double getVolume(AABB aabb) {
        return (aabb.getXsize() * aabb.getYsize() * aabb.getZsize());
    }

    public double getVolume() {
        return volume;
    }
    public boolean inside(AABB colBox) {
        for (AABB box : listOfBox) {
            if (box.intersects(colBox)) return true;
        }
        return false;
    }

    public void remove(BlockPos pos) {
        listOfBox.removeIf(aabb -> aabb.contains(Vec3.atCenterOf(pos)));
        boundsValid = false;
    }

    public void remove(AABB toRemove) {
        ArrayList<AABB> toRemoveList = new ArrayList<>();
        for (AABB aabb:listOfBox){
            toRemoveList.addAll(carve(toRemove, aabb));
        }
        listOfBox = toRemoveList;
        boundsValid = false;
    }

    public void setClosed() {
        this.closed = true;
    }

    public void setOpen() {
        this.closed = false;
    }

    public boolean isClosed() {
        return closed;
    }

    private List<AABB> carve(AABB carver, AABB carved) {
        if (carved.intersects(carver)) {
            return List.of(carved);
        }
        AABB iCarver = carver.intersect(carved);
        ArrayList<AABB> aabbs = new ArrayList<>(6);
        aabbs.add(new AABB(carved.minX, carved.minY, carved.minZ, iCarver.minX, carved.maxY, carved.maxZ));
        aabbs.add(new AABB(iCarver.maxX, carved.minY, carved.minZ, carver.maxX, carved.maxY, carved.maxZ));

        aabbs.add(new AABB(iCarver.minX, carved.minY, carved.minZ, iCarver.maxX, carved.maxY, iCarver.minZ));
        aabbs.add(new AABB(iCarver.minX, carved.minY, iCarver.maxZ, iCarver.maxX, carved.maxY, carved.maxZ));

        aabbs.add(new AABB(iCarver.minX, carved.minY, iCarver.minZ, iCarver.maxX, iCarver.minY, iCarver.maxZ));
        aabbs.add(new AABB(iCarver.minX, iCarver.maxY, iCarver.minZ, iCarver.maxX, carved.maxY, iCarver.maxZ));

        return aabbs.stream().filter(aabb -> aabb.getSize() != 0).toList();
    }
}