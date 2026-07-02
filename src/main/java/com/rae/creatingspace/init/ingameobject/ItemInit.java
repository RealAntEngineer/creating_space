package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.recipes.chemical_synthesis.CatalystItem;
import com.rae.creatingspace.init.CreativeModeTabsInit;
import com.rae.creatingspace.init.EngineMaterialInit;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankItem;
import com.rae.creatingspace.content.life_support.spacesuit.SpacesuitHelmetItem;
import com.rae.creatingspace.content.rocket.engine.design.DesignBlueprintItem;
import com.rae.creatingspace.content.rocket.engine.table.EngineFabricationBlueprint;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.data.recipe.CuttingRecipeGen;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.item.CombustibleItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Objects;

import static com.rae.creatingspace.CreatingSpace.*;
import static com.simibubi.create.AllTags.forgeItemTag;
import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;


public class ItemInit {
    static {
        REGISTRATE.setCreativeTab(CreativeModeTabsInit.COMPONENT_TAB);
    }

    public static final ItemEntry<? extends Item> AEROSPIKE_PLUG = smartRegisterSequencedItem("aerospike_plug");;

    public static final ItemEntry<? extends Item>            BELL_NOZZLE = smartRegisterSequencedItem("bell_nozzle");
    public static final ArrayList<ItemEntry<? extends Item>> POWER_PACK  = smartRegisterSequenced3DItem("power_pack");
    public static final ArrayList<ItemEntry<? extends Item>> EXHAUST_PACK       = smartRegisterSequenced3DItem("exhaust_pack");
    public static final ItemEntry<? extends Item>            COMBUSTION_CHAMBER = smartRegisterSequencedItem("combustion_chamber");
    public static final ArrayList<ItemEntry<? extends Item>> ENGINE_INGREDIENTS = EngineMaterialInit.collectMaterials();
    public static final ArrayList<ItemEntry<? extends Item>> METALS_INGREDIENTS = EngineMaterialInit.collectMetals();

