package com.rae.creatingspace.init;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapeInit extends AllShapes {


    public static final VoxelShaper
    EXPLOSIVE_STARTER = shape(0,0,0,1,1,1).forHorizontalAxis(),
    EXPLOSIVE_STARTER_FLOOR = shape(0,0,0,1,1,1).forHorizontalAxis(),
    EXPLOSIVE_STARTER_WALL = shape(0,0,0,1,1,1).forHorizontal(Direction.NORTH);

    private static Builder shape(VoxelShape shape) {
        return new Builder(shape);
    }

    private static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
