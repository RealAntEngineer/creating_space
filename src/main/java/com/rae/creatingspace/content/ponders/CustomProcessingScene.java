package com.rae.creatingspace.content.ponders;

import com.google.common.collect.ImmutableList;
import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.content.recipes.chemical_synthesis.CatalystCarrierBlockEntity;
import com.rae.creatingspace.content.recipes.electrolysis.MechanicalElectrolyzerBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.IntAttached;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

public class CustomProcessingScene {
    public static void electrolysis(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("mechanical_electrolyzer", "Processing Fluids with the Mechanical Electrolyzer");
        scene.configureBasePlate(0, 0, 5);
        scene.world.setBlock(util.grid.at(1, 1, 2), AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(1, 4, 3, 1, 1, 5), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 1, 2), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 2, 2), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 4, 2), Direction.SOUTH);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(3, 1, 1, 1, 1, 1), Direction.SOUTH);
        scene.world.showSection(util.select.fromTo(3, 1, 5, 3, 1, 2), Direction.SOUTH);
        scene.idle(20);

        BlockPos basin = util.grid.at(1, 2, 2);
        BlockPos pressPos = util.grid.at(1, 4, 2);
        Vec3 basinSide = util.vector.blockSurface(basin, Direction.WEST);

        scene.overlay.showText(60)
                .pointAt(basinSide)
                .placeNearTarget()
                .attachKeyFrame()
                .text("With an Electrolyzer and Basin, some Fluids Can be decomposed into more reactive ones");
        scene.idle(40);

        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(basin), Pointing.LEFT).withItem(Items.WATER_BUCKET.getDefaultInstance()), 30);
        //scene.overlay.showControls(new InputWindowElement(util.vector.topOf(basin), Pointing.RIGHT).withItem(red), 30);
        scene.idle(30);
        Class<MechanicalElectrolyzerBlockEntity> type = MechanicalElectrolyzerBlockEntity.class;
        scene.world.modifyBlockEntity(pressPos, type, pte -> pte.startProcessingBasin());
        //scene.world.createItemOnBeltLike(basin, Direction.UP, red);
        //scene.world.createItemOnBeltLike(basin, Direction.UP, blue);
        scene.idle(80);
        scene.world.modifyBlockEntityNBT(util.select.position(basin), BasinBlockEntity.class, nbt -> {
            nbt.put("VisualizedFluids",
                    NBTHelper.writeCompoundList(ImmutableList.of(
                            IntAttached.with(10, new FluidStack(FluidInit.LIQUID_HYDROGEN.get(), 160)),
                            IntAttached.with(10, new FluidStack(FluidInit.LIQUID_OXYGEN.get(), 80))), ia -> ia.getValue()
                            .writeToNBT(new CompoundTag())));
        });
        scene.idle(4);
        //scene.world.createItemOnBelt(util.grid.at(1, 1, 1), Direction.UP, purple);
        scene.idle(30);

        scene.overlay.showText(80)
                .pointAt(basinSide)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Available recipes include any Shapeless Crafting Recipe, plus a couple extra ones");
        scene.idle(80);

        scene.rotateCameraY(-30);
        scene.idle(10);
        scene.world.setBlock(util.grid.at(1, 1, 2), AllBlocks.BLAZE_BURNER.getDefaultState()
                .setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED), true);
        scene.idle(10);

        scene.overlay.showText(80)
                .pointAt(basinSide.subtract(0, 1, 0))
                .placeNearTarget()
                .text("Some of those recipes may require the heat of a Blaze Burner");
        scene.idle(40);

        scene.rotateCameraY(30);

        scene.idle(60);
        Vec3 filterPos = util.vector.of(1, 2.75f, 2.5f);
        scene.overlay.showFilterSlotInput(filterPos, Direction.WEST, 100);
        scene.overlay.showText(100)
                .pointAt(filterPos)
                .placeNearTarget()
                .attachKeyFrame()
                .text("The filter slot can be used in case two recipes are conflicting.");
        scene.idle(80);
    }

    public static void chemical(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("catalyst_carrier", "Processing Fluids with the Catalyst Carrier");
        scene.configureBasePlate(0, 0, 5);
        scene.world.setBlock(util.grid.at(1, 1, 2), AllBlocks.ANDESITE_CASING.getDefaultState(), false);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(1, 4, 3, 1, 1, 5), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 1, 2), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 2, 2), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 4, 2), Direction.SOUTH);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(3, 1, 1, 1, 1, 1), Direction.SOUTH);
        scene.world.showSection(util.select.fromTo(3, 1, 5, 3, 1, 2), Direction.SOUTH);
        scene.idle(20);

        BlockPos basin = util.grid.at(1, 2, 2);
        BlockPos pressPos = util.grid.at(1, 4, 2);
        Vec3 basinSide = util.vector.blockSurface(basin, Direction.WEST);

        scene.overlay.showText(60)
                .pointAt(basinSide)
                .placeNearTarget()
                .attachKeyFrame()
                .text("With a Catalyst carrier and Basin, some Fluids Can be combined into something else");
        scene.idle(40);

        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(basin), Pointing.LEFT).withItem(Items.WATER_BUCKET.getDefaultInstance()), 30);
        //scene.overlay.showControls(new InputWindowElement(util.vector.topOf(basin), Pointing.RIGHT).withItem(red), 30);
        scene.idle(30);
        Class<CatalystCarrierBlockEntity> type = CatalystCarrierBlockEntity.class;
        scene.world.modifyBlockEntity(pressPos, type, CatalystCarrierBlockEntity::startProcessingBasin);
        //scene.world.createItemOnBeltLike(basin, Direction.UP, red);
        //scene.world.createItemOnBeltLike(basin, Direction.UP, blue);
        scene.idle(80);
        scene.world.modifyBlockEntityNBT(util.select.position(basin), BasinBlockEntity.class, nbt -> {
            nbt.put("VisualizedFluids",
                    NBTHelper.writeCompoundList(ImmutableList.of(
                            IntAttached.with(10, new FluidStack(FluidInit.LIQUID_HYDROGEN.get(), 286)),
                            IntAttached.with(10, new FluidStack(FluidInit.LIQUID_CO2.get(), 100))), ia -> ia.getValue()
                            .writeToNBT(new CompoundTag())));
        });
        scene.idle(4);
        //scene.world.createItemOnBelt(util.grid.at(1, 1, 1), Direction.UP, purple);
        scene.idle(30);

        scene.rotateCameraY(-30);
        scene.idle(10);
        scene.world.setBlock(util.grid.at(1, 1, 2), AllBlocks.BLAZE_BURNER.getDefaultState()
                .setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED), true);
        scene.idle(10);

        scene.overlay.showText(80)
                .pointAt(basinSide.subtract(0, 1, 0))
                .placeNearTarget()
                .text("Some of those recipes may require the heat of a Blaze Burner");
        scene.idle(40);

        scene.rotateCameraY(30);

        scene.idle(60);
        Vec3 filterPos = util.vector.of(1, 2.75f, 2.5f);
        scene.overlay.showFilterSlotInput(filterPos, Direction.WEST, 100);
        scene.overlay.showText(100)
                .pointAt(filterPos)
                .placeNearTarget()
                .attachKeyFrame()
                .text("The filter slot can be used in case two recipes are conflicting.");
        scene.idle(80);
    }

}
