package com.rae.creatingspace.server.entities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import com.rae.creatingspace.init.EntityDataSerializersInit;
import com.rae.creatingspace.server.blockentities.atmosphere.RoomPressuriserBlockEntity;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import java.util.*;

public class RoomAtmosphere extends Entity {

    private int o2amount;//equivalent to 1 mb of liquid oxygen ?
    private static final EntityDataAccessor<RoomShape> SHAPE_DATA_ACCESSOR = SynchedEntityData.defineId(RoomAtmosphere.class, EntityDataSerializersInit.SHAPE_SERIALIZER);

    public float getO2concentration() {
        return (float) this.o2amount / getShape().volume;
    }

    public void addO2(int o2amount) {
        this.o2amount += o2amount;
        this.o2amount = (int) Math.min(this.o2amount, 100 * getShape().getVolume());
    }
    public RoomAtmosphere(EntityType<?> entityType, Level level) {
        super(entityType, level);
        noPhysics = true;
        //shape = new RoomShape(new ArrayList<>());
    }

    //TODO first test with only 1 sealer then try with several the system to merge and separate rooms

    //TODO compatibility with contraptions
    //TODO make a search for every sealer inside -> list of sealer

    //TODO making special behavior for blocks and be inside an oxygen room.
    // plants will filter (absorbing C02 and releasing 02, living will consume 02 and release C02)
    ArrayList<BlockPos> roomSealers = new ArrayList<>();
    HashMap<ResourceLocation, AtmosphereFilterData> passiveFilters = new HashMap<>();

