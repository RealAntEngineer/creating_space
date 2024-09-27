package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.fluids.storage.CryogenicTankBlock;
import com.rae.creatingspace.content.fluids.effect.BurnBlock;
import com.rae.creatingspace.content.fluids.effect.FreezerBlock;
import com.rae.creatingspace.content.planets.RegolithSurfaceBlock;
import com.rae.creatingspace.content.planets.hologram.ProjectorBlock;
import com.rae.creatingspace.content.recipes.chemical_synthesis.CatalystCarrierBlock;
import com.rae.creatingspace.content.recipes.electrolysis.MechanicalElectrolyzerBlock;
import com.rae.creatingspace.content.recipes.air_liquefying.AirLiquefierBlock;
import com.rae.creatingspace.content.rocket.flight_recorder.FlightRecorderBlock;
import com.rae.creatingspace.content.rocket.engine.*;
import com.rae.creatingspace.content.rocket.engine.table.RocketEngineerTableBlock;
import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlock;
import com.rae.creatingspace.init.CreativeModeTabsInit;
import com.rae.creatingspace.init.graphics.SpriteShiftInit;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankBlock;
import com.rae.creatingspace.content.fluids.meter.FlowGaugeBlock;
import com.rae.creatingspace.legacy.server.blocks.RocketGeneratorBlock;
import com.rae.creatingspace.content.fluids.cassing.IsolatedFluidPipe;
import com.rae.creatingspace.content.fluids.cassing.IsolatedFluidPump;
import com.rae.creatingspace.legacy.server.blocks.atmosphere.OxygenBlock;
import com.rae.creatingspace.legacy.server.blocks.atmosphere.SealerBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.engines.BigEngineBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.engines.SmallEngineBlock;
import com.rae.creatingspace.content.rocket.engine.SuperEngineBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.BigRocketStructuralBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.SmallRocketStructuralBlock;
import com.rae.creatingspace.legacy.server.items.BigEngineItem;
import com.rae.creatingspace.legacy.server.items.SmallEngineItem;
import com.rae.creatingspace.server.contraption.behaviour.interaction.FlightRecorderInteraction;
import com.rae.creatingspace.server.contraption.behaviour.interaction.RocketControlInteraction;
import com.rae.creatingspace.server.contraption.behaviour.movement.EngineMovementBehaviour;
import com.rae.creatingspace.content.fluids.storage.CryogenicTankItem;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.fluids.PipeAttachmentModel;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.*;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.Registry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.Tags;
import org.checkerframework.checker.units.qual.C;

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
    /*public static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> TITANIUM = BLOCK_REGISTER.register(
            "titanium", () -> new Block(BlockBehaviour.Properties.of(Material.METAL)));*/
    public static final BlockEntry<ProjectorBlock> PROJECTOR = REGISTRATE
            .block("projector", ProjectorBlock::new)
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .build().register();
    public static final BlockEntry<RocketEngineerTableBlock> ROCKET_ENGINEER_TABLE = REGISTRATE
            .block("rocket_engineer_table", RocketEngineerTableBlock::new)
            .properties(p -> p.strength(1.0f).noOcclusion())
            .blockstate((c, p)-> p.horizontalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .build().register();
    public static final BlockEntry<SmallEngineBlock> SMALL_ROCKET_ENGINE = REGISTRATE
            .block("small_rocket_engine", SmallEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion())

            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .onRegister(movementBehaviour(new EngineMovementBehaviour()))
            .item(SmallEngineItem::new)
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            //.model((c,p)-> p.withExistingParent(c.getName(), CreatingSpace.resource("block/small_rocket_engine")))
            .build()
            .register();
    public static final BlockEntry<SuperEngineBlock> ROCKET_ENGINE = REGISTRATE
            .block("rocket_engine", SuperEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion())

            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), p.models().getExistingFile(CreatingSpace.resource("block/small_rocket_engine"))))
            .onRegister(movementBehaviour(new EngineMovementBehaviour()))
            .item(EngineItem::new)
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .model((c,p)-> p.withExistingParent(c.getName(), CreatingSpace.resource("block/small_rocket_engine")))
            .build()
            .register();
    public static final BlockEntry<BigEngineBlock> BIG_ROCKET_ENGINE = REGISTRATE
            .block("big_rocket_engine", BigEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion())
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .onRegister(movementBehaviour(new EngineMovementBehaviour()))
            .item(BigEngineItem::new)
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            //.model((c,p)-> p.withExistingParent(c.getName(), CreatingSpace.resource("block/big_rocket_engine")))
            .build()
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
            .build()
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
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .onRegister(interactionBehaviour(new RocketControlInteraction()))
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .build()
            .register();
    public static final BlockEntry<FlightRecorderBlock> FLIGHT_RECORDER = REGISTRATE.block(
                    "flight_recorder", FlightRecorderBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion().requiresCorrectToolForDrops())
            .blockstate(BlockStateGen.directionalAxisBlockProvider())
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
            .blockstate((c,p)-> p.getVariantBuilder(c.get())
                    .forAllStatesExcept(state ->
                        ConfiguredModel.builder().modelFile(
                                p.models().getExistingFile(
                        state.getValue(RocketGeneratorBlock.CHARGED)?
                                CreatingSpace.resource("block/rocket_generator/loaded"):
                                CreatingSpace.resource("block/rocket_generator/empty")))
                                .rotationY(((int) state.getValue(RocketGeneratorBlock.FACING).toYRot() + 180) % 360)
                                .build()
                    , BlockStateProperties.FACING, RocketGeneratorBlock.GENERATING))
            .transform(BlockStressDefaults.setCapacity(10000))
            .transform(BlockStressDefaults.setGeneratorSpeed(RocketGeneratorBlock::getSpeedRange))
            .transform(axeOrPickaxe())
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();

    /*@Deprecated
    public static final BlockEntry<ChemicalSynthesizerBlock> CHEMICAL_SYNTHESIZER = REGISTRATE.block(
                    "chemical_synthesizer", ChemicalSynthesizerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
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
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .item()
            //.properties(p-> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .onRegisterAfter(Registry.ITEM_REGISTRY, i -> ItemDescription.useKey(i, "block.creatingspace.legacy_mechanical_electrolyzer"))
            .register();*/
    public static final BlockEntry<MechanicalElectrolyzerBlock> MECHANICAL_ELECTROLYZER = REGISTRATE.block(
                    "mechanical_electrolyzer", MechanicalElectrolyzerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .transform(BlockStressDefaults.setImpact(2000))
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
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
            .blockstate(BlockStateGen.directionalAxisBlockProvider())
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .transform(customItemModel())
            .register();
    public static final BlockEntry<AirLiquefierBlock> AIR_LIQUEFIER = REGISTRATE.block(
                    "air_liquefier", AirLiquefierBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().requiresCorrectToolForDrops())
            .transform(BlockStressDefaults.setImpact(500))
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.directionalAxisBlockProvider())
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
            .build()
            .register();
    public static final BlockEntry<OxygenBlock> OXYGEN = REGISTRATE
            .block("oxygen", OxygenBlock::new)
            .initialProperties(() -> Blocks.AIR)
            .properties(p -> p.noOcclusion().noCollission().dynamicShape().air())
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                    .forAllStatesExcept(BlockStateGen.mapToAir(p), DirectionalBlock.FACING))
            .register();
    //transform .backtank() or similar to generate some correct shit
    public static final BlockEntry<OxygenBacktankBlock> COPPER_OXYGEN_BACKTANK = REGISTRATE
            .block("copper_oxygen_backtank", OxygenBacktankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::dynamicShape)
            .transform(pickaxeOnly())
            .blockstate((c,p)-> p.horizontalBlock(c.getEntry(),p.models().getExistingFile(CreatingSpace.resource("block/oxygen_backtank/copper"))))
            .loot((lt, block) -> lt.add(block, LootTable.lootTable().withPool(LootPool.lootPool()
                    .when(ExplosionCondition.survivesExplosion())
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ItemInit.COPPER_OXYGEN_BACKTANK.get())
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("Oxygen", "Oxygen"))
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("prevOxygen", "prevOxygen"))
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("Enchantments", "Enchantments"))))))
            .register();

    public static final BlockEntry<OxygenBacktankBlock> NETHERITE_OXYGEN_BACKTANK = REGISTRATE
            .block("netherite_oxygen_backtank", OxygenBacktankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::dynamicShape)
            .transform(pickaxeOnly())
            .blockstate((c,p)-> p.horizontalBlock(c.getEntry(),p.models().getExistingFile(CreatingSpace.resource("block/oxygen_backtank/netherite"))))
            .loot((lt, block) -> {
                LootTable.Builder builder = LootTable.lootTable();
                LootItemCondition.Builder survivesExplosion = ExplosionCondition.survivesExplosion();
                lt.add(block, builder.withPool(LootPool.lootPool()
                        .when(survivesExplosion)
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ItemInit.NETHERITE_OXYGEN_BACKTANK.get())
                                .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                        .copy("Oxygen", "Oxygen"))
                                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                        .copy("prevOxygen", "prevOxygen"))
                                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                        .copy("Enchantments", "Enchantments")))));
            })
            .register();

    public static final BlockEntry<CryogenicTankBlock> CRYOGENIC_TANK = REGISTRATE
            .block("cryogenic_tank", CryogenicTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .transform(pickaxeOnly())
            .blockstate((c,p)-> p.simpleBlock(c.getEntry(), p.models().getExistingFile(CreatingSpace.resource("block/cryogenic_tank"))))
            .item(CryogenicTankItem::new)
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB).stacksTo(1))
            .build()
            .register();


    public static final BlockEntry<Block> MOON_STONE = REGISTRATE
            .block("moon_stone",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();
    public static final BlockEntry<Block> MOON_STONE_BRICK = REGISTRATE
            .block("moon_stone_brick",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();
    public static final BlockEntry<Block> POLISHED_MOON_STONE = REGISTRATE
            .block("polished_moon_stone",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();
    public static final BlockEntry<Block> MOON_REGOLITH = REGISTRATE
            .block("moon_regolith",Block::new).initialProperties(()-> Blocks.DIRT)
            .properties(p-> p.strength(1.0f).sound(SoundType.SNOW).color(MaterialColor.SNOW))
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();

    public static final BlockEntry<RegolithSurfaceBlock> MOON_SURFACE_REGOLITH = REGISTRATE
            .block("moon_surface_regolith",RegolithSurfaceBlock::new).initialProperties(()-> Blocks.DIRT)
            .properties(p-> p.strength(1.0f).sound(SoundType.SNOW).color(MaterialColor.SNOW))
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();

    public static final BlockEntry<Block> MARS_STONE = REGISTRATE
            .block("mars_stone", Block::new).initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();
    public static final BlockEntry<Block> MARS_REGOLITH = REGISTRATE
            .block("mars_regolith", Block::new).initialProperties(() -> Blocks.DIRT)
            .properties(p -> p.strength(1.0f).sound(SoundType.SNOW).color(MaterialColor.TERRACOTTA_RED))
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();
    public static final BlockEntry<Block> MARS_SURFACE_REGOLITH = REGISTRATE
            .block("mars_surface_regolith", Block::new).initialProperties(() -> Blocks.DIRT)
            .properties(p -> p.strength(1.0f).sound(SoundType.SNOW).color(MaterialColor.TERRACOTTA_RED))
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
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
            .build()
            .register();

    public static final BlockEntry<Block> MOON_NICKEL_ORE = REGISTRATE.block(
                    "moon_nickel_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(3.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();

    public static final BlockEntry<Block> RAW_NICKEL_BLOCK = REGISTRATE.block(
                    "raw_nickel_block",Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();

    public static final BlockEntry<Block> MOON_COBALT_ORE = REGISTRATE.block(
                    "moon_cobalt_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(3.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();
    public static final BlockEntry<Block> RAW_COBALT_BLOCK = REGISTRATE.block(
                    "raw_cobalt_block",Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();
    public static final BlockEntry<Block> MOON_ALUMINUM_ORE = REGISTRATE.block(
                    "moon_aluminum_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(3.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();
    public static final BlockEntry<Block> RAW_ALUMINUM_BLOCK = REGISTRATE.block(
                    "raw_aluminum_block",Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p-> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();


    /*public static final BlockEntry<Block> TITANIUM_BLOCK = REGISTRATE.block(
                    "titanium_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();*/

    //machinery

    public static final BlockEntry<FreezerBlock> FREEZER_BLOCK = REGISTRATE.block(
                    "freezer_block", FreezerBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();

    public static final BlockEntry<BurnBlock> BURN_BLOCK = REGISTRATE.block(
                    "burn_block", BurnBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .build()
            .register();

    public static final BlockEntry<CasingBlock> ISOLATE_CASING = REGISTRATE.block("isolate_casing", CasingBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .properties(p -> p.tab(CreativeModeTabsInit.MACHINE_TAB))
            .build()
            .register();


    public static final BlockEntry<IsolatedFluidPipe> ISOLATED_FLUID_PIPE =
            REGISTRATE.block("isolated_fluid_pipe", p -> new IsolatedFluidPipe(p, BlockInit.ISOLATE_CASING::get))
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.noOcclusion().color(MaterialColor.TERRACOTTA_LIGHT_GRAY))
                    .transform(axeOrPickaxe())
                    .blockstate(BlockStateGen.encasedPipe())
                    .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(SpriteShiftInit.ISOLATE_CASING)))
                    .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, SpriteShiftInit.ISOLATE_CASING,
                            (s, f) -> !s.getValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)))))
                    //.onRegisterAfter(ForgeRegistries.BLOCKS.getRegistryKey(), b -> EncasingRegistry.addVariant(FLUID_PIPE.get(), b))
                    .onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::new))
                    //.transform(EncasingRegistry.addVariantTo(AllBlocks.FLUID_PIPE))
                    .register();

    public static final BlockEntry<IsolatedFluidPump> ISOLATED_FLUID_PUMP =
            REGISTRATE.block("isolated_fluid_pump", p -> new IsolatedFluidPump(p, BlockInit.ISOLATE_CASING::get))
                    .initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.noOcclusion().color(MaterialColor.TERRACOTTA_LIGHT_GRAY))
                    .transform(axeOrPickaxe())
                    //.blockstate(BlockStateGen.encasedPipe())
                    //.onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.COPPER_CASING)))
                    //.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.COPPER_CASING,
                    //        (s, f) -> !s.getValue(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)))))
                    //.onRegisterAfter(ForgeRegistries.BLOCKS.getRegistryKey(), b -> EncasingRegistry.addVariant(FLUID_PIPE.get(), b))
                    .onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::new))
                    //.transform(EncasingRegistry.addVariantTo(AllBlocks.FLUID_PIPE))
                    .register();


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
