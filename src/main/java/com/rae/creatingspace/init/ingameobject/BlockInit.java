package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.configs.CSStress;
import com.rae.creatingspace.content.fluids.storage.CryogenicTankBlock;
import com.rae.creatingspace.content.fluids.meter.FlowGaugeBlock;
import com.rae.creatingspace.content.life_support.sealer.RoomPressuriserBlock;
import com.rae.creatingspace.content.planets.BuddingCrystalBlock;
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

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
//import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.*;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.neoforged.neoforge.common.Tags;

import java.util.Map;

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
    public static final BlockEntry<RocketEngineerTableBlock> ROCKET_ENGINEER_TABLE = REGISTRATE
            .block("rocket_engineer_table", RocketEngineerTableBlock::new)
            .properties(p -> p.strength(1.0f).noOcclusion())
            .blockstate((c, p)-> p.horizontalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                        .define('W', ItemTags.WOODEN_SLABS)
                        .define('S', Items.SMOOTH_STONE)
                        .pattern("WWW")
                        .pattern("WWW")
                        .pattern(" S ")
                        .unlockedBy("has_" + c.getName(), has(c.get()))
                        .save(p, resource("crafting/machines/" + c.getName())))
            .item()
            .build().register();

    public static final BlockEntry<SmallEngineBlock> SMALL_ROCKET_ENGINE = REGISTRATE
            .block("small_rocket_engine", SmallEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion())
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
            .onRegister(movementBehaviour(new EngineMovementBehaviour()))
            .item(SmallEngineItem::new)
            .transform(customItemModel("small_rocket_engine"))
            .register();

    public static final BlockEntry<SuperEngineBlock> ROCKET_ENGINE = REGISTRATE
            .block("rocket_engine", SuperEngineBlock::new)
            //.initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion())
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
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
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
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
                    .blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models()
                            .getExistingFile(p.modLoc("block/structural/big_engine"))))
                    .properties(p -> p.mapColor(MapColor.COLOR_BLUE))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
                    .transform(axeOrPickaxe())
                    .register();

    public static final BlockEntry<SuperRocketStructuralBlock> ENGINE_STRUCTURAL =
            REGISTRATE.block("engine_structure", SuperRocketStructuralBlock::new)
                    //.initialProperties(SharedProperties::copperMetal)
                    .properties(p -> p.strength(1.0f))
                    .blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models()
                            .getExistingFile(p.modLoc("block/structural/super_engine"))))                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
                    .transform(axeOrPickaxe())
                    .register();

    public static final BlockEntry<SmallRocketStructuralBlock> SMALL_ENGINE_STRUCTURAL =
            REGISTRATE.block("small_engine_structure", SmallRocketStructuralBlock::new)
                    //.initialProperties(SharedProperties::copperMetal)
                    .properties(p-> p.strength(1.0f))
                    .blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models()
                            .getExistingFile(p.modLoc("block/structural/small_engine"))))
                    .properties(p -> p.mapColor(MapColor.METAL))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
                    .transform(axeOrPickaxe())
                    .register();

    public static final BlockEntry<Block> CLAMPS = REGISTRATE
            .block("clamps",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p -> p.strength(1.0f))
            .tag(AllTags.AllBlockTags.NON_MOVABLE.tag)
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 4)
                            .define('I', Blocks.IRON_BLOCK)
                            .define('C', AllBlocks.COPPER_CASING.get())
                            .pattern("ICI")
                            .pattern("CIC")
                            .pattern("ICI")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/machines/" + c.getName())))
            .item()
            .transform(customItemModel("clamps"))
            .register();

    public static final BlockEntry<CasingBlock> ROCKET_CASING = REGISTRATE
            .block("rocket_casing", CasingBlock::new)
            .transform(BuilderTransformers.casing(() -> SpriteShiftInit.ROCKET_CASING))
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c, p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('S', commonItemTag("plates/aluminum"))
                            .define('C', commonItemTag("ingots/cobalt"))
                            .pattern("CSC")
                            .pattern("SCS")
                            .pattern("CSC")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/machines/" + c.getName())))
            .item()
            .build()
            .register();

    public static final BlockEntry<RocketControlsBlock> ROCKET_CONTROLS = REGISTRATE.block(
                    "rocket_controls", RocketControlsBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .properties(p -> p.strength(1.0f).dynamicShape().noOcclusion().requiresCorrectToolForDrops())
            .transform(axeOrPickaxe())
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .onRegister(interactionBehaviour(new RocketControlInteraction()))
            .recipe((c,p) -> {
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('E', AllItems.ELECTRON_TUBE.get())
                            .define('R', AllBlocks.REDSTONE_LINK.get())
                            .define('T', AllBlocks.TRAIN_CONTROLS.get())
                            .define('S', AllItems.STURDY_SHEET.get())
                            .pattern("ERE")
                            .pattern("ETE")
                            .pattern("SSS")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/machines/" + c.getName()));
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 1)
                            .requires(c.get())
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName() + "_reset"));
            })
            .item(RocketControlsItem::new)
            .transform(customItemModel("rocket_controls"))
            .register();

    public static final BlockEntry<FlightRecorderBlock> FLIGHT_RECORDER = REGISTRATE.block(
                    "flight_recorder", FlightRecorderBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p-> p.strength(1.0f).dynamicShape().noOcclusion().requiresCorrectToolForDrops())
            .blockstate(BlockStateGen.directionalAxisBlockProvider())
            .transform(axeOrPickaxe())
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .onRegister(interactionBehaviour(new FlightRecorderInteraction()))
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('B', AllBlocks.BRASS_CASING.get())
                            .define('A', AllBlocks.SHAFT.get())
                            .define('K', Items.DRIED_KELP_BLOCK)
                            .pattern(" B ")
                            .pattern("AKA")
                            .pattern(" B ")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/machines/" + c.getName())))
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
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('C', ItemInit.COPPER_COIL.get())
                            .define('X', AllBlocks.COPPER_CASING.get())
                            .define('T', AllBlocks.FLUID_TANK.get())
                            .define('S', AllBlocks.SHAFT.get())
                            .define('G', commonItemTag("plates/gold"))
                            .pattern("XSX")
                            .pattern("CCC")
                            .pattern("GTG")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/machines/" + c.getName())))
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
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('N', commonItemTag("ingots/nickel"))
                            .define('G', commonItemTag("plates/gold"))
                            .define('P', AllBlocks.MECHANICAL_PRESS.get())
                            .pattern(" G ")
                            .pattern("NPN")
                            .pattern(" G ")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/machines/" + c.getName())))
            .item(AssemblyOperatorBlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<RoomPressuriserBlock> OXYGEN_SEALER = REGISTRATE
            .block("oxygen_sealer", RoomPressuriserBlock::new)
            .properties(p -> p.strength(1.0f).dynamicShape().requiresCorrectToolForDrops())
            .blockstate(BlockStateGen.directionalAxisBlockProvider())
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('P', AllItems.PROPELLER.get())
                            .define('C', AllBlocks.COPPER_CASING.get())
                            .define('T', AllBlocks.FLUID_TANK.get())
                            .define('N', commonItemTag("plates/nickel"))
                            .pattern("NPN")
                            .pattern("CTC")
                            .pattern("CCC")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/machines/" + c.getName())))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<AirLiquefierBlock> AIR_LIQUEFIER = REGISTRATE.block(
                    "air_liquefier", AirLiquefierBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p-> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .blockstate(BlockStateGen.directionalAxisBlockProvider())
            .transform(CSStress.setImpact(500))
            .transform(axeOrPickaxe())
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('S', commonItemTag("plates/brass"))
                            .define('T', AllBlocks.FLUID_TANK.get())
                            .define('C', AllBlocks.BRASS_CASING.get())
                            .define('P', AllBlocks.ENCASED_FAN.get())
                            .pattern(" S ")
                            .pattern("CPC")
                            .pattern(" T ")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/machines/" + c.getName())))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<FlowGaugeBlock> FLOW_METER = REGISTRATE
            .block("flow_meter", FlowGaugeBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.strength(1.0f).noOcclusion().requiresCorrectToolForDrops())
            .blockstate((c,p)-> p.horizontalBlock(c.getEntry(),p.models().getExistingFile(resource("block/flow_meter/block"))))
            .transform(axeOrPickaxe())
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('C', AllBlocks.COPPER_CASING.get())
                            .define('G', Items.COMPASS)
                            .pattern("G")
                            .pattern("C")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/machines/" + c.getName())))
            .item()
            .transform(customItemModel("flow_meter/block"))
            .register();

    public static final BlockEntry<OxygenBacktankBlock> COPPER_OXYGEN_BACKTANK = REGISTRATE
            .block("copper_oxygen_backtank", OxygenBacktankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .blockstate((c,p)-> p.horizontalBlock(c.getEntry(),p.models().getExistingFile(resource("block/oxygen_backtank/copper"))))
            .properties(BlockBehaviour.Properties::dynamicShape)
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c,p) ->
                    MechanicalCraftingRecipeBuilder.shapedRecipe(c.get(), 1)
                            .key('F', ItemInit.BASIC_SPACESUIT_FABRIC.get())
                            .key('N', commonItemTag("plates/nickel"))
                            .key('W', Items.RED_WOOL)
                            .key('T', AllBlocks.FLUID_TANK.get())
                            .patternLine(" F F ")
                            .patternLine("WTNTW")
                            .patternLine(" FWF ")
                            .build(p, resource("mechanical_crafting/armor/" + c.getName())))
            .transform(pickaxeOnly())
            .register();

    public static final BlockEntry<OxygenBacktankBlock> NETHERITE_OXYGEN_BACKTANK = REGISTRATE
            .block("netherite_oxygen_backtank", OxygenBacktankBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .blockstate((c,p)-> p.horizontalBlock(c.getEntry(),p.models().getExistingFile(resource("block/oxygen_backtank/netherite"))))
            .properties(BlockBehaviour.Properties::dynamicShape)
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c,p) ->
                    MechanicalCraftingRecipeBuilder.shapedRecipe(c.get(), 1)
                            .key('F', ItemInit.ADVANCED_SPACESUIT_FABRIC.get())
                            .key('N', commonItemTag("plates/aluminum"))
                            .key('W', Items.WHITE_WOOL)
                            .key('T', AllBlocks.FLUID_TANK.get())
                            .patternLine(" F F ")
                            .patternLine("WTNTW")
                            .patternLine(" FWF ")
                            .build(p, resource("mechanical_crafting/armor/" + c.getName())))
            .transform(pickaxeOnly())
            .register();

    public static final BlockEntry<CryogenicTankBlock> CRYOGENIC_TANK = REGISTRATE
            .block("cryogenic_tank", CryogenicTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .blockstate((c,p)-> p.simpleBlock(c.getEntry(), p.models().getExistingFile(resource("block/cryogenic_tank"))))
            .transform(pickaxeOnly())
            .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('N', commonItemTag("plates/nickel"))
                            .define('W', Items.RED_WOOL)
                            .define('T', AllBlocks.FLUID_TANK.get())
                            .pattern("NWN")
                            .pattern("WTW")
                            .pattern("NWN")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/machines/" + c.getName())))
            .item(CryogenicTankItem::new)
            .build()
            .register();

    static{
        REGISTRATE.setCreativeTab(CreativeModeTabsInit.MINERALS_TAB);
    }

    public static final BlockEntry<Block> MOON_STONE = REGISTRATE
            .block("moon_stone",Block::new).initialProperties(()-> Blocks.STONE)
            .properties(p-> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_STONE_TOOL)
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
            .tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL)
            .item()
            .transform(customItemModel("moon_regolith"))
            .register();

    public static final BlockEntry<RegolithSurfaceBlock> MOON_SURFACE_REGOLITH = REGISTRATE
            .block("moon_surface_regolith",RegolithSurfaceBlock::new).initialProperties(()-> Blocks.DIRT)
            .properties(p-> p.strength(1.0f).sound(SoundType.SNOW).mapColor(MapColor.SNOW))
            .tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL)
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
            .tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL)
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel("mars_regolith"))
            .register();

    public static final BlockEntry<Block> MARS_SURFACE_REGOLITH = REGISTRATE
            .block("mars_surface_regolith", Block::new).initialProperties(() -> Blocks.DIRT)
            .properties(p -> p.strength(1.0f).sound(SoundType.SNOW))
            .tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL)
            .item()
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .transform(customItemModel("mars_surface_regolith"))
            .register();

    //ores
    public static final BlockEntry<Block> NICKEL_ORE = REGISTRATE.block(
                    "nickel_ore", Block::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(3.0f).requiresCorrectToolForDrops())
            .loot((lt, b) -> {
                HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
                lt.add(b,
                        lt.createSilkTouchDispatchTable(b,
                                lt.applyExplosionDecay(b, LootItem.lootTableItem(ItemInit.RAW_NICKEL.get())
                                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
            })
            .tag(Tags.Blocks.ORES)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores/nickel")))
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores_in_ground/stone")))
            .transform(TagGen.pickaxeOnly())
            .transform(TagGen.tagBlockAndItem(Map.of(CommonMetal.NICKEL.ores.blocks(), CommonMetal.NICKEL.ores.items(),
                    Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE
            )))
            .build()
            .register();

    public static final BlockEntry<Block> DEEPSLATE_NICKEL_ORE = REGISTRATE.block(
                    "deepslate_nickel_ore", Block::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(4.0f).requiresCorrectToolForDrops())
            .loot((lt, b) -> {
                HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
                lt.add(b,
                        lt.createSilkTouchDispatchTable(b,
                                lt.applyExplosionDecay(b, LootItem.lootTableItem(ItemInit.RAW_NICKEL)
                                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
            })
            .tag(Tags.Blocks.ORES)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores/nickel")))
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores_in_ground/stone")))
            .transform(TagGen.pickaxeOnly())
            .transform(TagGen.tagBlockAndItem(Map.of(CommonMetal.NICKEL.ores.blocks(), CommonMetal.NICKEL.ores.items(),
                    Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE
            )))            .build()
            .register();

    public static final BlockEntry<Block> MOON_NICKEL_ORE = REGISTRATE.block(
                    "moon_nickel_ore", Block::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(3.0f).requiresCorrectToolForDrops())
            .loot((lt, b) -> {
                HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
                lt.add(b,
                        lt.createSilkTouchDispatchTable(b,
                                lt.applyExplosionDecay(b, LootItem.lootTableItem(ItemInit.RAW_NICKEL.get())
                                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
            })
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(Tags.Blocks.ORES)
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores/nickel")))
            .transform(TagGen.pickaxeOnly())
            .transform(TagGen.tagBlockAndItem(Map.of(CommonMetal.NICKEL.ores.blocks(), CommonMetal.NICKEL.ores.items())))
            .transform(customItemModel("moon_nickel_ore"))
            .register();

    public static final BlockEntry<Block> RAW_NICKEL_BLOCK = REGISTRATE.block(
                    "raw_nickel_block", Block::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(1.0f).requiresCorrectToolForDrops())
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
            .transform(TagGen.tagBlockAndItem(CommonMetal.NICKEL.rawStorageBlocks))
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
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/nickel")))
            .recipe((c, p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("ingots/nickel"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .transform(TagGen.tagBlockAndItem(CommonMetal.NICKEL.storageBlocks))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    public static final BlockEntry<Block> MOON_COBALT_ORE = REGISTRATE.block(
                    "moon_cobalt_ore", Block::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(3.0f).requiresCorrectToolForDrops())
            .loot((lt, b) -> {
                HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
                lt.add(b,
                        lt.createSilkTouchDispatchTable(b,
                                lt.applyExplosionDecay(b, LootItem.lootTableItem(ItemInit.RAW_COBALT.get())
                                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
            })
            .tag(Tags.Blocks.ORES)
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores/cobalt")))
            .transform(TagGen.pickaxeOnly())
            .item()
            .tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/cobalt")))
            .transform(customItemModel("moon_cobalt_ore"))
            .register();

    public static final BlockEntry<Block> RAW_COBALT_BLOCK = REGISTRATE.block(
                    "raw_cobalt_block", Block::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .transform(TagGen.pickaxeOnly())
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_cobalt")))
            .recipe((c, p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("raw_materials/cobalt"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .item()
            .tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_cobalt")))
            .transform(customItemModel("raw_cobalt_block"))
            .register();

    public static final BlockEntry<Block> MOON_ALUMINUM_ORE = REGISTRATE.block(
                    "moon_aluminum_ore", Block::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(3.0f).requiresCorrectToolForDrops())
            .loot((lt, b) -> {
                HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
                lt.add(b,
                        lt.createSilkTouchDispatchTable(b,
                                lt.applyExplosionDecay(b, LootItem.lootTableItem(ItemInit.RAW_ALUMINUM.get())
                                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
            })
            .tag(Tags.Blocks.ORES)
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores/aluminum")))
            .transform(TagGen.pickaxeOnly())
            .item()
            .tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/aluminum")))
            .transform(customItemModel("moon_aluminum_ore"))
            .register();

    public static final BlockEntry<Block> RAW_ALUMINUM_BLOCK = REGISTRATE.block(
                    "raw_aluminum_block", Block::new)
            .initialProperties(() -> Blocks.STONE)
            .properties(p -> p.strength(1.0f).requiresCorrectToolForDrops())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .transform(TagGen.pickaxeOnly())
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_aluminum")))
            .recipe((c, p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("raw_materials/aluminum"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .item()
            .tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_aluminum")))
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
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/aluminum")))
            .recipe((c, p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', commonItemTag("ingots/aluminum"))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .item()
            .tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/aluminum")))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .build()
            .register();

    public static final BlockEntry<Block> COBALT_BLOCK = REGISTRATE.block("cobalt_block", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .tag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/cobalt")))
            .recipe((c, p) ->
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
            .register();

    //machinery


    public static final BlockEntry<AmethystBlock> NICKEL_SULFATE_BLOCK = REGISTRATE.block(
                    "nickel_sulfate_block", AmethystBlock::new)
            .initialProperties(() -> Blocks.AMETHYST_BLOCK)
            .properties(p -> p.strength(1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops())
            .item()
            .build()
            .register();

    public static final BlockEntry<AmethystClusterBlock> NICKEL_SULFATE_CLUSTER = REGISTRATE.block(
                    "nickel_sulfate_cluster", p -> new AmethystClusterBlock(7, 3, p))
            .initialProperties(() -> Blocks.AMETHYST_CLUSTER)
            .blockstate((c, p)-> p.directionalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .properties(p -> p.strength(1.5F)
                    .randomTicks().sound(SoundType.AMETHYST_CLUSTER)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 5))
            .loot((lt, b) ->  {
                HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = lt.getRegistries().lookupOrThrow(Registries.ENCHANTMENT);
                lt.add(b,
                        lt.createSilkTouchDispatchTable(b,
                                lt.applyExplosionDecay(b, LootItem.lootTableItem(ItemInit.NICKEL_SULFATE_SHARD.get())
                                        .apply(ApplyBonusCount.addOreBonusCount(enchantmentRegistryLookup.getOrThrow(Enchantments.FORTUNE))))));
            })
            .item()
            .transform((b) -> b.model(AssetLookup.itemModel("nickel_sulfate_cluster")))
            .build().register();

    public static final BlockEntry<AmethystClusterBlock> LARGE_NICKEL_SULFATE_BUD = REGISTRATE.block(
                    "large_nickel_sulfate_bud", p -> new AmethystClusterBlock(5, 3, p))
            .initialProperties(() -> Blocks.AMETHYST_CLUSTER)
            .blockstate((c, p)-> p.directionalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .properties(p -> p.strength(1.5F)
                    .randomTicks().sound(SoundType.LARGE_AMETHYST_BUD)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 4))
            .item()
            .transform((b) -> b.model(AssetLookup.itemModel("large_nickel_sulfate_bud")))
            .build().register();

    public static final BlockEntry<AmethystClusterBlock> MEDIUM_NICKEL_SULFATE_BUD = REGISTRATE.block(
                    "medium_nickel_sulfate_bud", p -> new AmethystClusterBlock(5, 3, p))
            .initialProperties(() -> Blocks.AMETHYST_CLUSTER)
            .blockstate((c, p)-> p.directionalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .properties(p -> p.strength(1.5F)
                    .randomTicks().sound(SoundType.MEDIUM_AMETHYST_BUD)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 2))
            .item()
            .transform((b) -> b.model(AssetLookup.itemModel("medium_nickel_sulfate_bud")))
            .build().register();

    public static final BlockEntry<AmethystClusterBlock> SMALL_NICKEL_SULFATE_BUD = REGISTRATE.block(
                    "small_nickel_sulfate_bud", p -> new AmethystClusterBlock(5, 3, p))
            .initialProperties(() -> Blocks.AMETHYST_CLUSTER)
            .blockstate((c, p)-> p.directionalBlock(c.getEntry(), p.models().getExistingFile(c.getId())))
            .properties(p -> p.strength(1.5F)
                    .randomTicks().sound(SoundType.SMALL_AMETHYST_BUD)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 1))
            .item()
            .transform((b) -> b.model(AssetLookup.itemModel("small_nickel_sulfate_bud")))
            .build().register();

    public static final BlockEntry<BuddingCrystalBlock> BUDDING_NICKEL_SULFATE = REGISTRATE.block(
                    "budding_nickel_sulfate", (p) -> new BuddingCrystalBlock(p,
                            SMALL_NICKEL_SULFATE_BUD.get(),
                            MEDIUM_NICKEL_SULFATE_BUD.get(),
                            LARGE_NICKEL_SULFATE_BUD.get(),
                            NICKEL_SULFATE_CLUSTER.get()))
            .initialProperties(() -> Blocks.BUDDING_AMETHYST)
            .properties(p -> p.strength(1.5F).randomTicks().sound(SoundType.AMETHYST).requiresCorrectToolForDrops())
            .item()
            .build().register();

    private static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(String s) {
        return TagGen.tagBlockAndItem(BlockTags.create(ResourceLocation.withDefaultNamespace(s)),
                ItemTags.create(ResourceLocation.withDefaultNamespace(s)));
    }

    public static void register() {}

}
