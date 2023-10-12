package com.rae.creatingspace.client.ponders;

import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class RocketScene {
    public static void rocketBuild(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("rocket", "Building a rocket");
        scene.configureBasePlate(0,0,5);
        scene.showBasePlate();

        Selection methaneTank = util.select.fromTo(0,2,1,0,4,1);
        Selection oxygenTank = util.select.fromTo(2,2,1,2,4,1);

        BlockPos methaneTank_pos = new BlockPos( 0,2,1);
        BlockPos oxygenTank_pos = new BlockPos( 2,2,1);


        Capability<IFluidHandler> fluidHandler = ForgeCapabilities.FLUID_HANDLER;
        FluidStack methane = new FluidStack(FluidInit.LIQUID_METHANE.getSource(),8000);
        FluidStack oxygen = new FluidStack(FluidInit.LIQUID_OXYGEN.getSource(),8000);

        Selection control = util.select.position(1,2,2);
        Selection seat = util.select.position(1,2,1);

        Selection engines = util.select.fromTo(0,1,0,2,1,2);

        scene.world.showSection(engines,Direction.DOWN);
        scene.world.showSection(control,Direction.UP);
        scene.overlay.showSelectionWithText(control,40).text("Rocket Controls");
        scene.world.showSection(methaneTank, Direction.EAST);
        scene.world.showSection(oxygenTank,Direction.WEST);

        scene.idleSeconds(5);
        scene.world.showSection(seat,Direction.UP);
        scene.overlay.showText(60).text("Don't forget the seat or you will be left Behind");

        scene.idleSeconds(3);
        scene.world.modifyBlockEntity(methaneTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(methane, IFluidHandler.FluidAction.EXECUTE)));
        scene.world.modifyBlockEntity(oxygenTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(oxygen, IFluidHandler.FluidAction.EXECUTE)));

        scene.overlay.showText(60).text("It produce 100mb of methane every 4 seconds");




    }
}
