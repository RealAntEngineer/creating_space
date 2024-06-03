package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.client.renderer.blockentity.*;
import com.rae.creatingspace.client.renderer.instance.*;
import com.rae.creatingspace.server.armor.OxygenBacktankBlockEntity;
import com.rae.creatingspace.server.blockentities.*;
import com.rae.creatingspace.server.blockentities.atmosphere.OxygenBlockEntity;
import com.rae.creatingspace.server.blockentities.atmosphere.RoomPressuriserBlockEntity;
import com.rae.creatingspace.server.blockentities.atmosphere.SealerBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
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

    public static final BlockEntityEntry<RocketGeneratorBlockEntity> ROCKET_GENERATOR =
            REGISTRATE.blockEntity("rocket_generator", RocketGeneratorBlockEntity::new )
                    .instance(() -> ShaftInstance::new, false)
                    .validBlocks(BlockInit.ROCKET_GENERATOR)
                    .renderer(() -> RocketGeneratorBlockRenderer::new)
                    .register();

    public static final BlockEntityEntry<ChemicalSynthesizerBlockEntity> SYNTHESIZER =
            REGISTRATE.blockEntity("synthesizer", ChemicalSynthesizerBlockEntity::new)
                    .validBlocks(BlockInit.CHEMICAL_SYNTHESIZER)
                    .register();

    public static final BlockEntityEntry<CatalystCarrierBlockEntity> CATALYST_CARRIER =
            REGISTRATE.blockEntity("catalyst_carrier", CatalystCarrierBlockEntity::new)
                    .instance(() -> CatalystCarrierInstance::new)
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
                    .validBlocks(BlockInit.SUPER_ROCKET_ENGINE)
                    .register();

    public static final BlockEntityEntry<LegacyMechanicalElectrolyzerBlockEntity> LEGACY_ELECTROLIZER =
            REGISTRATE.blockEntity(
                            "legacy_electrolyzer", LegacyMechanicalElectrolyzerBlockEntity::new)
                    .instance(() -> ShaftInstance::new, false)
                    .validBlocks(BlockInit.LEGACY_MECHANICAL_ELECTROLYZER)
                    .renderer(() -> LegacyMechanicalElectrolyserBlockRenderer::new)
                    .register();
    public static final BlockEntityEntry<MechanicalElectrolyzerBlockEntity> ELECTROLIZER =
            REGISTRATE.blockEntity(
                            "electrolyzer", MechanicalElectrolyzerBlockEntity::new)
                    .instance(() -> ElectrolyzerInstance::new, false)
                    .validBlocks( BlockInit.MECHANICAL_ELECTROLYZER)
                    .renderer(()-> MechanicalElectrolyserBlockRenderer::new)
                    .register();
    public static final BlockEntityEntry<AirLiquefierBlockEntity> AIR_LIQUEFIER =
            REGISTRATE.blockEntity(
                            "air_liquefier", AirLiquefierBlockEntity::new)
                    .instance(()-> AirLiquefierInstance::new)
                    .validBlocks( BlockInit.AIR_LIQUEFIER)
                    .renderer(()-> AirLiquefierBlockRenderer::new)
                    .register();
    public static final BlockEntityEntry<FlowGaugeBlockEntity> FLOW_METER =
            REGISTRATE.blockEntity(
                            "flow_meter", FlowGaugeBlockEntity::new)
                    .validBlocks( BlockInit.FLOW_METER)
                    .renderer(()-> FlowGaugeBlockRenderer::new)
                    .register();
    public static final BlockEntityEntry<OxygenBlockEntity> OXYGEN =
            REGISTRATE.blockEntity(
                            "oxygen", OxygenBlockEntity::new)
                    .validBlocks(BlockInit.OXYGEN)
                    .register();

    public static final BlockEntityEntry<SealerBlockEntity> OXYGEN_SEALER =
            REGISTRATE.blockEntity(
                            "oxygen_sealer", SealerBlockEntity::new)
                    .instance(()-> OxygenSealerInstance::new)
                    .validBlocks(BlockInit.OXYGEN_SEALER)
                    .renderer(() -> OxygenSealerRenderer::new)
                    .register();
    public static final BlockEntityEntry<RoomPressuriserBlockEntity> ROOM_PRESSURIZER =
            REGISTRATE.blockEntity(
                            "room_pressurizer", RoomPressuriserBlockEntity::new)
                    .instance(() -> RoomPressuriserInstance::new)
                    .validBlocks(BlockInit.ROOM_PRESSURISER)
                    .renderer(() -> RoomPressuriserRenderer::new)
                    .register();
    public static final BlockEntityEntry<FlightRecorderBlockEntity> FLIGHT_RECORDER =
            REGISTRATE.blockEntity(
                            "flight_recorder", FlightRecorderBlockEntity::new)
                    .instance(()-> FlightRecorderInstance::new,false)
                    .validBlocks( BlockInit.FLIGHT_RECORDER)
                    .renderer(()-> FlightRecorderRenderer::new)
                    .register();


    /*public static final BlockEntityEntry<IOBlockEntity> IO_TILE = REGISTRATE
            .blockEntity("io", IOBlockEntity::new)
            .validBlocks(BlockInit.IO_BLOCK)
            .register();
    public static final BlockEntityEntry<MultiblockBlockEntity> GHOST_TILE = REGISTRATE
            .blockEntity("multiblock", MultiblockBlockEntity::new)
            .validBlocks(GHOST_BLOCK)
            .register();
    public static final BlockEntityEntry<KineticInputBlockEntity> KINETIC_INPUT_TILE = REGISTRATE
            .blockEntity("kinetic_input", KineticInputBlockEntity::new)
            .instance(() -> KineticInputInstance::new)
            .validBlocks(KINETIC_INPUT)
            .renderer(() -> KineticInputBlockEntityRenderer::new)
            .register();*/

    public static void register() {}
}