    //TODO make a regenerateRoom and a searchFrontier methode (regenerateRoom will only initiate the call, searchFrontier will be private)
    public void regenerateRoom(BlockPos firstPos) {
        passiveFilters = new HashMap<>();
        RoomShape shape = searchTopology(firstPos);
        setBoundingBox(shape.getEncapsulatingBox());
        entityData.set(SHAPE_DATA_ACCESSOR, shape);
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

    private RoomShape searchTopology(BlockPos start) {
        Queue<BlockPos> toVist = new ArrayDeque<>();
        toVist.add(start);
        ArrayList<AABB> tempRoom = new ArrayList<>();
        while (!toVist.isEmpty() && size(tempRoom) < 1000 * Math.max(roomSealers.size(), 1)) {
            BlockPos tempPos = toVist.poll();
            assert tempPos != null;
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
                        //TODO filter the collected block directly with the stream
                        BlockPos.betweenClosedStream(expansion.contract(1, 1, 1)).forEach(
                                (pos) -> {
                                    collectedPos.add(pos.immutable());
                                }
                        );
                        boolean canBeExpandedToward = true;
                        //ensure that there is no intersecting boxs
                        for (BlockPos pos : collectedPos) {
                            if (!tempAabb.contains(Vec3.atCenterOf(pos)) && (!canGoThrough(level, pos, dir) || contains(tempRoom, pos))) {
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
                                if (!tempAabb.contains(Vec3.atCenterOf(pos)) && !contains(tempRoom, pos)) {
                                    if (canGoThrough(level, pos, dir)) {
                                        toVist.add(pos);
                                        if (!level.getBlockState(pos).isAir()) {
                                            applyOnSolidBlock(pos);
                                        }
                                    } else {
                                        applyOnSolidBlock(pos);
                                    }
                                }
                            }
                        }
                    }
                }
                tempRoom.add(tempAabb);
            }
        }
        RoomShape shape = new RoomShape(tempRoom);
        if (size(tempRoom) >= 1000 * Math.max(roomSealers.size(), 1)) {
            shape.setOpen();
        }
        return shape;
    }

    private void applyOnSolidBlock(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (level.getBlockEntity(pos) instanceof RoomPressuriserBlockEntity rp) {
            roomSealers.add(pos);
        }
        if (state.getBlock() instanceof LeavesBlock) {
            ResourceLocation location = state.getBlock().builtInRegistryHolder().key().location();
            if (passiveFilters.containsKey(location)) {
                passiveFilters.put(location, passiveFilters.get(location).add(pos));
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

    private boolean canGoThrough(Level world, BlockPos currentPos, Direction dir) {
        //copied from AirCurrent
        BlockState state = world.getBlockState(currentPos);
        BlockState copycatState = CopycatBlock.getMaterial(world, currentPos);
        if (shouldAlwaysPass(copycatState.isAir() ? state : copycatState)) {
            return true;
        }

        VoxelShape shape = state.getCollisionShape(world, currentPos);
        if (shape.isEmpty()) {
            return true;
        }
        if (shape == Shapes.block()) {
            return false;
        }
        double shapeDepth = findMaxDepth(shape, dir);
        return shapeDepth == Double.POSITIVE_INFINITY;
    }

    //credit to Create for this code
    private static final double[][] DEPTH_TEST_COORDINATES = {
            {0.25, 0.25},
            {0.25, 0.75},
            {0.5, 0.5},
            {0.75, 0.25},
            {0.75, 0.75}
    };

    // Finds the maximum depth of the shape when traveling in the given direction.
    // The result is always positive.
    // If there is a hole, the result will be Double.POSITIVE_INFINITY.
    private static double findMaxDepth(VoxelShape shape, Direction direction) {
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getAxisDirection();
        double maxDepth = 0;

        for (double[] coordinates : DEPTH_TEST_COORDINATES) {
            double depth;
            if (axisDirection == Direction.AxisDirection.POSITIVE) {
                double min = shape.min(axis, coordinates[0], coordinates[1]);
                if (min == Double.POSITIVE_INFINITY) {
                    return Double.POSITIVE_INFINITY;
                }
                depth = min;
            } else {
                double max = shape.max(axis, coordinates[0], coordinates[1]);
                if (max == Double.NEGATIVE_INFINITY) {
                    return Double.POSITIVE_INFINITY;
                }
                depth = 1 - max;
            }

            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }

        return maxDepth;
    }

    private static boolean shouldAlwaysPass(BlockState state) {
        return AllTags.AllBlockTags.FAN_TRANSPARENT.matches(state);
    }

    //end of shadowing Create code TODO use mixin ? instead of coping the code ?
    @Override
    protected void defineSynchedData() {
        this.entityData.define(SHAPE_DATA_ACCESSOR, new RoomShape(new ArrayList<>()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        passiveFilters = new HashMap<>(
                PASSIVE_FILTER_CODEC.parse(NbtOps.INSTANCE, nbt.get("passiveFilters"))
                        .result().orElse(new HashMap<>())
        );
        o2amount = nbt.getInt("o2amount");
        entityData.set(SHAPE_DATA_ACCESSOR, RoomShape.fromNbt((CompoundTag) nbt.get("shape")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.put("passiveFilters",
                PASSIVE_FILTER_CODEC.encodeStart(NbtOps.INSTANCE, passiveFilters).result()
                        .orElse(new CompoundTag()));
        nbt.putInt("o2amount", o2amount);
        nbt.put("shape", getShape().toNbt());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void addBlockToFrontier(BlockPos pos) {
        RoomShape shape = getShape();
        shape.add(pos);
        this.entityData.set(SHAPE_DATA_ACCESSOR, shape);
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

        if (!level.isClientSide()) {
            if (hasShape()) {
                List<Entity> entitiesInside = getShape().getEntitiesInside(level);
                for (Entity entity :
                        entitiesInside) {
                    if (entity instanceof LivingEntity living && breathable()) {
                        consumeO2();
                    }
                }
                for (AtmosphereFilterData data : passiveFilters.values()) {
                    //System.out.println("globalImpact of "+this.getId()+ ": "+data.globalImpact);
                    addO2(data.globalImpact);
                }
            }
        }
    }

    public boolean hasShape() {
        return !getShape().listOfBox.isEmpty();
    }

    public void consumeO2() {
        if (o2amount >= 10) {
            o2amount -= 10;
        }
    }

    public boolean breathable() {
        return (float) o2amount / getShape().getVolume() > 10 && getShape().isClosed();
    }

    public RoomShape getShape() {
        return this.entityData.get(SHAPE_DATA_ACCESSOR);
    }

    private static final UnboundedMapCodec<ResourceLocation, AtmosphereFilterData> PASSIVE_FILTER_CODEC =
            Codec.unboundedMap(
                    ResourceLocation.CODEC, AtmosphereFilterData.CODEC);

    public record AtmosphereFilterData(ArrayList<BlockPos> positions, Integer individualImpact, int globalImpact) {

        public static Codec<AtmosphereFilterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(BlockPos.CODEC).fieldOf("positions").forGetter(i -> i.positions),
                Codec.INT.fieldOf("individualImpact").forGetter(i -> i.individualImpact)
        ).apply(instance, AtmosphereFilterData::new));

        AtmosphereFilterData(List<BlockPos> positions, int individualImpact) {
            this(new ArrayList<>(positions), individualImpact, individualImpact * positions.size());
        }

        public AtmosphereFilterData add(BlockPos pos) {
            if (!positions.contains(pos)) {
                positions.add(pos);
            }
            return new AtmosphereFilterData(positions, individualImpact, globalImpact + individualImpact);
        }

        public AtmosphereFilterData remove(BlockPos pos) {
            boolean flag = positions.remove(pos);
            return new AtmosphereFilterData(positions, individualImpact, flag ? globalImpact - individualImpact : globalImpact);
        }
    }
}