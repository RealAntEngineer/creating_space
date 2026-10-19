package com.rae.creatingspace.content.life_support.sealer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.life_support.INeedOxygen;
import com.rae.creatingspace.init.EntityDataSerializersInit;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
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

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RoomAtmosphere extends Entity {

    private static final EntityDataAccessor<RoomShape> SHAPE_DATA_ACCESSOR = SynchedEntityData.defineId(RoomAtmosphere.class, EntityDataSerializersInit.SHAPE_SERIALIZER);
    private static final EntityDataAccessor<Integer> O2_AMOUNT = SynchedEntityData.defineId(RoomAtmosphere.class, EntityDataSerializers.INT);

    public float getO2concentration() {
        return (float) this.entityData.get(O2_AMOUNT) / getShape().volume;
    }

    public void addO2(int o2amount) {
        this.entityData.set(O2_AMOUNT,(int) Math.min(this.entityData.get(O2_AMOUNT)+ o2amount, 100 * getShape().getVolume()));

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
    Queue<BlockPos> toVist = new ArrayDeque<>();
    //TODO make a regenerateRoom and a searchFrontier methode (regenerateRoom will only initiate the call, searchFrontier will be private)
    public void regenerateRoom(BlockPos firstPos) {
        passiveFilters = new HashMap<>();
        roomSealers = new ArrayList<>();
        RoomShape shape = searchTopology(firstPos);
        setBoundingBox(shape.getEncapsulatingBox());
        entityData.set(SHAPE_DATA_ACCESSOR, shape);
    }


    private boolean contains(List<AABB> tempRoom, BlockPos tempPos) {
        // no Vec3 allocation per box: this runs for every candidate block against every box
        double x = tempPos.getX() + 0.5;
        double y = tempPos.getY() + 0.5;
        double z = tempPos.getZ() + 0.5;
        for (AABB aabb : tempRoom) {
            if (aabb.contains(x, y, z)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Queue the cells around a box we stopped growing because of a size cap (not because it was
     * blocked on every side), so the search continues from there instead of declaring the room closed.
     */
    private void enqueueFrontier(AABB box, List<AABB> tempRoom) {
        for (Direction dir : Direction.values()) {
            AABB slab = getExpandedPart(dir, box).contract(1, 1, 1);
            for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(slab.minX), Mth.floor(slab.minY), Mth.floor(slab.minZ),
                    Mth.floor(slab.maxX), Mth.floor(slab.maxY), Mth.floor(slab.maxZ))) {
                if (contains(tempRoom, pos)) {
                    continue;
                }
                if (canGoThrough(level(), pos, dir)) {
                    toVist.add(pos.immutable());
                    if (!level().getBlockState(pos).isAir()) {
                        applyOnBlock(pos);
                    }
                } else {
                    applyOnBlock(pos);
                }
            }
        }
    }

    /**
     * @param start the first pos of the room
     * @return a list of non-intersecting AABB covering the entirety of the room
     */

    private RoomShape searchTopology(BlockPos start) {
        toVist.add(start);
        // build a new list: the current shape is shared with in-flight packets and must never be modified
        ArrayList<AABB> tempRoom = new ArrayList<>(getShape().getListOfBox());

        // drop the box(es) containing the start, and take their volume out of the running total
        int removedVolume = 0;
        for (Iterator<AABB> it = tempRoom.iterator(); it.hasNext(); ) {
            AABB a = it.next();
            if (a.contains(Vec3.atCenterOf(start))) {
                removedVolume += (int) (a.getXsize() * a.getYsize() * a.getZsize());
                it.remove();
            }
        }
        int baseVolume = Math.max(0, getShape().volume - removedVolume);

        int addedSize = 0;

        while (!toVist.isEmpty() && addedSize < CSConfigs.SERVER.maxBlockPerTick.get()) {
            BlockPos tempPos = toVist.poll();
            //
            assert tempPos != null;
            if (!contains(tempRoom, tempPos)) {
                AABB tempAabb = new AABB(tempPos);

                boolean canContinue = true;//map for the 6 directions
                // a single box may not use more than what is left of this tick's budget
                double boxBudget = Math.max(CSConfigs.SERVER.maxBlockPerTick.get() - addedSize, 1);
                while (canContinue && tempAabb.getSize() < 30
                        && tempAabb.getXsize() * tempAabb.getYsize() * tempAabb.getZsize() < boxBudget) {
                    canContinue = false;
                    for (Direction dir : Direction.values()) {

                        AABB expansion = tempAabb.expandTowards(dir.getNormal().getX(), dir.getNormal().getY(), dir.getNormal().getZ());
                        AABB expandedPart = getExpandedPart(dir, tempAabb).contract(1,1,1);//to search only on the expanded portion

                        List<BlockPos> collectedPos = new ArrayList<>();
                        boolean canBeExpandedToward = true;
                        for (BlockPos pos:BlockPos.betweenClosed(Mth.floor(expandedPart.minX), Mth.floor(expandedPart.minY), Mth.floor(expandedPart.minZ),
                                Mth.floor(expandedPart.maxX), Mth.floor(expandedPart.maxY),Mth.floor( expandedPart.maxZ))) {
                            if (!contains(tempRoom, pos)) {//ensure that there is no intersecting boxs
                                collectedPos.add(pos.immutable());
                            } else {
                                canBeExpandedToward = false;
                            }
                        }
                        if (canBeExpandedToward) {
                            for (BlockPos pos : collectedPos) {
                                //make this smarter. it doesn't work with stairs.
                                if ((!(canGoThrough(level(), pos, dir) && canGoThrough(level(), tempPos, dir.getOpposite())))) {
                                    canBeExpandedToward = false;
                                    break;
                                }
                            }
                        }
                        //if we can expand toward that way we add it to the current box
                        // else we add every air block of this slice to vist
                        if (canBeExpandedToward) {
                            tempAabb = expansion;
                            canContinue = true;
                        } else {
                            for (BlockPos pos : collectedPos) {
                                if (canGoThrough(level(), pos, dir)) {
                                    toVist.add(pos);
                                    if (!level().getBlockState(pos).isAir()) {
                                        applyOnBlock(pos);
                                    }
                                } else {
                                    applyOnBlock(pos);
                                }
                            }
                        }
                    }
                }
                tempRoom.add(tempAabb);
                addedSize += (int) (tempAabb.getXsize() * tempAabb.getYsize() * tempAabb.getZsize());
                if (canContinue) {
                    // stopped by a size cap while it could still grow: keep exploring around it
                    enqueueFrontier(tempAabb, tempRoom);
                }
            }
        }

        RoomShape shape = RoomShape.adopt(tempRoom, baseVolume + addedSize);
        if (shape.volume >=  CSConfigs.SERVER.maxSizePerSealer.get() * Math.max(roomSealers.size(), 1)) {
            shape.setOpen();
            toVist = new ArrayDeque<>();
        }
        else if (toVist.isEmpty()){
            shape.setClosed();
            toVist = new ArrayDeque<>();
        }
        else {
            shape.setOpen();
        }

        return shape;
    }

    private static @NotNull AABB getExpandedPart(Direction dir, AABB tempAabb) {
        double minX = tempAabb.minX;
        double minY = tempAabb.minY;
        double minZ = tempAabb.minZ;
        double maxX = tempAabb.maxX;
        double maxY = tempAabb.maxY;
        double maxZ = tempAabb.maxZ;

        if (dir.getNormal().getX() < 0){
            maxX = minX;
            minX = minX-1;

        } else if (dir.getNormal().getX() > 0) {
            minX = maxX;
            maxX = maxX+1;
        }

        if (dir.getNormal().getY() < 0){
            maxY =  minY;
            minY = minY-1;


        } else if (dir.getNormal().getY() > 0) {
            minY = maxY;
            maxY = maxY+1;
        }

        if (dir.getNormal().getZ() < 0){
            maxZ =minZ;
            minZ = minZ-1;
        } else if (dir.getNormal().getZ() > 0) {
            minZ = maxZ;
            maxZ = maxZ+1;
        }
        return new AABB(minX,minY,minZ,maxX,maxY,maxZ);
    }

    private void applyOnBlock(BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        if (level().getBlockEntity(pos) instanceof RoomPressuriserBlockEntity rp && !roomSealers.contains(pos)) {
            roomSealers.add(pos);
        }
        if (state.getBlock() instanceof LeavesBlock) {
            ResourceLocation location = state.getBlock().builtInRegistryHolder().key().location();
            if (passiveFilters.containsKey(location)) {
                passiveFilters.put(location, passiveFilters.get(location).add(pos));
            } else {
                passiveFilters.put(location, new AtmosphereFilterData(new ArrayList<>(List.of(pos)), CSConfigs.SERVER.leafOxygenProduction.get()));
            }
        }
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SHAPE_DATA_ACCESSOR, new RoomShape(new ArrayList<>()));
        builder.define(O2_AMOUNT, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        passiveFilters = new HashMap<>(
                PASSIVE_FILTER_CODEC.parse(NbtOps.INSTANCE, nbt.get("passiveFilters"))
                        .result().orElse(new HashMap<>())
        );
        entityData.set(O2_AMOUNT,nbt.getInt("o2amount"));
        entityData.set(SHAPE_DATA_ACCESSOR, RoomShape.fromNbt((CompoundTag) nbt.get("shape")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.put("passiveFilters",
                PASSIVE_FILTER_CODEC.encodeStart(NbtOps.INSTANCE, passiveFilters).result()
                        .orElse(new CompoundTag()));
        nbt.putInt("o2amount", entityData.get(O2_AMOUNT));
        nbt.put("shape", getShape().toNbt());
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

        if (!level().isClientSide()) {
            if (!toVist.isEmpty()){
                entityData.set(SHAPE_DATA_ACCESSOR,searchTopology(toVist.poll()));
            }
            if (!getShape().closed){
                entityData.set(O2_AMOUNT,0);
            }
            if (hasShape()) {
                List<Entity> entitiesInside = getShape().getEntitiesInside(level());
                for (Entity entity :
                        entitiesInside) {
                    if (entity instanceof LivingEntity living && breathable()) {
                        consumeO2();//cancel event ?
                        ((INeedOxygen)living).setInsideOxygenRoom(true);
                    }
                }
                for (AtmosphereFilterData data : passiveFilters.values()) {
                    //System.out.println("globalImpact of "+this.getId()+ ": "+data.globalImpact);
                    addO2(data.globalImpact);
                }
            }
        }
        if (tickCount%10 == 0){
            lazyTick();
        }
    }

    public void lazyTick(){
        AABB box = getShape().getEncapsulatingBox();
        setBoundingBox(box==null? new AABB(getOnPos()): box);
    }

    public boolean hasShape() {
        return !getShape().listOfBox.isEmpty();
    }

    public void consumeO2() {
        if (entityData.get(O2_AMOUNT) >= CSConfigs.SERVER.livingO2Consumption.get()) {
            entityData.set(O2_AMOUNT,entityData.get(O2_AMOUNT) - CSConfigs.SERVER.livingO2Consumption.get());
        }
    }

    public boolean breathable() {
        return (float) entityData.get(O2_AMOUNT) / getShape().getVolume() > 10 && getShape().isClosed();
    }
    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    public RoomShape getShape() {
        return this.entityData.get(SHAPE_DATA_ACCESSOR);
    }
    public void resetShape(){
        this.entityData.set(SHAPE_DATA_ACCESSOR,new RoomShape(List.of()));
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