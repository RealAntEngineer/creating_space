package com.rae.creatingspace.content.ponders;

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
        scene.title("rocket_building", "Building a rocket");
        scene.configureBasePlate(0,0,5);
        scene.showBasePlate();

        Selection methaneTank = util.select.fromTo(0,2,1,0,4,1);
        Selection oxygenTank = util.select.fromTo(2,2,1,2,4,1);

        BlockPos methaneTank_pos = new BlockPos( 0,2,1);
        BlockPos oxygenTank_pos = new BlockPos( 2,2,1);


        Capability<IFluidHandler> fluidHandler = ForgeCapabilities.FLUID_HANDLER;
        FluidStack methane = new FluidStack(FluidInit.LIQUID_METHANE.getSource(),8000);
        FluidStack oxygen = new FluidStack(FluidInit.LIQUID_OXYGEN.getSource(),8000);

        Selection control = util.select.position(1,2,1);
        Selection seat = util.select.position(1,2,0);
        Selection flight_recorder = util.select.position(0,2,0);

        Selection engines = util.select.fromTo(0,1,0,2,1,1);

        scene.overlay.showText(40).text("To build a rocket you need rocket controls,");

        scene.world.showSection(control,Direction.DOWN);
        scene.idleSeconds(3);
        scene.addKeyframe();


        scene.overlay.showText(40).text("enough propellant, here oxygen and methane");
        scene.world.showSection(methaneTank, Direction.EAST);
        scene.world.showSection(oxygenTank,Direction.WEST);
        scene.world.modifyBlockEntity(methaneTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(methane, IFluidHandler.FluidAction.EXECUTE)));
        scene.world.modifyBlockEntity(oxygenTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(oxygen, IFluidHandler.FluidAction.EXECUTE)));
        scene.idleSeconds(3);
        scene.addKeyframe();

        scene.overlay.showText(40).text("and enough thrust to lift the rocket");
        scene.world.showSection(engines,Direction.UP);
        scene.idleSeconds(3);
        scene.addKeyframe();

        scene.world.showSection(seat,Direction.DOWN);
        scene.overlay.showText(40).text("A seat can prevent falling from the rocket");
        scene.idleSeconds(3);
        scene.addKeyframe();

        scene.overlay.showText(40).text("If the there isn't enough thrust or propellant the rocket will refuse to go");
        scene.idleSeconds(3);
        scene.addKeyframe();

        scene.overlay.showText(40).text("the flight recorder can help in those moments");
        scene.world.showSection(flight_recorder,Direction.DOWN);
        scene.idleSeconds(3);
        scene.markAsFinished();
    }
    public static void rocketDebug(SceneBuilder scene,SceneBuildingUtil util ){
        scene.title("rocket_debugging", "Debugging a rocket");

        scene.configureBasePlate(0,0,5);
        scene.showBasePlate();

        Selection methaneTank = util.select.fromTo(0,2,1,0,4,1);
        Selection oxygenTank = util.select.fromTo(2,2,1,2,4,1);

        BlockPos methaneTank_pos = new BlockPos( 0,2,1);
        BlockPos oxygenTank_pos = new BlockPos( 2,2,1);


        Capability<IFluidHandler> fluidHandler = ForgeCapabilities.FLUID_HANDLER;
        FluidStack methane = new FluidStack(FluidInit.LIQUID_METHANE.getSource(),8000);
        FluidStack oxygen = new FluidStack(FluidInit.LIQUID_OXYGEN.getSource(),8000);

        Selection control = util.select.position(1,2,1);
        Selection seat = util.select.position(1,2,0);
        Selection flight_recorder = util.select.position(0,2,0);

        Selection engines = util.select.fromTo(0,1,0,2,1,1);

        scene.world.showSection(control,Direction.DOWN);
        scene.world.showSection(seat,Direction.DOWN);

        scene.world.showSection(engines,Direction.UP);

        scene.world.showSection(methaneTank, Direction.EAST);
        scene.world.showSection(oxygenTank,Direction.WEST);
        scene.world.modifyBlockEntity(methaneTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(methane, IFluidHandler.FluidAction.EXECUTE)));
        scene.world.modifyBlockEntity(oxygenTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(oxygen, IFluidHandler.FluidAction.EXECUTE)));
        scene.world.showSection(flight_recorder,Direction.DOWN);

        scene.overlay.showText(40).text("The flight recorder show tooltip info about about it's last flight or N/A if there it's just placed,");
        scene.overlay.showSelectionWithText(flight_recorder,60).text("N/A");
        scene.idleSeconds(3);
        scene.addKeyframe();

        scene.overlay.showText(40).text("If there is not enough propellant mass, you will get this :");
        scene.world.modifyBlockEntity(oxygenTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.drain(oxygen, IFluidHandler.FluidAction.EXECUTE)));

        scene.overlay.showSelectionWithText(flight_recorder,60).text(
                "not enough propellant");
        scene.idleSeconds(3);
        scene.addKeyframe();

        scene.overlay.showText(40).text("add more propellant to solve the issue :");
        scene.world.modifyBlockEntity(oxygenTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(oxygen, IFluidHandler.FluidAction.EXECUTE)));
        scene.idleSeconds(3);
        scene.addKeyframe();

        scene.overlay.showText(40).text("If there is enough mass but the wrong ratio of propellant, you will get this :");
        scene.world.modifyBlockEntity(oxygenTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.drain(oxygen, IFluidHandler.FluidAction.EXECUTE)));
        scene.world.modifyBlockEntity(oxygenTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(methane, IFluidHandler.FluidAction.EXECUTE)));
        scene.overlay.showSelectionWithText(flight_recorder,40).text(
                "wrong propellant ratio");
        scene.idleSeconds(3);
        scene.addKeyframe();

        scene.overlay.showText(40).text("equilibrate the propellants to solve the issue");
        scene.world.modifyBlockEntity(oxygenTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.drain(methane, IFluidHandler.FluidAction.EXECUTE)));
        scene.world.modifyBlockEntity(oxygenTank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(oxygen, IFluidHandler.FluidAction.EXECUTE)));
        scene.idleSeconds(3);
        scene.addKeyframe();

        scene.overlay.showText(40).text("if the rocket is too heavy you will get :");
        Selection weight = util.select.position(new BlockPos(2,2,0));
        scene.world.showSection(weight,Direction.DOWN);
        scene.overlay.showSelectionWithText(flight_recorder,40).text("not enough thrust");
        scene.idleSeconds(3);
        scene.addKeyframe();

        Selection additional_engines = util.select.fromTo(0,0,2,2,1,2);
        scene.world.showSection(additional_engines,Direction.UP);
        scene.overlay.showSelectionWithText(additional_engines,40).text("add more engines to solve the issue");
        scene.idleSeconds(3);
        scene.markAsFinished();
    }
}