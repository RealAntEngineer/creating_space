package com.rae.creatingspace.content.ponders;

import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class FluidScene {
    public static void chemicalSynthesizer(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("synthesizer", "Crafting methane");
        scene.configureBasePlate(0,0,5);
        scene.showBasePlate();

        Selection pump1 = util.select.position(3,0,2);
        Selection pump2 = util.select.position(1,0,2);

        FluidStack hydrogen = new FluidStack(FluidInit.LIQUID_HYDROGEN.getSource(),8000);
        FluidStack methane = new FluidStack(FluidInit.LIQUID_METHANE.getSource(),100);

        ItemStack coal_dust = ItemInit.COAL_DUST.asStack(64);
        Capability<IFluidHandler> fluidHandler = ForgeCapabilities.FLUID_HANDLER;
        Capability<IItemHandler> inventoryHandler = ForgeCapabilities.ITEM_HANDLER;


        BlockPos hydrogen_tank_pos = util.grid.at(0,0,2);
        BlockPos methane_tank_pos = util.grid.at(4,0,2);
        BlockPos chest_pos = util.grid.at(2,2,2);

        scene.world.modifyBlockEntity(hydrogen_tank_pos, FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(hydrogen, IFluidHandler.FluidAction.EXECUTE)));

        scene.world.setKineticSpeed(pump1,16);
        scene.world.setKineticSpeed(pump2,16);

        scene.overlay.showText(60).text("You need to put hydrogen in it...");

        scene.idle(60);

        Selection coal_source = util.select.fromTo(2,1,2,3,3,3);

        scene.world.showSection(coal_source, Direction.NORTH);
        scene.world.modifyBlockEntity(chest_pos, ChestBlockEntity.class,be -> be.getCapability(inventoryHandler)
                .ifPresent(ifh -> ifh.insertItem(0,coal_dust,false)));

        scene.overlay.showText(60).text("...And put coal dust, then wait for methane to be produced");

        scene.idleSeconds(4);
        scene.world.modifyBlockEntity(methane_tank_pos,FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(methane, IFluidHandler.FluidAction.EXECUTE)));

        scene.overlay.showText(60).text("It produces 100mb of methane every 4 seconds");

        scene.idleSeconds(4);
        scene.world.modifyBlockEntity(methane_tank_pos,FluidTankBlockEntity.class, be -> be.getCapability(fluidHandler)
                .ifPresent(ifh -> ifh.fill(methane, IFluidHandler.FluidAction.EXECUTE)));

    }
}
