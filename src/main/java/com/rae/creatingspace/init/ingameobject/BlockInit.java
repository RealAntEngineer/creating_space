package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.init.CreativeModeTabsInit;
import com.rae.creatingspace.init.graphics.SpriteShiftInit;
import com.rae.creatingspace.server.armor.OxygenBacktankBlock;
import com.rae.creatingspace.server.blocks.*;
import com.rae.creatingspace.server.blocks.atmosphere.OxygenBlock;
import com.rae.creatingspace.server.blocks.atmosphere.RoomPressuriserBlock;
import com.rae.creatingspace.server.blocks.atmosphere.SealerBlock;
import com.rae.creatingspace.server.blocks.multiblock.BigRocketStructuralBlock;
import com.rae.creatingspace.server.blocks.multiblock.SmallRocketStructuralBlock;
import com.rae.creatingspace.server.blocks.multiblock.SuperRocketStructuralBlock;
import com.rae.creatingspace.server.blocks.multiblock.engines.BigEngineBlock;
import com.rae.creatingspace.server.blocks.multiblock.engines.SmallEngineBlock;
import com.rae.creatingspace.server.blocks.multiblock.engines.SuperEngineBlock;
import com.rae.creatingspace.server.contraption.behaviour.interaction.FlightRecorderInteraction;
import com.rae.creatingspace.server.contraption.behaviour.interaction.RocketControlInteraction;
import com.rae.creatingspace.server.contraption.behaviour.movement.EngineMovementBehaviour;
import com.rae.creatingspace.server.items.CryogenicTankItem;
import com.rae.creatingspace.server.items.engine.BigEngineItem;
import com.rae.creatingspace.server.items.engine.EngineItem;
import com.rae.creatingspace.server.items.engine.SmallEngineItem;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.*;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.Registry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.Tags;

import static com.rae.creatingspace.CreatingSpace.REGISTRATE;
import static com.simibubi.create.AllInteractionBehaviours.interactionBehaviour;
import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;

public class BlockInit {
    //TODO setup correctly the no occlusion and dynamic shape to avoid lighting issues ?
    // + test if it's really the issue ( see QuadLighter )

