package com.rae.creatingspace.init.ingameobject;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.decoration.encasing.EncasedBlock;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;

public class EncasingsInit {
    public static <B extends Block & EncasableBlock, E extends Block & EncasedBlock, P> void registerEncasings() {
        BlockEntry<FluidPipeBlock> fluidPipeSupplier = (BlockEntry<FluidPipeBlock>) AllBlocks.FLUID_PIPE;

        if (fluidPipeSupplier != null) {
            EncasingRegistry.addVariant(
                    AllBlocks.FLUID_PIPE.get(),
                    BlockInit.ISOLATED_FLUID_PIPE.get()
            );
        } else {
            // Handle the error appropriately, perhaps by logging a warning
            System.out.println("FLUID_PIPE is not registered or available.");
        }
        BlockEntry<PumpBlock> pumpSupplier = (BlockEntry<PumpBlock>) AllBlocks.MECHANICAL_PUMP;

        if (pumpSupplier != null) {
            PumpBlock pumpBlock = pumpSupplier.get();
                    EncasingRegistry.addVariant(
                            (B) pumpBlock,
                            BlockInit.ISOLATED_FLUID_PUMP.get()
                    );
        } else {
            System.out.println("MECHANICAL_PUMP is not registered or available.");
        }
    }
}
