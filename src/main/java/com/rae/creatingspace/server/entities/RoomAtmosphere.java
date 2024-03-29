package com.rae.creatingspace.server.entities;

import com.rae.creatingspace.server.blockentities.atmosphere.RoomPressuriserBlockEntity;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class RoomAtmosphere extends Entity {

    RoomShape shape;
    private int o2amount;

    public RoomAtmosphere(EntityType<?> entityType, Level level) {
        super(entityType, level);
        noPhysics = true;
        shape = new RoomShape(new ArrayList<>());
    }

    //the tick method will be called by the sealer be
    static int searchPerTick = 100;
    static int maxSearch = 10;
    int nbrOfSearch = 0;
    // schedule a search for maximum 10 ticks and 200 block search every tick

    //TODO first test with only 1 sealer then try with several the system to merge and separate rooms

    //TODO compatibility with contraptions
    //TODO make a search for every sealer inside -> list of sealer

    boolean shouldContinueSearch = false;
    boolean hasSearchThisTick = false;
    //TODO making special behavior for blocks and be inside an oxygen room.
    // plants will filter (absorbing C02 and releasing 02, living will consume 02 and release C02)
    ArrayList<BlockPos> roomSealers = new ArrayList<>();
    Queue<BlockPos> toVist = new ArrayDeque<>();
    ArrayList<BlockPos> visited = new ArrayList<>();

    //TODO make a regenerateRoom and a searchFrontier methode (regenerateRoom will only initiate the call, searchFrontier will be private)
    public void regenerateRoom(BlockPos firstPos) {

        shape = new RoomShape(searchTopology(firstPos));
        System.out.println(shape.getVolume());
    }

    private List<AABB> searchTopology(BlockPos start) {
        Queue<BlockPos> toVist = new ArrayDeque<>();
        toVist.add(start);
        ArrayList<AABB> tempRoom = new ArrayList<>();
        while (!toVist.isEmpty() && size(tempRoom) < 1000) {
            BlockPos tempPos = toVist.poll();
            boolean contain = false;
            for (AABB aabb : tempRoom) {
                if (aabb.contains(Vec3.atCenterOf(tempPos))) {
                    contain = true;
                }
            }
            if (!contain) {
                AABB tempAabb = new AABB(tempPos);

                boolean canContinue = true;
                //make it possible to compute in batch
                while (canContinue && tempAabb.getSize() < 10) {
                    canContinue = false;
                    for (Direction dir : Direction.values()) {
                        //make a condition here to avoid looping when no walls
                        //verify that it's in the range of a room pressurizer ? or just size
                        //verify for positive dir and negative ones
                        AABB expansion = tempAabb.expandTowards(dir.getNormal().getX(), dir.getNormal().getY(), dir.getNormal().getZ());

                        List<BlockPos> collectedPos = new ArrayList<>();
                        BlockPos.betweenClosedStream(expansion.contract(1, 1, 1)).forEach(
                                (pos) -> {
                                    collectedPos.add(pos.immutable());
                                }
                        );
                        boolean canBeExpandedToward = true;
                        for (BlockPos pos : collectedPos) {
                            if (!tempAabb.contains(Vec3.atCenterOf(pos)) && !canGoThrough(level.getBlockState(pos), dir)) {
                                canBeExpandedToward = false;
                            }
                        }
                        //if we can expand toward that way we add it to the current box
                        // else we add every air block of this slice to vist
                        if (canBeExpandedToward) {
                            tempAabb = expansion;
                            canContinue = true;
                        } else {
                            for (BlockPos pos : collectedPos) {
                                if (!tempAabb.contains(Vec3.atCenterOf(pos)) && canGoThrough(level.getBlockState(pos), dir)) {
                                    toVist.add(pos);
                                } else if (!tempAabb.contains(Vec3.atCenterOf(pos))) {
                                    if (level.getBlockEntity(pos) instanceof RoomPressuriserBlockEntity rp) {
                                        roomSealers.add(pos);
                                    }
                                }
                            }
                        }
                    }
                }
                tempRoom.add(tempAabb);
            }
        }
        System.out.println(tempRoom);
        return tempRoom;
    }

    private int size(List<AABB> aabbs) {
        AABB aabb = new RoomShape(aabbs).getEncapsulatingBox();
        if (aabb == null) return 0;
        return (int) (aabb.getXsize() * aabb.getYsize() * aabb.getZsize());
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
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void addBlockToFrontier(BlockPos pos) {
        shape.add(pos);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<RoomAtmosphere> entityBuilder =
                (EntityType.Builder<RoomAtmosphere>) builder;
        return entityBuilder;
    }
    @Override
    public void tick() {
        super.tick();

        if (!level.isClientSide()) {
            if (hasFrontier()) {
                List<Entity> entitiesInside = shape.getEntitiesInside(this, level);
                for (Entity entity :
                        entitiesInside) {
                    if (entity instanceof LivingEntity living) {
                        consumeO2();
                        //cancel Oxygen suffocation event if present ??

                    }
                }
                /*if (shouldContinueSearch) {
                    regenerateRoom();
                }*/
            } else {
                //regenerateRoom();
            }
            hasSearchThisTick = false;
        }
    }

    public boolean hasFrontier() {
        return !shape.listOfBox.isEmpty();
    }

    public void consumeO2() {

    }

    public boolean isRoomBreathable() {
        return false;
    }

    public RoomShape getShape() {
        return shape;
    }

}