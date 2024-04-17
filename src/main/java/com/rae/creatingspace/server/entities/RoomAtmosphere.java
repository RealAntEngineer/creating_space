package com.rae.creatingspace.server.entities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import com.rae.creatingspace.server.blockentities.atmosphere.RoomPressuriserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
        shape = new RoomShape(searchTopology(firstPos));
        setBoundingBox(shape.getEncapsulatingBox());
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
    private boolean canGoThrough(BlockState blockState, Direction dir) {
        return blockState.isAir();
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        passiveFilters = new HashMap<>(
                PASSIVE_FILTER_CODEC.parse(NbtOps.INSTANCE, nbt.get("passiveFilters"))
                        .result().orElse(new HashMap<>())
        );
        o2amount = nbt.getInt("o2amount");
        shape = RoomShape.fromNbt((CompoundTag) nbt.get("shape"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.put("passiveFilters",
                PASSIVE_FILTER_CODEC.encodeStart(NbtOps.INSTANCE, passiveFilters).result()
                        .orElse(new CompoundTag()));
        nbt.putInt("o2amount", o2amount);
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
        return entityBuilder.sized(1, 1);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level.isClientSide()) {
            if (hasShape()) {
                List<Entity> entitiesInside = shape.getEntitiesInside(level);
                for (Entity entity :
                        entitiesInside) {
                    if (entity instanceof LivingEntity living && breathable()) {
                        consumeO2();
                    }
                }
                for (AtmosphereFilterData data : passiveFilters.values()) {
                    //System.out.println("globalImpact of "+this.getId()+ ": "+data.globalImpact);
                    o2amount += data.globalImpact;
                }
            }
        }
    }

    public boolean hasShape() {
        return !shape.listOfBox.isEmpty();
    }

    public void consumeO2() {
        if (o2amount >= 10) {
            o2amount -= 10;
        }
    }

    public boolean breathable() {
        return (float) o2amount / shape.getVolume() > 10;
    }

    public RoomShape getShape() {
        return shape;
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