package com.rae.creatingspace.utilities;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.utilities.data.MassOfBlockReader;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;

public class CSMassUtil {

    public static int mass(BlockState state){
        MassOfBlockReader.PartialMassMap data = MassOfBlockReader.MASS_HOLDER.getData(CreatingSpace.resource("blocks_mass"));
        if( data!=null){
            String id = Registry.BLOCK.getKey(state.getBlock()).toString();
            Integer mass = data.massMap().get(id);
            if (mass!=null){
                return mass;
            }
        }
        return 1000;
    }


}
