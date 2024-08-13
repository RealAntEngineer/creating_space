package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.CreativeModeTabsInit;
import com.rae.creatingspace.init.EngineMaterialInit;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.server.armor.OxygenBacktankItem;
import com.rae.creatingspace.server.armor.SpacesuitHelmetItem;
import com.rae.creatingspace.server.items.DesignBlueprintItem;
import com.rae.creatingspace.server.items.EngineFabricationBlueprint;
import com.rae.creatingspace.server.items.UpgradableEquipment;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.item.CombustibleItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

import static com.simibubi.create.AllTags.forgeItemTag;
public class ItemInit {
    static {
        smartRegisterSequencedItem("aerospike_plug");
        smartRegisterSequencedItem("bell_nozzle");
        smartRegisterSequencedItem("power_pack");
        smartRegisterSequencedItem("exhaust_pack");
        smartRegisterSequencedItem("combustion_chamber");
        EngineMaterialInit.register();

    }

    public static void registerEngineIngredientForMaterial(String name) {

        smartRegisterSequencedItem(name + "_blisk");
        smartRegisterSequencedItem(name + "_injector");
        CreatingSpace.REGISTRATE.item(
                        name + "_engine_wall", Item::new)
                .properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                .register();
        CreatingSpace.REGISTRATE.item(
                        name + "_rib", Item::new)
                .properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                .register();
        CreatingSpace.REGISTRATE.item(
                        name + "_canal", Item::new)
                .properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                .register();
        CreatingSpace.REGISTRATE.item(
                        name + "_engine_pipe", Item::new)
                .properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                .register();
        smartRegisterSequencedItem(name + "_turbine");
        smartRegisterSequencedItem(name + "_injector_grid");
    }

    private static void smartRegisterSequencedItem(String name) {
        CreatingSpace.REGISTRATE.item(
                        name, Item::new)
                .properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                .register();
        registerSequencedItem("incomplete_" + name);
    }

    private static ItemEntry<SequencedAssemblyItem> registerSequencedItem(String name) {
        return CreatingSpace.REGISTRATE.item(
                        name, SequencedAssemblyItem::new)
                .register();
    }


    public static final ItemEntry<DesignBlueprintItem> DESIGN_BLUEPRINT =
            CreatingSpace.REGISTRATE.item("design_blueprint", DesignBlueprintItem::new)
                    .properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .register();
    public static final ItemEntry<EngineFabricationBlueprint> ENGINE_BLUEPRINT =
            CreatingSpace.REGISTRATE.item("engine_blueprint", EngineFabricationBlueprint::new)
                    .properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .register();

    public static final ItemEntry<Item> BASIC_SPACESUIT_FABRIC = CreatingSpace.REGISTRATE.item(
                    "basic_spacesuit_fabric",Item::new)
            .properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();

    public static final ItemEntry<Item> ADVANCED_SPACESUIT_FABRIC = CreatingSpace.REGISTRATE.item(
                    "advanced_spacesuit_fabric",Item::new)
            .properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();



