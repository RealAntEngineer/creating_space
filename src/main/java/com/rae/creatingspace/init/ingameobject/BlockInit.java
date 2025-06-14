package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.configs.CSStress;
import com.rae.creatingspace.content.fluids.storage.CryogenicTankBlock;
import com.rae.creatingspace.content.fluids.meter.FlowGaugeBlock;
import com.rae.creatingspace.content.life_support.sealer.RoomPressuriserBlock;
import com.rae.creatingspace.content.planets.RegolithSurfaceBlock;
import com.rae.creatingspace.content.recipes.air_liquefying.AirLiquefierBlock;
import com.rae.creatingspace.content.recipes.chemical_synthesis.CatalystCarrierBlock;
import com.rae.creatingspace.content.recipes.electrolysis.MechanicalElectrolyzerBlock;
import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlock;
import com.rae.creatingspace.content.rocket.engine.table.RocketEngineerTableBlock;
import com.rae.creatingspace.content.rocket.flight_recorder.FlightRecorderBlock;
import com.rae.creatingspace.init.CreativeModeTabsInit;
import com.rae.creatingspace.init.graphics.SpriteShiftInit;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.BigRocketStructuralBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.SmallRocketStructuralBlock;
import com.rae.creatingspace.content.rocket.engine.SuperRocketStructuralBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.engines.BigEngineBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.engines.SmallEngineBlock;
import com.rae.creatingspace.content.rocket.engine.SuperEngineBlock;
import com.rae.creatingspace.content.rocket.contraption.behaviour.interaction.FlightRecorderInteraction;
import com.rae.creatingspace.content.rocket.contraption.behaviour.interaction.RocketControlInteraction;
import com.rae.creatingspace.content.rocket.contraption.behaviour.movement.EngineMovementBehaviour;
import com.rae.creatingspace.content.fluids.storage.CryogenicTankItem;
import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsItem;
import com.rae.creatingspace.legacy.server.items.BigEngineItem;
import com.rae.creatingspace.content.rocket.engine.EngineItem;
import com.rae.creatingspace.legacy.server.items.SmallEngineItem;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.*;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.Tags;

import static com.rae.creatingspace.CreatingSpace.REGISTRATE;
import static com.rae.creatingspace.CreatingSpace.resource;
import static com.simibubi.create.AllTags.commonItemTag;
import static com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour.interactionBehaviour;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;
import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;

