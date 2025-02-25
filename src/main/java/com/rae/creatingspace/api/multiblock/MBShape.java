package com.rae.creatingspace.api.multiblock;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MBShape {
    //make functions for every sizes
    public static MBShape make3x1x1(DirectionalBlock structure){
        return new MBShape(structure, new Vec3i(3, 1,1), new Vec3i(1,0,0),
                new HashMap<>(Map.of(
                        Direction.UP, List.of(List.of(List.of(Direction.UP),List.of(),List.of(Direction.DOWN))),
                        Direction.DOWN, List.of(List.of(List.of(Direction.DOWN),List.of(),List.of(Direction.UP))),
                        Direction.WEST, List.of(List.of(List.of(Direction.WEST)),List.of(),List.of(List.of(Direction.EAST))),
                        Direction.EAST, List.of(List.of(List.of(Direction.EAST)),List.of(),List.of(List.of(Direction.WEST))),
                        Direction.NORTH, List.of(List.of(List.of(Direction.NORTH,Direction.SOUTH))),
                        Direction.SOUTH, List.of(List.of(List.of(Direction.SOUTH,Direction.NORTH)))
                )
                ));
    }
    public static MBShape make2x1x1(DirectionalBlock structure){
        return new MBShape(structure, new Vec3i(2, 1,1), new Vec3i(1,0,0),
                new HashMap<>(Map.of(
                        Direction.UP, List.of(List.of(List.of(Direction.UP))),
                        Direction.DOWN, List.of(List.of(List.of(Direction.DOWN))),
                        Direction.WEST, List.of(List.of(List.of(Direction.WEST))),
                        Direction.EAST, List.of(List.of(List.of(Direction.EAST))),
                        Direction.NORTH, List.of(List.of(List.of(Direction.NORTH))),
                        Direction.SOUTH, List.of(List.of(List.of(Direction.SOUTH))))
                ));
    }
    //default is north
    private final DirectionalBlock structure;
    private final HashMap<Direction, List<List<List<Direction>>>> shapes;// X, Y, Z
    private final Vec3i defaultOffset;
    private final Vec3i defaultSize;

    public MBShape(DirectionalBlock structure,  Vec3i defaultSize,  Vec3i defaultOffset,HashMap<Direction, List<List<List<Direction>>>> shapes) {
        this.structure = structure;
        this.defaultOffset = defaultOffset;
        this.defaultSize = defaultSize;
        this.shapes = shapes;
    }

    public Vec3i getOffset(Direction facing,boolean mirrorOnDir){
        final int dirMultiply = facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE && mirrorOnDir ? -1 : 1;
        return switch (facing.getAxis()){
            case Z -> new Vec3i( defaultOffset.getZ(), defaultOffset.getY(), dirMultiply *defaultOffset.getX());
            case Y -> new Vec3i( defaultOffset.getY(), dirMultiply *defaultOffset.getX(),defaultOffset.getZ());
            default -> new Vec3i( dirMultiply *defaultOffset.getX(),defaultOffset.getY(), defaultOffset.getZ());
        };
    }
    public List<List<List<Direction>>> getShape(Direction facing){
        return shapes.get(facing);
    }
    public Vec3i getSize(Direction facing){
        return switch (facing.getAxis()){
            case Z -> new Vec3i(defaultSize.getZ(), defaultSize.getY(), defaultSize.getX());
            case Y -> new Vec3i(defaultSize.getY(), defaultSize.getX(),defaultSize.getZ());
            default -> defaultSize;
        };
    }
    /**
     * repair or place the structure blocks
     */
    public void repairStructure(Level level, BlockPos controlPos, Direction facing){
        try {
            List<List<List<Direction>>> shape = getShape(facing);//iteration over the structure, maybe do a function that takes a BlockPos consumer
            Vec3i off = getOffset(facing,false);
            Vec3i size = getSize(facing);
            boolean negative = facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
            for (int x = -off.getX();  x < size.getX() - off.getX(); x++) {
                for (int y = -off.getY(); y < size.getY() - off.getY(); y++) {
                    for (int z = -off.getZ(); z < size.getZ() - off.getZ(); z++) {
                        if (x != 0 || y != 0 || z != 0) {
                            //verify
                            BlockState structureShape = level.getBlockState(controlPos
                                    .offset(
                                            negative?-x:x,
                                            negative?-y:y,
                                            negative?-z:z
                                    ));
                            if (!structureShape.is(structure) || structureShape.getValue(DirectionalBlock.FACING) != shape.get(x + off.getX()).get(y + off.getY()).get(z + off.getZ())) {
                                level.setBlockAndUpdate(
                                        controlPos.offset(
                                                negative?-x:x,
                                                negative?-y:y,
                                                negative?-z:z),

                                        Blocks.DISPENSER.defaultBlockState().setValue(DirectionalBlock.FACING, shape.get(x + off.getX()).get(y + off.getY()).get(z + off.getZ()))
                                );
                            }
                        }
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            CreatingSpace.LOGGER.error("out of bound exception in Multiblock shape \"repair structure\" function, ask dev of Creating Space or anyone depending on it's API");
        }
    }
}