    //just blocks
    //TODO add geode's blocks
    public static final BlockEntry<RocketEngineerTableBlock> ROCKET_ENGINEER_TABLE = REGISTRATE
            .block("rocket_engineer_table", RocketEngineerTableBlock::new)
            .properties(p -> p.strength(1.0f).noOcclusion())
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .build().register();
    public static final BlockEntry<SmallEngineBlock> SMALL_ROCKET_ENGINE = REGISTRATE
            .block("small_rocket_engine", SmallEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion())

            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
            .onRegister(movementBehaviour(new EngineMovementBehaviour()))
            .item(SmallEngineItem::new)
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<SuperEngineBlock> ROCKET_ENGINE = REGISTRATE
            .block("rocket_engine", SuperEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion())

            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
            .onRegister(movementBehaviour(new EngineMovementBehaviour()))
            .item(EngineItem::new)
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<BigEngineBlock> BIG_ROCKET_ENGINE = REGISTRATE
            .block("big_rocket_engine", BigEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion())
            .transform(axeOrPickaxe())
            .onRegister(movementBehaviour(new EngineMovementBehaviour()))
            .item(BigEngineItem::new)
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<BigRocketStructuralBlock> BIG_ENGINE_STRUCTURAL =
            REGISTRATE.block("big_engine_structure", BigRocketStructuralBlock::new)
                    //.initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.strength(1.0f))
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), BigRocketStructuralBlock.FACING))
                    .properties(p -> p.color(MaterialColor.DIRT))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .transform(axeOrPickaxe())
                    .register();
    public static final BlockEntry<SuperRocketStructuralBlock> ENGINE_STRUCTURAL =
            REGISTRATE.block("engine_structure", SuperRocketStructuralBlock::new)
                    //.initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.strength(1.0f))
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), SmallRocketStructuralBlock.FACING))
                    .properties(p -> p.color(MaterialColor.DIRT))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .transform(axeOrPickaxe())
                    .register();
    public static final BlockEntry<SmallRocketStructuralBlock> SMALL_ENGINE_STRUCTURAL =
            REGISTRATE.block("small_engine_structure", SmallRocketStructuralBlock::new)
                    //.initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.strength(1.0f))
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), SmallRocketStructuralBlock.FACING))
                    .properties(p -> p.color(MaterialColor.DIRT))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .transform(axeOrPickaxe())
                    .register();
    public static final BlockEntry<Block> CLAMPS = REGISTRATE
            .block("clamps",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p -> p.strength(1.0f))
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<CasingBlock> ROCKET_CASING = REGISTRATE
            .block("rocket_casing",CasingBlock::new)
            .properties(p-> p
                    .color(MaterialColor.COLOR_BLUE))
            .transform(BuilderTransformers.casing(() -> SpriteShiftInit.ROCKET_CASING))
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .build()
            .register();
    public static final BlockEntry<RocketControlsBlock> ROCKET_CONTROLS = REGISTRATE.block(
                    "rocket_controls", RocketControlsBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion().requiresCorrectToolForDrops())
            .transform(axeOrPickaxe())
            .onRegister(interactionBehaviour(new RocketControlInteraction()))
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<FlightRecorderBlock> FLIGHT_RECORDER = REGISTRATE.block(
                    "flight_recorder", FlightRecorderBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion().requiresCorrectToolForDrops())
            .transform(axeOrPickaxe())
            .onRegister(interactionBehaviour(new FlightRecorderInteraction()))
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<RocketGeneratorBlock> ROCKET_GENERATOR = REGISTRATE.block(
                    "rocket_generator", RocketGeneratorBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .transform(BlockStressDefaults.setCapacity(10000))
            .transform(BlockStressDefaults.setGeneratorSpeed(RocketGeneratorBlock::getSpeedRange))
            .transform(axeOrPickaxe())
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();

    @Deprecated
    public static final BlockEntry<ChemicalSynthesizerBlock> CHEMICAL_SYNTHESIZER = REGISTRATE.block(
                    "chemical_synthesizer", ChemicalSynthesizerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .transform(axeOrPickaxe())
            .item()
            //.properties(p-> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();

    @Deprecated
    public static final BlockEntry<LegacyMechanicalElectrolyzerBlock> LEGACY_MECHANICAL_ELECTROLYZER = REGISTRATE.block(
                    "legacy_mechanical_electrolyzer", LegacyMechanicalElectrolyzerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .transform(BlockStressDefaults.setImpact(10000))
            .transform(axeOrPickaxe())
            .item()
            //.properties(p-> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .onRegisterAfter(Registry.ITEM_REGISTRY, i -> ItemDescription.useKey(i, "block.creatingspace.legacy_mechanical_electrolyzer"))
            .register();
    public static final BlockEntry<MechanicalElectrolyzerBlock> MECHANICAL_ELECTROLYZER = REGISTRATE.block(
                    "mechanical_electrolyzer", MechanicalElectrolyzerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .transform(BlockStressDefaults.setImpact(2000))
            .transform(axeOrPickaxe())
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .onRegisterAfter(Registry.ITEM_REGISTRY, i -> ItemDescription.useKey(i, "block.creatingspace.mechanical_electrolyzer"))
            .register();
    public static final BlockEntry<CatalystCarrierBlock> CATALYST_CARRIER = REGISTRATE.block(
                    "catalyst_carrier", CatalystCarrierBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().color(MaterialColor.PODZOL))
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .transform(BlockStressDefaults.setImpact(8.0))
            .item(AssemblyOperatorBlockItem::new)
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SealerBlock> OXYGEN_SEALER = REGISTRATE
            .block("oxygen_sealer", SealerBlock::new)
            .properties(p -> p.strength(1.0f).dynamicShape().requiresCorrectToolForDrops())
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .build()
            .register();
    public static final BlockEntry<AirLiquefierBlock> AIR_LIQUEFIER = REGISTRATE.block(
                    "air_liquefier", AirLiquefierBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().requiresCorrectToolForDrops())
            .transform(BlockStressDefaults.setImpact(500))
            .transform(axeOrPickaxe())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<FlowGaugeBlock> FLOW_METER = REGISTRATE
            .block("flow_meter", FlowGaugeBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .transform(axeOrPickaxe())
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<OxygenBlock> OXYGEN = REGISTRATE
            .block("oxygen", OxygenBlock::new)
            .initialProperties(() -> Blocks.AIR)
            .properties(p -> p.noOcclusion().noCollission().dynamicShape().air())
            .register();
    public static final BlockEntry<OxygenBacktankBlock> COPPER_OXYGEN_BACKTANK = REGISTRATE
            .block("copper_oxygen_backtank", OxygenBacktankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::dynamicShape)
            .transform(pickaxeOnly())
            .register();

    public static final BlockEntry<OxygenBacktankBlock> NETHERITE_OXYGEN_BACKTANK = REGISTRATE
            .block("netherite_oxygen_backtank", OxygenBacktankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::dynamicShape)
            .transform(pickaxeOnly())
            .register();

    public static final BlockEntry<CryogenicTankBlock> CRYOGENIC_TANK = REGISTRATE
            .block("cryogenic_tank", CryogenicTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .transform(pickaxeOnly())
            .item(CryogenicTankItem::new)
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB).stacksTo(1))
            .build()
            .register();


    public static final BlockEntry<Block> MOON_STONE = REGISTRATE
            .block("moon_stone",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<Block> MOON_STONE_BRICK = REGISTRATE
            .block("moon_stone_brick",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<Block> POLISHED_MOON_STONE = REGISTRATE
            .block("polished_moon_stone",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<Block> MOON_REGOLITH = REGISTRATE
            .block("moon_regolith",Block::new).initialProperties(()-> Blocks.DIRT)
            .properties(p-> p.strength(1.0f).sound(SoundType.SNOW))
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<RegolithSurfaceBlock> MOON_SURFACE_REGOLITH = REGISTRATE
            .block("moon_surface_regolith",RegolithSurfaceBlock::new).initialProperties(()-> Blocks.DIRT)
            .properties(p-> p.strength(1.0f).sound(SoundType.SNOW))
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> MARS_STONE = REGISTRATE
            .block("mars_stone", Block::new).initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<Block> MARS_REGOLITH = REGISTRATE
            .block("mars_regolith", Block::new).initialProperties(() -> Blocks.DIRT)
            .properties(p -> p.strength(1.0f).sound(SoundType.SNOW))
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<Block> MARS_SURFACE_REGOLITH = REGISTRATE
            .block("mars_surface_regolith", Block::new).initialProperties(() -> Blocks.DIRT)
            .properties(p -> p.strength(1.0f).sound(SoundType.SNOW))
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();

    //ores
    public static final BlockEntry<Block> NICKEL_ORE = REGISTRATE.block(
                    "nickel_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(3.0f).requiresCorrectToolForDrops())
            .tag(Tags.Blocks.ORES)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .transform(tagBlockAndItem("ores/nickel", "ores_in_ground/stone"))
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();


    public static final BlockEntry<Block> DEEPSLATE_NICKEL_ORE = REGISTRATE.block(
                    "deepslate_nickel_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(4.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> MOON_NICKEL_ORE = REGISTRATE.block(
                    "moon_nickel_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(3.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> RAW_NICKEL_BLOCK = REGISTRATE.block(
                    "raw_nickel_block",Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> MOON_COBALT_ORE = REGISTRATE.block(
                    "moon_cobalt_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(3.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<Block> RAW_COBALT_BLOCK = REGISTRATE.block(
                    "raw_cobalt_block",Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<Block> MOON_ALUMINUM_ORE = REGISTRATE.block(
                    "moon_aluminum_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(3.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<Block> RAW_ALUMINUM_BLOCK = REGISTRATE.block(
                    "raw_aluminum_block",Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel())
            .register();

    //machinery





    public static final BlockEntry<AmethystBlock> CRYSTAL_BLOCK = REGISTRATE.block(
                    "crystal_block", AmethystBlock::new)
            .initialProperties(() -> Blocks.AMETHYST_BLOCK)
            .properties(p -> p.strength(1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops())
            .item()
            .build()
            .register();
    public static final BlockEntry<BuddingAmethystBlock> BUDDING_CRYSTAL = REGISTRATE.block(
                    "budding_crystal", BuddingAmethystBlock::new)
            .initialProperties(() -> Blocks.BUDDING_AMETHYST)
            .properties(p -> p.strength(1.5F).randomTicks().sound(SoundType.AMETHYST).requiresCorrectToolForDrops())
            .item()
            .build()
            .register();
    public static final BlockEntry<AmethystClusterBlock> CRYSTAL_CLUSTER = REGISTRATE.block(
                    "crystal_cluster", p -> new AmethystClusterBlock(7, 3, p))
            .initialProperties(() -> Blocks.AMETHYST_CLUSTER)
            .properties(p -> p.strength(1.5F)
                    .randomTicks().sound(SoundType.AMETHYST_CLUSTER)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 5))
            .item()
            .build()
            .register();
    public static final BlockEntry<AmethystClusterBlock> LARGE_CRYSTAL_BUD = REGISTRATE.block(
                    "large_crystal_bud", p -> new AmethystClusterBlock(5, 3, p))
            .initialProperties(() -> Blocks.AMETHYST_CLUSTER)
            .properties(p -> p.strength(1.5F)
                    .randomTicks().sound(SoundType.LARGE_AMETHYST_BUD)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 4))
            .item()
            .build()
            .register();
    public static final BlockEntry<AmethystClusterBlock> MEDIUM_CRYSTAL_BUD = REGISTRATE.block(
                    "medium_crystal_bud", p -> new AmethystClusterBlock(5, 3, p))
            .initialProperties(() -> Blocks.AMETHYST_CLUSTER)
            .properties(p -> p.strength(1.5F)
                    .randomTicks().sound(SoundType.MEDIUM_AMETHYST_BUD)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 2))
            .item()
            .build()
            .register();
    public static final BlockEntry<AmethystClusterBlock> SMALL_CRYSTAL_BUD = REGISTRATE.block(
                    "small_crystal_bud", p -> new AmethystClusterBlock(5, 3, p))
            .initialProperties(() -> Blocks.AMETHYST_CLUSTER)
            .properties(p -> p.strength(1.5F)
                    .randomTicks().sound(SoundType.SMALL_AMETHYST_BUD)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 1))
            .item()
            .build()
            .register();
    public static void register() {}

}