public class BlockInit {
    static{
        REGISTRATE.setCreativeTab(CreativeModeTabsInit.MACHINE_TAB);
    }
    //just blocks
    //TODO add geode's blocks
    public static final BlockEntry<RocketEngineerTableBlock> ROCKET_ENGINEER_TABLE = REGISTRATE
            .block("rocket_engineer_table", RocketEngineerTableBlock::new)
            .properties(p -> p.strength(1.0f).noOcclusion())
            .blockstate((c, p)-> p.horizontalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .item()
            .build().register();
    public static final BlockEntry<SmallEngineBlock> SMALL_ROCKET_ENGINE = REGISTRATE
            .block("small_rocket_engine", SmallEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion())

            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
            .onRegister(movementBehaviour(new EngineMovementBehaviour()))
            .item(SmallEngineItem::new)
            .transform(customItemModel("1_2_1_block"))
            .register();
    public static final BlockEntry<SuperEngineBlock> ROCKET_ENGINE = REGISTRATE
            .block("rocket_engine", SuperEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion())
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), p.models().getExistingFile(resource("block/small_rocket_engine"))))
            .onRegister(movementBehaviour(new EngineMovementBehaviour()))
            .item(EngineItem::new)
            .transform(customItemModel("small_rocket_engine"))
            .register();
    public static final BlockEntry<BigEngineBlock> BIG_ROCKET_ENGINE = REGISTRATE
            .block("big_rocket_engine", BigEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p-> p.strength(1.0f).dynamicShape().noOcclusion())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
            .transform(axeOrPickaxe())
            .onRegister(movementBehaviour(new EngineMovementBehaviour()))
            .item(BigEngineItem::new)
            .transform(customItemModel("big_rocket_engine"))
            .register();

    public static final BlockEntry<BigRocketStructuralBlock> BIG_ENGINE_STRUCTURAL =
            REGISTRATE.block("big_engine_structure", BigRocketStructuralBlock::new)
                    //.initialProperties(SharedProperties::copperMetal)
                    .properties(p-> p.strength(1.0f))
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), BigRocketStructuralBlock.FACING))
                    .properties(p -> p.mapColor(MapColor.COLOR_BLUE))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .transform(axeOrPickaxe())
                    .register();
    public static final BlockEntry<SuperRocketStructuralBlock> ENGINE_STRUCTURAL =
            REGISTRATE.block("engine_structure", SuperRocketStructuralBlock::new)
                    //.initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.strength(1.0f))
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), SmallRocketStructuralBlock.FACING))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .transform(axeOrPickaxe())
                    .register();
    public static final BlockEntry<SmallRocketStructuralBlock> SMALL_ENGINE_STRUCTURAL =
            REGISTRATE.block("small_engine_structure", SmallRocketStructuralBlock::new)
                    //.initialProperties(SharedProperties::copperMetal)
                    .properties(p-> p.strength(1.0f))
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .forAllStatesExcept(BlockStateGen.mapToAir(p), SmallRocketStructuralBlock.FACING))
                    .properties(p -> p.mapColor(MapColor.METAL))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .transform(axeOrPickaxe())
                    .register();
    public static final BlockEntry<Block> CLAMPS = REGISTRATE
            .block("clamps",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p -> p.strength(1.0f))
            .item()
            .transform(customItemModel("clamps"))
            .register();
    public static final BlockEntry<CasingBlock> ROCKET_CASING = REGISTRATE
            .block("rocket_casing",CasingBlock::new)
            .transform(BuilderTransformers.casing(() -> SpriteShiftInit.ROCKET_CASING))
            .item()
            .build()
            .register();
    public static final BlockEntry<RocketControlsBlock> ROCKET_CONTROLS = REGISTRATE.block(
                    "rocket_controls", RocketControlsBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion().requiresCorrectToolForDrops())
            .transform(axeOrPickaxe())
            .onRegister(interactionBehaviour(new RocketControlInteraction()))
            .item(RocketControlsItem::new)
            .transform(customItemModel("rocket_controls"))
            .register();
    public static final BlockEntry<FlightRecorderBlock> FLIGHT_RECORDER = REGISTRATE.block(
                    "flight_recorder", FlightRecorderBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p-> p.strength(1.0f).dynamicShape().noOcclusion().requiresCorrectToolForDrops())
            .blockstate(BlockStateGen.directionalAxisBlockProvider())
            .transform(axeOrPickaxe())
            .onRegister(interactionBehaviour(new FlightRecorderInteraction()))
            .item()
            .transform(customItemModel())
            .register();


    public static final BlockEntry<MechanicalElectrolyzerBlock> MECHANICAL_ELECTROLYZER = REGISTRATE.block(
                    "mechanical_electrolyzer", MechanicalElectrolyzerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .transform(CSStress.setImpact(2000))
            .transform(axeOrPickaxe())
            .item()
            .transform(customItemModel())
            .onRegisterAfter(Registries.ITEM, i -> ItemDescription.useKey(i, "block.creatingspace.mechanical_electrolyzer"))
            .register();
    public static final BlockEntry<CatalystCarrierBlock> CATALYST_CARRIER = REGISTRATE.block(
                    "catalyst_carrier", CatalystCarrierBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion())
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .transform(CSStress.setImpact(8.0))
            .item(AssemblyOperatorBlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<RoomPressuriserBlock> OXYGEN_SEALER = REGISTRATE
            .block("oxygen_sealer", RoomPressuriserBlock::new)
            .properties(p -> p.strength(1.0f).dynamicShape().requiresCorrectToolForDrops())
            .blockstate(BlockStateGen.directionalAxisBlockProvider())
            .item()
            .transform(customItemModel())
       //     .build()
            .register();
    public static final BlockEntry<AirLiquefierBlock> AIR_LIQUEFIER = REGISTRATE.block(
                    "air_liquefier", AirLiquefierBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p-> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .blockstate(BlockStateGen.directionalAxisBlockProvider())
            .transform(CSStress.setImpact(500))
            .transform(axeOrPickaxe())
            .item()
            .transform(customItemModel())
            .register();
    public static final BlockEntry<FlowGaugeBlock> FLOW_METER = REGISTRATE
            .block("flow_meter", FlowGaugeBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                    .forAllStatesExcept(BlockStateGen.mapToAir(p), FlowGaugeBlock.FACING))
            .transform(axeOrPickaxe())
            .item()
            .transform(customItemModel("flow_meter/block"))
            .register();

    public static final BlockEntry<OxygenBacktankBlock> COPPER_OXYGEN_BACKTANK = REGISTRATE
            .block("copper_oxygen_backtank", OxygenBacktankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .blockstate((c,p)-> p.horizontalBlock(c.getEntry(),p.models().getExistingFile(resource("block/oxygen_backtank/copper"))))
            .properties(BlockBehaviour.Properties::dynamicShape)
            .transform(pickaxeOnly())
            .register();

    public static final BlockEntry<OxygenBacktankBlock> NETHERITE_OXYGEN_BACKTANK = REGISTRATE
            .block("netherite_oxygen_backtank", OxygenBacktankBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .blockstate((c,p)-> p.horizontalBlock(c.getEntry(),p.models().getExistingFile(resource("block/oxygen_backtank/netherite"))))
            .properties(BlockBehaviour.Properties::dynamicShape)
            .transform(pickaxeOnly())
            .register();

    public static final BlockEntry<CryogenicTankBlock> CRYOGENIC_TANK = REGISTRATE
            .block("cryogenic_tank", CryogenicTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .blockstate((c,p)-> p.simpleBlock(c.getEntry(), p.models().getExistingFile(resource("block/cryogenic_tank"))))
            .transform(pickaxeOnly())
            .item(CryogenicTankItem::new)
            .build()
            .register();

    static{
        REGISTRATE.setCreativeTab(CreativeModeTabsInit.MINERALS_TAB);
    }

    public static final BlockEntry<Block> MOON_STONE = REGISTRATE
            .block("moon_stone",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            .transform(customItemModel("moon_stone"))
            .register();
    public static final BlockEntry<Block> MOON_STONE_BRICK = REGISTRATE
            .block("moon_stone_brick",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            .transform(customItemModel("moon_stone_brick"))
            .register();
    public static final BlockEntry<Block> POLISHED_MOON_STONE = REGISTRATE
            .block("polished_moon_stone",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .item()
            .transform(customItemModel("polished_moon_stone"))
            .register();
    public static final BlockEntry<Block> MOON_REGOLITH = REGISTRATE
            .block("moon_regolith",Block::new).initialProperties(()-> Blocks.DIRT)
            .properties(p-> p.strength(1.0f).sound(SoundType.SNOW))
            .item()
            .transform(customItemModel("moon_regolith"))
            .register();

    public static final BlockEntry<RegolithSurfaceBlock> MOON_SURFACE_REGOLITH = REGISTRATE
            .block("moon_surface_regolith",RegolithSurfaceBlock::new).initialProperties(()-> Blocks.DIRT)
            .properties(p-> p.strength(1.0f).sound(SoundType.SNOW).mapColor(MapColor.SNOW))
            .item()
            .transform(customItemModel("moon_surface_regolith"))
            .register();

    public static final BlockEntry<Block> MARS_STONE = REGISTRATE
            .block("mars_stone", Block::new).initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(1.0f).requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel("mars_stone"))
            .register();
    public static final BlockEntry<Block> MARS_REGOLITH = REGISTRATE
            .block("mars_regolith", Block::new).initialProperties(() -> Blocks.DIRT)
            .properties(p -> p.strength(1.0f).sound(SoundType.SNOW))
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel("mars_regolith"))
            .register();
    public static final BlockEntry<Block> MARS_SURFACE_REGOLITH = REGISTRATE
            .block("mars_surface_regolith", Block::new).initialProperties(() -> Blocks.DIRT)
            .properties(p -> p.strength(1.0f).sound(SoundType.SNOW))
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel("mars_surface_regolith"))
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
            .build()
            .register();


    public static final BlockEntry<Block> DEEPSLATE_NICKEL_ORE = REGISTRATE.block(
                    "deepslate_nickel_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(4.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .transform(tagBlockAndItem("ores/nickel", "ores_in_ground/stone"))
            .build()
            .register();

    public static final BlockEntry<Block> MOON_NICKEL_ORE = REGISTRATE.block(
                    "moon_nickel_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(3.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .transform(customItemModel("moon_nickel_ore"))
            .register();

    public static final BlockEntry<Block> RAW_NICKEL_BLOCK = REGISTRATE.block(
                    "raw_nickel_block",Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .transform(TagGen.pickaxeOnly())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .recipe((c,p) ->
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                    .define('#', commonItemTag("raw_materials/nickel"))
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .unlockedBy("has_" + c.getName(), has(c.get()))
                    .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/raw_nickel"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    public static final BlockEntry<Block> NICKEL_BLOCK = REGISTRATE.block("nickel_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("ingots/nickel"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/nickel"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            //.lang("Block of Nickel")
            .register();



    public static final BlockEntry<Block> MOON_COBALT_ORE = REGISTRATE.block(
                    "moon_cobalt_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(3.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .transform(customItemModel("moon_cobalt_ore"))
            .register();
    public static final BlockEntry<Block> RAW_COBALT_BLOCK = REGISTRATE.block(
                    "raw_cobalt_block",Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .transform(TagGen.pickaxeOnly())
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("raw_materials/cobalt"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/raw_cobalt"))
            .transform(customItemModel("raw_cobalt_block"))
            .register();
    public static final BlockEntry<Block> MOON_ALUMINUM_ORE = REGISTRATE.block(
                    "moon_aluminum_ore", Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(3.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .item()
            .transform(customItemModel("moon_aluminum_ore"))
            .register();
    public static final BlockEntry<Block> RAW_ALUMINUM_BLOCK = REGISTRATE.block(
                    "raw_aluminum_block",Block::new)
            .initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("raw_materials/aluminum"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/raw_aluminum"))
            .transform(customItemModel("raw_aluminum_block"))
            .register();

    public static final BlockEntry<Block> ALUMINUM_BLOCK = REGISTRATE.block("aluminum_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("ingots/aluminum"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/aluminum"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            //.lang("Block of Aluminum")
            .register();

    public static final BlockEntry<Block> COBALT_BLOCK = REGISTRATE.block("cobalt_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("ingots/cobalt"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/cobalt"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            //.lang("Block of Cobalt")
            .register();

    public static final BlockEntry<Block> COPRONICKEL_BLOCK = REGISTRATE.block(
                    "copronickel_block",Block::new)
            .initialProperties(()-> Blocks.IRON_BLOCK)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("ingots/copronickel"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/copronickel"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            //.lang("Block of Copronickel")
            .register();

    public static final BlockEntry<Block> REINFORCED_COPPER_BLOCK = REGISTRATE.block(
                    "reinforced_copper_block",Block::new)
            .initialProperties(()-> Blocks.IRON_BLOCK)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("ingots/reinforced_copper"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/reinforced_copper"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            //.lang("Block of Reinforced Copper")
            .register();

    public static final BlockEntry<Block> INCONEL_BLOCK = REGISTRATE.block(
                    "inconel_block",Block::new)
            .initialProperties(()-> Blocks.IRON_BLOCK)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("ingots/inconel"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/inconel"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            //.lang("Block of Inconel")
            .register();

    public static final BlockEntry<Block> HASTELLOY_BLOCK = REGISTRATE.block(
                    "hastelloy_block",Block::new)
            .initialProperties(()-> Blocks.IRON_BLOCK)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("ingots/hastelloy"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/hastelloy"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            //.lang("Block of Hastelloy")
            .register();

    public static final BlockEntry<Block> MONEL_BLOCK = REGISTRATE.block(
                    "monel_block",Block::new)
            .initialProperties(()-> Blocks.IRON_BLOCK)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("ingots/monel"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(tagBlockAndItem("storage_blocks/monel"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            //.lang("Block of Monel")
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
