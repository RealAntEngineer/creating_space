package com.rae.creatingspace.server.entities;

import com.rae.creatingspace.server.blockentities.atmosphere.RoomPressuriserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.*;

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
    HashMap<ResourceLocation, AtmosphereFilterData> passiveFilters = new HashMap<>();
    Queue<BlockPos> toVist = new ArrayDeque<>();
    ArrayList<BlockPos> visited = new ArrayList<>();

    //TODO make a regenerateRoom and a searchFrontier methode (regenerateRoom will only initiate the call, searchFrontier will be private)
    public void regenerateRoom(BlockPos firstPos) {

        shape = new RoomShape(searchTopology(firstPos));
        /*for (AABB aabb:
             shape.listOfBox) {
            level.addFreshEntity(new SuperGlueEntity(level,aabb));
        }*/
    }


    private boolean contains(List<AABB> tempRoom, BlockPos tempPos) {
        boolean contain = false;
        for (AABB aabb : tempRoom) {
            if (aabb.contains(Vec3.atCenterOf(tempPos))) {
                contain = true;
            }
        }
        return contain;
    }

    /**
     * @param start the first pos of the room
     * @return a list of non-intersecting AABB covering the entirety of the room
     */

    private List<AABB> searchTopology(BlockPos start) {
        Queue<BlockPos> toVist = new ArrayDeque<>();
        toVist.add(start);
        ArrayList<AABB> tempRoom = new ArrayList<>();
        while (!toVist.isEmpty() && size(tempRoom) < 1000) {
            BlockPos tempPos = toVist.poll();

            if (!contains(tempRoom, tempPos)) {
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
                        //ensure that there is no intersecting boxs
                        for (BlockPos pos : collectedPos) {
                            if (!tempAabb.contains(Vec3.atCenterOf(pos)) && (!canGoThrough(level.getBlockState(pos), dir) || contains(tempRoom, pos))) {
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
                                if (!tempAabb.contains(Vec3.atCenterOf(pos)) && canGoThrough(level.getBlockState(pos), dir) && !contains(tempRoom, pos)) {
                                    toVist.add(pos);
                                } else if (!tempAabb.contains(Vec3.atCenterOf(pos)) && !contains(tempRoom, pos)) {
                                    applyOnSolidBlock(pos);
                                }
                            }
                        }
                    }
                }
                tempRoom.add(tempAabb);
            }
        }
        return tempRoom;
    }

    private void applyOnSolidBlock(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (level.getBlockEntity(pos) instanceof RoomPressuriserBlockEntity rp) {
            roomSealers.add(pos);
        }
        if (state.getBlock() instanceof LeavesBlock) {
            ResourceLocation location = state.getBlock().builtInRegistryHolder().key().location();
            if (passiveFilters.containsKey(location)) {
                passiveFilters.get(location).add(pos);
                System.out.println(passiveFilters.get(location).getPositions());
            } else {
                passiveFilters.put(location, new AtmosphereFilterData(new ArrayList<>(List.of(pos)), 1));
            }
        }
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
        shape = RoomShape.fromNbt((CompoundTag) nbt.get("shape"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.put("shape", shape.toNbt());
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
            if (hasShape()) {
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

    public boolean hasShape() {
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

    private static class AtmosphereFilterData {

        ArrayList<BlockPos> positions;
        float individualImpact;
        float globalImpact;

        AtmosphereFilterData(float individualImpact) {
            this(new ArrayList<>(), individualImpact);
        }

        AtmosphereFilterData(ArrayList<BlockPos> positions, float individualImpact) {
            this.positions = positions;
            this.individualImpact = individualImpact;
            this.globalImpact = individualImpact * positions.size();
        }

        public void add(BlockPos pos) {
            if (!positions.contains(pos)) {
                positions.add(pos);
            }
            globalImpact += individualImpact;
        }

        public void remove(BlockPos pos) {
            positions.remove(pos);
            globalImpact -= individualImpact;
        }

        public ArrayList<BlockPos> getPositions() {
            return positions;
        }
    }
}