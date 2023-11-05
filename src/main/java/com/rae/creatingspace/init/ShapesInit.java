package com.rae.creatingspace.init;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapesInit {


    public static final VoxelShaper OXYGEN_BACKTANK =
            shape(5, 0, 4, 11, 12, 10)
                    .add(5,2,3,11,11,4)
                    .add(2,3,5,6,11,9)
                    .add(10,3,5,14,11,9)
            .forHorizontal(Direction.NORTH);

    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }

    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

}