    public static final ItemEntry<Item> COPPER_COIL = CreatingSpace.REGISTRATE.item(
            "copper_coil",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    public static final ItemEntry<Item> BASIC_CATALYST = CreatingSpace.REGISTRATE.item(
            "basic_catalyst",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    public static final ItemEntry<CombustibleItem> COAL_DUST = CreatingSpace.REGISTRATE.item(
            "coal_dust", CombustibleItem::new)
            .onRegister(i -> i.setBurnTime(500))
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    //food
    //exemple -> arn't registered...
    public static final ItemEntry<Item> SPACE_FOOD = CreatingSpace.REGISTRATE.item(
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
    public static final ItemEntry<Item> CRYSTAL_SHARD = CreatingSpace.REGISTRATE.item(
                    "crystal_shard", Item::new)
            //.properties(p -> p.tab(CreativeModeTabsInit.MINERALS_TAB))
            .register();
    //nickel
    public static final ItemEntry<Item> RAW_NICKEL = CreatingSpace.REGISTRATE.item(
            "raw_nickel",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    /*public static final ItemEntry<Item> CRUSHED_NICKEL_ORE = CreatingSpace.REGISTRATE.item(
            "crushed_nickel_ore",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();*/

    public static final ItemEntry<Item> NICKEL_DUST = CreatingSpace.REGISTRATE.item(
                    "nickel_dust",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    public static final ItemEntry<Item> NICKEL_INGOT = CreatingSpace.REGISTRATE.item(
            "nickel_ingot",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    public static final ItemEntry<Item> NICKEL_NUGGET = CreatingSpace.REGISTRATE.item(
            "nickel_nugget",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();



    public static final ItemEntry<Item> NICKEL_SHEET = CreatingSpace.REGISTRATE.item(
            "nickel_sheet",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();

    //aluminium

    public static final ItemEntry<Item> RAW_ALUMINUM = CreatingSpace.REGISTRATE.item(
                    "raw_aluminum",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    /*public static final ItemEntry<Item> CRUSHED_ALUMINUM_ORE = CreatingSpace.REGISTRATE.item(
                    "crushed_aluminum_ore",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();*/


    public static final ItemEntry<Item> ALUMINUM_INGOT = CreatingSpace.REGISTRATE.item(
                    "aluminum_ingot",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    public static final ItemEntry<Item> ALUMINUM_NUGGET = CreatingSpace.REGISTRATE.item(
                    "aluminum_nugget",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();



    public static final ItemEntry<Item> ALUMINUM_SHEET = CreatingSpace.REGISTRATE.item(
                    "aluminum_sheet",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();

    //cobalt

    public static final ItemEntry<Item> RAW_COBALT = CreatingSpace.REGISTRATE.item(
                    "raw_cobalt",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    public static final ItemEntry<Item> CRUSHED_COBALT_ORE = CreatingSpace.REGISTRATE.item(
                    "crushed_cobalt_ore",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    public static final ItemEntry<Item> COBALT_INGOT = CreatingSpace.REGISTRATE.item(
                    "cobalt_ingot",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();


    public static final ItemEntry<Item> COBALT_NUGGET = CreatingSpace.REGISTRATE.item(
                    "cobalt_nugget",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();

    public static final ItemEntry<Item> COBALT_SHEET = CreatingSpace.REGISTRATE.item(
                    "cobalt_sheet",Item::new)
            .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();

    public static final ItemEntry<SpacesuitHelmetItem> BASIC_SPACESUIT_HELMET =
            CreatingSpace.REGISTRATE
                    .item("basic_spacesuit_helmet",
                            p -> new SpacesuitHelmetItem(AllArmorMaterials.COPPER, p, CreatingSpace.resource("basic_spacesuit")))
                    .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .tag(forgeItemTag("armors/helmet"))
                    .register();
    public static final ItemEntry<OxygenBacktankItem.O2BacktankBlockItem> COPPER_BACKTANK_PLACEABLE =
            CreatingSpace.REGISTRATE
                    .item("copper_oxygen_backtank_placeable",
                            p -> new OxygenBacktankItem.O2BacktankBlockItem(BlockInit.COPPER_OXYGEN_BACKTANK.get(), ItemInit.COPPER_OXYGEN_BACKTANK::get, p))
                    .register();
    public static final ItemEntry<OxygenBacktankItem.Layered> COPPER_OXYGEN_BACKTANK =
            CreatingSpace.REGISTRATE
                    .item("copper_oxygen_backtank",
                            p -> new OxygenBacktankItem.Layered(AllArmorMaterials.COPPER, p, CreatingSpace.resource("basic_spacesuit"),
                                    COPPER_BACKTANK_PLACEABLE))
                    .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .tag(TagsInit.CustomItemTags.OXYGEN_SOURCES.tag)
                    .tag(forgeItemTag("armors/chestplates"))
                    .register();
    public static final ItemEntry<UpgradableEquipment> BASIC_SPACESUIT_LEGGINGS =
            CreatingSpace.REGISTRATE
                    .item("basic_spacesuit_leggings",
                            p -> new UpgradableEquipment(AllArmorMaterials.COPPER, EquipmentSlot.LEGS, p, CreatingSpace.resource("basic_spacesuit")))
                    .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .tag(forgeItemTag("armors/leggings"))
                    .register();
    public static final ItemEntry<UpgradableEquipment> BASIC_SPACESUIT_BOOTS =
            CreatingSpace.REGISTRATE
                    .item("basic_spacesuit_boots",
                            p -> new UpgradableEquipment(AllArmorMaterials.COPPER, EquipmentSlot.FEET, p, CreatingSpace.resource("basic_spacesuit")))
                    .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .tag(forgeItemTag("armors/boots"))
                    .register();

    public static final ItemEntry<SpacesuitHelmetItem> ADVANCED_SPACESUIT_HELMET =
            CreatingSpace.REGISTRATE
                    .item("advanced_spacesuit_helmet",
                            p -> new SpacesuitHelmetItem(ArmorMaterials.NETHERITE, p, CreatingSpace.resource("advanced_spacesuit")))
                    .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .tag(forgeItemTag("armors/helmet"))
                    .register();
    public static final ItemEntry<OxygenBacktankItem.O2BacktankBlockItem> NETHERITE_BACKTANK_PLACEABLE =
            CreatingSpace.REGISTRATE
                    .item("netherite_oxygen_backtank_placeable",
                            p -> new OxygenBacktankItem.O2BacktankBlockItem(BlockInit.NETHERITE_OXYGEN_BACKTANK.get(), ItemInit.NETHERITE_OXYGEN_BACKTANK::get, p))
                    .register();
    public static final ItemEntry<OxygenBacktankItem.Layered> NETHERITE_OXYGEN_BACKTANK =
            CreatingSpace.REGISTRATE
                    .item("netherite_oxygen_backtank",
                            p -> new OxygenBacktankItem.Layered(ArmorMaterials.NETHERITE, p, CreatingSpace.resource("advanced_spacesuit"),
                                    NETHERITE_BACKTANK_PLACEABLE))
                    .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .tag(TagsInit.CustomItemTags.OXYGEN_SOURCES.tag)
                    .tag(forgeItemTag("armors/chestplates"))
                    .register();
    public static final ItemEntry<UpgradableEquipment> ADVANCED_SPACESUIT_LEGGINGS =
            CreatingSpace.REGISTRATE
                    .item("advanced_spacesuit_leggings",
                            p -> new UpgradableEquipment(ArmorMaterials.NETHERITE, EquipmentSlot.LEGS, p, CreatingSpace.resource("advanced_spacesuit")))
                    .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .tag(forgeItemTag("armors/leggings"))
                    .register();
    public static final ItemEntry<UpgradableEquipment> ADVANCED_SPACESUIT_BOOTS =
            CreatingSpace.REGISTRATE
                    .item("advanced_spacesuit_boots",
                            p -> new UpgradableEquipment(ArmorMaterials.NETHERITE, EquipmentSlot.FEET, p, CreatingSpace.resource("advanced_spacesuit")))
                    .properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
                    .tag(forgeItemTag("armors/boots"))
                    .register();


    //legacy items
    public static final ItemEntry<CombustibleItem> STARTER_CHARGE = CreatingSpace.REGISTRATE.item(
                    "starter_charge", CombustibleItem::new)
            .onRegister(i -> i.setBurnTime(500))
            //.properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();
    public static final ItemEntry<Item> INJECTOR = CreatingSpace.REGISTRATE.item(
                    "injector", Item::new)
            //.properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();
    public static final ItemEntry<Item> REINFORCED_INJECTOR = CreatingSpace.REGISTRATE.item(
                    "reinforced_injector", Item::new)
            //.properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();
    public static final ItemEntry<Item> STURDY_PROPELLER = CreatingSpace.REGISTRATE.item(
                    "sturdy_propeller", Item::new)
            //.properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();

    public static final ItemEntry<Item> INJECTOR_GRID = CreatingSpace.REGISTRATE.item(
                    "injector_grid", Item::new)
            //.properties(p->p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();
    public static final ItemEntry<Item> REINFORCED_INJECTOR_GRID = CreatingSpace.REGISTRATE.item(
                    "reinforced_injector_grid", Item::new)
            //.properties(p -> p.tab(CreativeModeTabsInit.COMPONENT_TAB))
            .register();
    public static void register() {}
}
