package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.content.fluids.meter.FlowGaugeBlockRenderer;
import com.rae.creatingspace.content.fluids.storage.CryogenicTankBlockEntity;
import com.rae.creatingspace.content.fluids.meter.FlowGaugeBlockEntity;
import com.rae.creatingspace.content.life_support.sealer.CSFanVisual;
import com.rae.creatingspace.content.life_support.sealer.RoomPressuriserRenderer;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankBlockEntity;

import com.rae.creatingspace.content.recipes.air_liquefying.AirLiquefierBlockEntity;
import com.rae.creatingspace.content.recipes.air_liquefying.AirLiquefierBlockRenderer;
import com.rae.creatingspace.content.recipes.chemical_synthesis.CatalystCarrierBlockEntity;
import com.rae.creatingspace.content.recipes.chemical_synthesis.CatalystCarrierRenderer;
import com.rae.creatingspace.content.recipes.electrolysis.MechanicalElectrolyzerBlockRenderer;
import com.rae.creatingspace.content.recipes.electrolysis.MechanicalElectrolyzerBlockEntity;
import com.rae.creatingspace.content.rocket.flight_recorder.FlightRecorderBlockEntity;
import com.rae.creatingspace.content.rocket.flight_recorder.FlightRecorderRenderer;
import com.rae.creatingspace.content.rocket.engine.RocketEngineBlockEntity;
import com.rae.creatingspace.content.rocket.engine.table.RocketEngineerTableBlockEntity;
import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlockEntity;
import com.rae.creatingspace.init.graphics.PartialModelInit;

import com.rae.creatingspace.content.life_support.sealer.RoomPressuriserBlockEntity;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.rae.creatingspace.CreatingSpace.REGISTRATE;

public class BlockEntityInit {
    public static final BlockEntityEntry<RocketEngineerTableBlockEntity> ENGINEER_TABLE =
            REGISTRATE.blockEntity("engineer_table", RocketEngineerTableBlockEntity::new)
                    .validBlocks(BlockInit.ROCKET_ENGINEER_TABLE)
                    .register();

    public static final BlockEntityEntry<RocketControlsBlockEntity> CONTROLS =
            REGISTRATE.blockEntity("controls", RocketControlsBlockEntity::new)
            .validBlocks(BlockInit.ROCKET_CONTROLS)
            .register();

    /*public static final BlockEntityEntry<GroundBuilderBlockEntity> GROUND_STATION =
            REGISTRATE.blockEntity("station", GroundBuilderBlockEntity::new)
                    .validBlocks(BlockInit.GROUND_STATION)
                    .register();*/

    public static final BlockEntityEntry<OxygenBacktankBlockEntity> OXYGEN_BACKTANK =
            REGISTRATE.blockEntity("oxygen_backtank",OxygenBacktankBlockEntity::new)
                    .validBlocks(BlockInit.COPPER_OXYGEN_BACKTANK,BlockInit.NETHERITE_OXYGEN_BACKTANK)
                    .register();

    public static final BlockEntityEntry<CryogenicTankBlockEntity> CRYOGENIC_TANK =
            REGISTRATE.blockEntity("cryogenic_tank", CryogenicTankBlockEntity::new)
                    .validBlocks(BlockInit.CRYOGENIC_TANK)
                    .register();

    public static final BlockEntityEntry<CatalystCarrierBlockEntity> CATALYST_CARRIER =
            REGISTRATE.blockEntity("catalyst_carrier", CatalystCarrierBlockEntity::new)
                    //.visual(() -> CatalystCarrierVisual::new, false)
                    .validBlocks(BlockInit.CATALYST_CARRIER)
                    .renderer(() -> CatalystCarrierRenderer::new)
                    .register();

    public static final BlockEntityEntry<RocketEngineBlockEntity.BigEngine> BIG_ENGINE =
            REGISTRATE.blockEntity(
                    "big_engine", RocketEngineBlockEntity.BigEngine::new)
                    .validBlocks(BlockInit.BIG_ROCKET_ENGINE)
                    .register();

    public static final BlockEntityEntry<RocketEngineBlockEntity.SmallEngine> SMALL_ENGINE =
            REGISTRATE.blockEntity(
                            "small_engine", RocketEngineBlockEntity.SmallEngine::new)
                    .validBlocks(BlockInit.SMALL_ROCKET_ENGINE)
                    .register();

    public static final BlockEntityEntry<RocketEngineBlockEntity.NbtDependent> NBT_DEPENDENT_ENGINE =
            REGISTRATE.blockEntity("rocket_engine", RocketEngineBlockEntity.NbtDependent::new)
                    .validBlocks(BlockInit.ROCKET_ENGINE)
                    .register();

    public static final BlockEntityEntry<MechanicalElectrolyzerBlockEntity> ELECTROLIZER =
            REGISTRATE.blockEntity(
                            "electrolyzer", MechanicalElectrolyzerBlockEntity::new)
                    //.visual(() -> ElectrolyzerVisual::new, true)
                    .validBlocks( BlockInit.MECHANICAL_ELECTROLYZER)
                    .renderer(()-> MechanicalElectrolyzerBlockRenderer::new)
                    .register();

    public static final BlockEntityEntry<AirLiquefierBlockEntity> AIR_LIQUEFIER =
            REGISTRATE.blockEntity(
                            "air_liquefier", AirLiquefierBlockEntity::new)
                    .visual(()-> CSFanVisual::new)
                    .validBlocks( BlockInit.AIR_LIQUEFIER)
                    .renderer(()-> AirLiquefierBlockRenderer::new)
                    .register();

    public static final BlockEntityEntry<FlowGaugeBlockEntity> FLOW_METER =
            REGISTRATE.blockEntity(
                            "flow_meter", FlowGaugeBlockEntity::new)
                    .validBlocks( BlockInit.FLOW_METER)
                    .renderer(()-> FlowGaugeBlockRenderer::new)
                    .register();

    public static final BlockEntityEntry<RoomPressuriserBlockEntity> ROOM_PRESSURIZER =
            REGISTRATE.blockEntity(
                            "room_pressurizer", RoomPressuriserBlockEntity::new)
                    .visual(() -> CSFanVisual::new)
                    .validBlocks(BlockInit.OXYGEN_SEALER)
                    .renderer(() -> RoomPressuriserRenderer::new)
                    .register();

    public static final BlockEntityEntry<FlightRecorderBlockEntity> FLIGHT_RECORDER =
            REGISTRATE.blockEntity(
                            "flight_recorder", FlightRecorderBlockEntity::new)
                    .visual(()-> SingleAxisRotatingVisual.ofZ(PartialModelInit.MEMORY_ROLL))
                    .validBlocks( BlockInit.FLIGHT_RECORDER)
                    .renderer(()-> FlightRecorderRenderer::new)
                    .register();

    public static void register() {}
}