    public static ArrayList<ItemEntry<? extends Item>> registerEngineIngredientForMaterial(String name) {
        ArrayList<ItemEntry<? extends Item>> collector = new ArrayList<>();

        collector.add(smartRegisterSequencedItem(name + "_injector"));
        collector.add(smartRegisterSequencedItem(name + "_turbine"));
        collector.add(smartRegisterSequencedItem(name + "_injector_grid"));

        collector.add(REGISTRATE.item(
                        name + "_engine_wall", Item::new)
                /*.recipe((c,p) ->
                        CuttingRecipeGen
                ).defaultModel()*/
                .register());
        // Splitting off Andesite because we had to be difficult XD
        if (Objects.equals(name, "andesite")) {
           collector.add(REGISTRATE.item((name + "_blisk"), Item::new)
                   .defaultModel()
                   .recipe((c, p) ->
                       ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                           .define('N', AllItems.ANDESITE_ALLOY)
                           .define('I', AllBlocks.SHAFT)
                           .define('P', Items.IRON_NUGGET)
                           .pattern("NPN")
                           .pattern("PIP")
                           .pattern("NPN")
                           .unlockedBy("has_" + c.getName(), has(c.get()))
                           .save(p, resource("crafting/rocket_ingredients/" + name + "_blisk")))
                   .register());

           collector.add(REGISTRATE.item(
                           name + "_turbine_shaft", Item::new)
                   .defaultModel()
                   .recipe((c, p) ->
                           ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                                   .define('I', AllItems.ANDESITE_ALLOY)
                                   .pattern("I  ")
                                   .pattern(" I ")
                                   .pattern("  I")
                                   .unlockedBy("has_" + c.getName(), has(c.get()))
                                   .save(p, resource("crafting/rocket_ingredients/" + name + "_turbine_shaft")))
                   .register());

           collector.add(REGISTRATE.item(
                           name + "_rib", Item::new)
                   .defaultModel()
                   .recipe((c,p) ->
                           ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                                   .define('N', AllItems.ANDESITE_ALLOY)
                                   .define('I', Items.IRON_NUGGET)
                                   .pattern("NIN")
                                   .pattern(" N ")
                                   .unlockedBy("has_" + c.getName(), has(c.get()))
                                   .save(p, resource("crafting/rocket_ingredients/" + name + "_rib")))
                   .register());
       }
       else {
           collector.add(REGISTRATE.item((name + "_blisk"), Item::new)
                   .defaultModel()
                   .recipe((c, p) ->
                       ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                           .define('N', forgeItemTag("nuggets/" + name))
                           .define('I', forgeItemTag("ingots/" + name))
                           .define('P', forgeItemTag("plates/" + name))
                           .pattern("NPN")
                           .pattern("PIP")
                           .pattern("NPN")
                           .unlockedBy("has_" + c.getName(), has(c.get()))
                           .save(p, resource("crafting/rocket_ingredients/" + name + "_blisk")))
                   .register());

           collector.add(REGISTRATE.item(
                           name + "_turbine_shaft", Item::new)
                   .defaultModel()
                   .recipe((c, p) ->
                           ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                                   .define('I', forgeItemTag("ingots/" + name))
                                   .pattern("I  ")
                                   .pattern(" I ")
                                   .pattern("  I")
                                   .unlockedBy("has_" + c.getName(), has(c.get()))
                                   .save(p, resource("crafting/rocket_ingredients/" + name + "_turbine_shaft")))
                   .register());

           collector.add(REGISTRATE.item(
                           name + "_rib", Item::new)
                   .defaultModel()
                   .recipe((c,p) ->
                           ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                                   .define('N', forgeItemTag("nuggets/" + name))
                                   .define('I', forgeItemTag("ingots/" + name))
                                   .pattern("NIN")
                                   .pattern(" N ")
                                   .unlockedBy("has_" + c.getName(), has(c.get()))
                                   .save(p, resource("crafting/rocket_ingredients/" + name + "_rib")))
                   .register());
       }

        /*collector.add(CreatingSpace.REGISTRATE.item(
                        name + "_canal", Item::new)
                .defaultModel()
                .register());
        collector.add(CreatingSpace.REGISTRATE.item(
                        name + "_engine_pipe", Item::new)
                .defaultModel()
                .register());*/
        return collector;
    }
    public static ArrayList<ItemEntry<? extends Item>> registerMetalVariants(String name) {
        ArrayList<ItemEntry<? extends Item>> collector = new ArrayList<>();

        collector.add(REGISTRATE.item(name + "_ingot", Item::new)
                .defaultModel()
                //.properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                .recipe((c,p) -> {
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('#', forgeItemTag("nuggets/" + name))
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + name + "_ingot_from_nuggets"));
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                            .requires(forgeItemTag("storage_blocks/" + name))
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + name + "_ingot_from_block"));
                })
                .tag(forgeItemTag("ingots/"+ name), forgeItemTag("ingots"))
                .register());
        collector.add(REGISTRATE.item(
                        name + "_sheet", Item::new)
                .tag(forgeItemTag("plates/" + name), forgeItemTag("plates"))
                .defaultModel()
                //.properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                .register());
        collector.add(REGISTRATE.item(
                        name + "_nugget", Item::new)
                .recipe((c,p) ->
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                            .requires(forgeItemTag("ingots/" + name))
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + name + "_nuggets_from_ingot")))
                .tag(forgeItemTag("nuggets/"+ name), forgeItemTag("nuggets"))
                .defaultModel()
                //.properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                .register());
        return collector;
    }

    private static ItemEntry<? extends Item> smartRegisterSequencedItem(String name) {

        registerTransitionItem(name); // we don't put the incomplete version in the creative tab
        return REGISTRATE.item(
                        name, Item::new)
                .defaultModel()
                .register();
    }

    private static ArrayList<ItemEntry<? extends Item>> smartRegisterSequenced3DItem(String name) {
        ArrayList<ItemEntry<? extends Item>> collector = new ArrayList<>();
        collector.add(REGISTRATE.item(
                        name, Item::new)
                //.properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                                .transform((b) -> b.model(AssetLookup.itemModel(name))
                )
                .register());
        REGISTRATE.item(
                        "incomplete_"+name, SequencedAssemblyItem::new)
                .model((c, p) ->
                        p.getExistingFile(resource("item/incomplete_"+name)))
                .register();
        // we don't put the incomplete version in the creative tab
        return collector;
    }

    private static ItemEntry<SequencedAssemblyItem> registerTransitionItem(String name) {
        return REGISTRATE.item(
                        "incomplete_"+name, SequencedAssemblyItem::new)
                .model((c, p) ->
                        p.withExistingParent("item/incomplete_"+name,
                        "item/generated").texture("layer0",
                        resource("item/transition_item/"+ name)))
                .register();
    }

    private static ItemEntry<SequencedAssemblyItem> registerSequencedEngineItem(String name) {
        return REGISTRATE.item(
                        name, SequencedAssemblyItem::new)
                .model((c, p) -> p.withExistingParent(name,
                        resource("block/1_2_1_block")))
                .register();
    }

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_ENGINE = registerSequencedEngineItem("incomplete_rocket_engine");

    public static final ItemEntry<DesignBlueprintItem> DESIGN_BLUEPRINT =
            REGISTRATE.item("design_blueprint", DesignBlueprintItem::new)
                    //.properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .model((c, p) -> p.withExistingParent("design_blueprint",
                            "item/generated").texture("layer0",
                            ResourceLocation.tryParse("item/paper")))
                    .register();

    public static final ItemEntry<EngineFabricationBlueprint> ENGINE_BLUEPRINT =
            REGISTRATE.item("engine_blueprint", EngineFabricationBlueprint::new)
                    //.properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .defaultModel()
                    .register();

    public static final ItemEntry<? extends Item> BASIC_SPACESUIT_FABRIC = smartRegisterSequencedItem(
            "basic_spacesuit_fabric"
    );

    public static final ItemEntry<? extends Item> ADVANCED_SPACESUIT_FABRIC = smartRegisterSequencedItem(
            "advanced_spacesuit_fabric"
    );

    public static final ItemEntry<Item> COPPER_COIL = REGISTRATE.item(
            "copper_coil",Item::new)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('C', forgeItemTag("ingots/copper"))
                            .pattern("CCC")
                            .pattern("C C")
                            .pattern("CCC")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/misc/" + c.getName())))
            .register();

    public static final ItemEntry<Item> BASIC_CATALYST = REGISTRATE.item(
            "basic_catalyst",Item::new)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 4)
                            .define('B', Items.BONE_MEAL)
                            .define('N', ItemInit.NICKEL_DUST)
                            .pattern("NBN")
                            .pattern("BNB")
                            .pattern("NBN")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/misc/" + c.getName())))
            .register();

    public static final ItemEntry<CombustibleItem> COAL_DUST = CreatingSpace.REGISTRATE.item(
            "coal_dust", CombustibleItem::new)
            .onRegister(i -> i.setBurnTime(500))
            .register();

    //food
    //exemple -> arn't registered...
    public static final ItemEntry<Item> SPACE_FOOD = REGISTRATE.item(
            "space_food",Item::new)
            .properties(p->p.food(new FoodProperties.Builder()
                        .alwaysEat()
                        .nutrition(4)
                        .saturationMod(1f)
                        .fast()
                        .effect(()->
                                    new MobEffectInstance(MobEffects.NIGHT_VISION,
                                            72000
                                            ,1)
                            , 1.0f)
                        .build())
            )
            .register();

    //minerals
    public static final ItemEntry<Item> NICKEL_SULFATE_SHARD = REGISTRATE.item(
                    "nickel_sulfate_shard", Item::new)
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .register();
    //nickel
    public static final ItemEntry<Item> RAW_NICKEL = REGISTRATE.item(
            "raw_nickel",Item::new)
            .recipe((c,p) ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                        .requires(forgeItemTag("storage_blocks/raw_nickel"))
                        .unlockedBy("has_" + c.getName(), has(c.get()))
                        .save(p, resource("crafting/" + c.getName() + "_from_block")))
            .tag(forgeItemTag("raw_materials/nickel"), forgeItemTag("raw_materials"), forgeItemTag("ores/nickel"))
            .register();


    /*public static final ItemEntry<Item> CRUSHED_NICKEL_ORE = CreatingSpace.REGISTRATE.item(
            "crushed_nickel_ore",Item::new)
            .tag(commonItemTag("crushed_raw_nickel"), commonItemTag("crushed_raw_materials"))
            .register();*/

    public static final ItemEntry<Item> NICKEL_DUST = REGISTRATE.item(
                    "nickel_dust",Item::new)
            .tag(forgeItemTag("dusts/nickel"), forgeItemTag("dusts"))
            .register();


    public static final ItemEntry<Item> NICKEL_INGOT = REGISTRATE.item(
            "nickel_ingot",Item::new)
            .recipe((c,p) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                        .define('#', forgeItemTag("nuggets/nickel"))
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .unlockedBy("has_" + c.getName(), has(c.get()))
                        .save(p, resource("crafting/" + c.getName() + "_from_nuggets"));
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                        .requires(forgeItemTag("storage_blocks/nickel"))
                        .unlockedBy("has_" + c.getName(), has(c.get()))
                        .save(p, resource("crafting/" + c.getName() + "_from_block"));
            })
            .tag(forgeItemTag("ingots/nickel"), forgeItemTag("ingots"))
            .register();


    public static final ItemEntry<Item> NICKEL_NUGGET = REGISTRATE.item(
            "nickel_nugget",Item::new)
            .recipe((c,p) ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                        .requires(forgeItemTag("ingots/nickel"))
                        .unlockedBy("has_" + c.getName(), has(c.get()))
                        .save(p, resource("crafting/" + c.getName())))
            .tag(forgeItemTag("nuggets/nickel"), forgeItemTag("nuggets"))
            .register();



    public static final ItemEntry<Item> NICKEL_SHEET = REGISTRATE.item(
            "nickel_sheet",Item::new)
            .tag(forgeItemTag("plates/nickel"), forgeItemTag("plates"))
            .register();

    //aluminium

    public static final ItemEntry<Item> RAW_ALUMINUM = REGISTRATE.item(
                    "raw_aluminum",Item::new)
            .recipe((c,p) ->
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                            .requires(forgeItemTag("storage_blocks/raw_aluminum"))
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName() + "_from_block")))
            .tag(forgeItemTag("raw_materials/aluminum"), forgeItemTag("raw_materials"))
            .register();


    /*public static final ItemEntry<Item> CRUSHED_ALUMINUM_ORE = CreatingSpace.REGISTRATE.item(
                    "crushed_aluminum_ore",Item::new)
            .tag(commonItemTag("crushed_raw_aluminum"), commonItemTag("crushed_raw_materials"), commonItemTag("ores/aluminum"))
            .register();*/


    public static final ItemEntry<Item> ALUMINUM_INGOT = REGISTRATE.item(
                    "aluminum_ingot",Item::new)
            .recipe((c,p) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                        .define('#', forgeItemTag("nuggets/aluminum"))
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .unlockedBy("has_" + c.getName(), has(c.get()))
                        .save(p, resource("crafting/" + c.getName() + "_from_nuggets"));
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                        .requires(forgeItemTag("storage_blocks/aluminum"))
                        .unlockedBy("has_" + c.getName(), has(c.get()))
                        .save(p, resource("crafting/" + c.getName() + "_from_block"));
            })
            .tag(forgeItemTag("ingots/aluminum"), forgeItemTag("ingots"))
            .register();


    public static final ItemEntry<Item> ALUMINUM_NUGGET = REGISTRATE.item(
                    "aluminum_nugget",Item::new)
            .recipe((c,p) ->
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                            .requires(forgeItemTag("ingots/aluminum"))
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .tag(forgeItemTag("nuggets/aluminum"), forgeItemTag("nuggets"))
            .register();

    public static final ItemEntry<Item> ALUMINUM_SHEET = REGISTRATE.item(
                    "aluminum_sheet",Item::new)
            .tag(forgeItemTag("plates/aluminum"), forgeItemTag("plates"))
            .register();

    //cobalt

    public static final ItemEntry<Item> RAW_COBALT = REGISTRATE.item(
                    "raw_cobalt",Item::new)
            .recipe((c,p) ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                        .requires(forgeItemTag("storage_blocks/raw_cobalt"))
                        .unlockedBy("has_" + c.getName(), has(c.get()))
                        .save(p, resource("crafting/" + c.getName() + "_from_block")))
            .tag(forgeItemTag("raw_materials/cobalt"), forgeItemTag("raw_materials"))
            .register();

    public static final ItemEntry<Item> CRUSHED_COBALT_ORE = REGISTRATE.item(
                    "crushed_cobalt_ore",Item::new)
            .tag(forgeItemTag("crushed_raw_cobalt"), forgeItemTag("crushed_raw_materials"), forgeItemTag("ores/cobalt"))
            .register();

    public static final ItemEntry<Item> COBALT_INGOT = REGISTRATE.item(
                    "cobalt_ingot",Item::new)
            .recipe((c,p) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                        .define('#', forgeItemTag("nuggets/cobalt"))
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .unlockedBy("has_" + c.getName(), has(c.get()))
                        .save(p, resource("crafting/" + c.getName() + "_from_nuggets"));
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                        .requires(forgeItemTag("storage_blocks/cobalt"))
                        .unlockedBy("has_" + c.getName(), has(c.get()))
                        .save(p, resource("crafting/" + c.getName() + "_from_block"));
            })
            .tag(forgeItemTag("ingots/cobalt"), forgeItemTag("ingots"))
            .register();

    public static final ItemEntry<Item> COBALT_NUGGET = REGISTRATE.item(
                    "cobalt_nugget",Item::new)
            .recipe((c,p) ->
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 9)
                            .requires(forgeItemTag("ingots/cobalt"))
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/" + c.getName())))
            .tag(forgeItemTag("nuggets/cobalt"), forgeItemTag("nuggets"))
            .register();

    public static final ItemEntry<Item> COBALT_SHEET = REGISTRATE.item(
                    "cobalt_sheet",Item::new)
            .tag(forgeItemTag("plates/cobalt"), forgeItemTag("plates"))
            .register();

    public static final ItemEntry<OxygenBacktankItem.O2BacktankBlockItem> COPPER_BACKTANK_PLACEABLE =
            REGISTRATE
                    .item("copper_oxygen_backtank_placeable",
                            p -> new OxygenBacktankItem.O2BacktankBlockItem(BlockInit.COPPER_OXYGEN_BACKTANK.get(), ItemInit.COPPER_OXYGEN_BACKTANK::get, p))
                    .model((c,p) -> p.withExistingParent("copper_oxygen_backtank_placeable",
                        "minecraft:item/barrier"))
                    .register();

    public static final ItemEntry<OxygenBacktankItem.Layered> COPPER_OXYGEN_BACKTANK =
            REGISTRATE
                    .item("copper_oxygen_backtank",
                            p -> new OxygenBacktankItem.Layered(AllArmorMaterials.COPPER, p, CreatingSpace.resource("basic_spacesuit"),
                                    COPPER_BACKTANK_PLACEABLE))
                    .model((c,p) -> p.withExistingParent("copper_oxygen_backtank",
                            MODID + ":block/oxygen_backtank/copper"))
                    .recipe((c,p) ->
                            MechanicalCraftingRecipeBuilder.shapedRecipe(c.get(), 1)
                                    .key('F', ItemInit.BASIC_SPACESUIT_FABRIC.get())
                                    .key('N', forgeItemTag("plates/nickel"))
                                    .key('W', Items.RED_WOOL)
                                    .key('T', AllBlocks.FLUID_TANK.get())
                                    .patternLine(" F F ")
                                    .patternLine("WTNTW")
                                    .patternLine(" FWF ")
                                    .build(p, resource("mechanical_crafting/armor/" + c.getName())))
                    .tag(TagsInit.CustomItemTags.OXYGEN_SOURCES.tag)
                    //.tag(ItemTags.CHEST_ARMOR)
                    .tag(ItemTags.TRIMMABLE_ARMOR)
                    .tag(TagsInit.CustomItemTags.SPACESUIT.tag)
                    .register();

    public static final ItemEntry<OxygenBacktankItem.O2BacktankBlockItem> NETHERITE_BACKTANK_PLACEABLE =
            REGISTRATE
                    .item("netherite_oxygen_backtank_placeable",
                            p -> new OxygenBacktankItem.O2BacktankBlockItem(BlockInit.NETHERITE_OXYGEN_BACKTANK.get(), ItemInit.NETHERITE_OXYGEN_BACKTANK::get, p))
                    .properties(Item.Properties::fireResistant)
                    .model((c,p) -> p.withExistingParent("netherite_oxygen_backtank_placeable",
                            "minecraft:item/barrier"))
                    .register();

    public static final ItemEntry<OxygenBacktankItem.Layered> NETHERITE_OXYGEN_BACKTANK =
            REGISTRATE
                    .item("netherite_oxygen_backtank",
                            p -> new OxygenBacktankItem.Layered(ArmorMaterials.NETHERITE, p, CreatingSpace.resource("advanced_spacesuit"),
                                    NETHERITE_BACKTANK_PLACEABLE))
                    .properties(Item.Properties::fireResistant)
                    .model((c,p) -> p.withExistingParent("netherite_oxygen_backtank",
                            MODID + ":block/oxygen_backtank/netherite"))
                    .tag(TagsInit.CustomItemTags.OXYGEN_SOURCES.tag)
                    //.tag(ItemTags.CHEST_ARMOR)
                    .tag(ItemTags.TRIMMABLE_ARMOR)
                    .tag(TagsInit.CustomItemTags.SPACESUIT.tag)
                    .recipe((c,p) ->
                            MechanicalCraftingRecipeBuilder.shapedRecipe(c.get(), 1)
                                    .key('F', ItemInit.ADVANCED_SPACESUIT_FABRIC.get())
                                    .key('N', forgeItemTag("plates/aluminum"))
                                    .key('W', Items.WHITE_WOOL)
                                    .key('T', AllBlocks.FLUID_TANK.get())
                                    .patternLine(" F F ")
                                    .patternLine("WTNTW")
                                    .patternLine(" FWF ")
                                    .build(p, resource("mechanical_crafting/armor/" + c.getName())))
                    .register();

    public static final ItemEntry<BaseArmorItem> BASIC_SPACESUIT_LEGGINGS =
            REGISTRATE
                    .item("basic_spacesuit_leggings",
                            p -> new BaseArmorItem(AllArmorMaterials.COPPER, ArmorItem.Type.LEGGINGS, p, CreatingSpace.resource("basic_spacesuit")))
                    .recipe((c,p) ->
                            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                                    .define('F', ItemInit.BASIC_SPACESUIT_FABRIC::get)
                                    .pattern("FFF")
                                    .pattern("F F")
                                    .pattern("F F")
                                    .unlockedBy("has_" + c.getName(), has(c.get()))
                                    .save(p, resource("crafting/armor/" + c.getName())))
                    .tag(ItemTags.TRIMMABLE_ARMOR)
                    .tag(TagsInit.CustomItemTags.SPACESUIT.tag)
                    .register();

    public static final ItemEntry<BaseArmorItem> BASIC_SPACESUIT_BOOTS =
            REGISTRATE
                    .item("basic_spacesuit_boots",
                            p -> new BaseArmorItem(AllArmorMaterials.COPPER, ArmorItem.Type.BOOTS, p, CreatingSpace.resource("basic_spacesuit")))
                    .recipe((c,p) ->
                            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                                    .define('F', ItemInit.BASIC_SPACESUIT_FABRIC::get)
                                    .pattern("F F")
                                    .pattern("F F")
                                    .unlockedBy("has_" + c.getName(), has(c.get()))
                                    .save(p, resource("crafting/armor/" + c.getName())))
                    .tag(ItemTags.TRIMMABLE_ARMOR)
                    .tag(TagsInit.CustomItemTags.SPACESUIT.tag)
                    .register();

    public static final ItemEntry<SpacesuitHelmetItem> BASIC_SPACESUIT_HELMET =
            REGISTRATE
                    .item("basic_spacesuit_helmet",
                            p -> new SpacesuitHelmetItem(AllArmorMaterials.COPPER, p, CreatingSpace.resource("basic_spacesuit")))
                    .recipe((c,p) ->
                            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                                    .define('F', ItemInit.BASIC_SPACESUIT_FABRIC::get)
                                    .define('G', AllItems.GOLDEN_SHEET::get)
                                    .pattern("FFF")
                                    .pattern("FGF")
                                    .unlockedBy("has_" + c.getName(), has(c.get()))
                                    .save(p, resource("crafting/armor/" + c.getName())))
                    .tag(ItemTags.TRIMMABLE_ARMOR)
                    .tag(TagsInit.CustomItemTags.SPACESUIT.tag)
                    .register();

    public static final ItemEntry<BaseArmorItem> ADVANCED_SPACESUIT_LEGGINGS =
            REGISTRATE
                    .item("advanced_spacesuit_leggings",
                            p -> new BaseArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, p, CreatingSpace.resource("advanced_spacesuit")))
                    .properties(Item.Properties::fireResistant)
                    .recipe((c,p) ->
                            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                                    .define('F', ItemInit.ADVANCED_SPACESUIT_FABRIC::get)
                                    .pattern("FFF")
                                    .pattern("F F")
                                    .pattern("F F")
                                    .unlockedBy("has_" + c.getName(), has(c.get()))
                                    .save(p, resource("crafting/armor/" + c.getName())))
                    .tag(ItemTags.TRIMMABLE_ARMOR)
                    .tag(TagsInit.CustomItemTags.SPACESUIT.tag)
                    .register();

    public static final ItemEntry<BaseArmorItem> ADVANCED_SPACESUIT_BOOTS =
            REGISTRATE
                    .item("advanced_spacesuit_boots",
                            p -> new BaseArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, p, CreatingSpace.resource("advanced_spacesuit")))
                    .properties(Item.Properties::fireResistant)
                    .recipe((c,p) ->
                            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                                    .define('F', ItemInit.ADVANCED_SPACESUIT_FABRIC::get)
                                    .pattern("F F")
                                    .pattern("F F")
                                    .unlockedBy("has_" + c.getName(), has(c.get()))
                                    .save(p, resource("crafting/armor/" + c.getName())))
                    .tag(ItemTags.TRIMMABLE_ARMOR)
                    .tag(TagsInit.CustomItemTags.SPACESUIT.tag)
                    .register();

    public static final ItemEntry<SpacesuitHelmetItem> ADVANCED_SPACESUIT_HELMET =
            REGISTRATE
                    .item("advanced_spacesuit_helmet",
                            p -> new SpacesuitHelmetItem(ArmorMaterials.NETHERITE, p, CreatingSpace.resource("advanced_spacesuit")))
                    .properties(Item.Properties::fireResistant)
                    .recipe((c,p) ->
                            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                                    .define('F', ItemInit.ADVANCED_SPACESUIT_FABRIC::get)
                                    .define('G', AllItems.GOLDEN_SHEET::get)
                                    .pattern("FFF")
                                    .pattern("FGF")
                                    .unlockedBy("has_" + c.getName(), has(c.get()))
                                    .save(p, resource("crafting/armor/" + c.getName())))
                    .tag(ItemTags.TRIMMABLE_ARMOR)
                    .tag(TagsInit.CustomItemTags.SPACESUIT.tag)
                    .register();

    public static final ItemEntry<Item> INJECTOR = REGISTRATE.item(
                    "injector", Item::new)
            .register();

    public static final ItemEntry<Item> STURDY_PROPELLER = REGISTRATE.item(
                    "sturdy_propeller", Item::new)
            .recipe((c,p) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .define('I', forgeItemTag("ingots/iron"))
                            .define('S', AllItems.STURDY_SHEET)
                            .pattern(" S ")
                            .pattern("SIS")
                            .pattern(" S ")
                            .unlockedBy("has_" + c.getName(), has(c.get()))
                            .save(p, resource("crafting/misc/" + c.getName())))
            .register();

    public static final ItemEntry<CatalystItem> NICKEL_SULFATE_CATALYST = REGISTRATE.item(
            "nickel_sulfate_catalyst", (p) -> new CatalystItem(p, PartialModelInit.NICKEL_SULFATE_CATALYST))
            .properties(p -> p.durability(100))
            .register();

    public static void register() {}
}
