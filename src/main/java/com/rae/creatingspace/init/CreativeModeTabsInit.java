package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllCreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeModeTabsInit {
    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreatingSpace.MODID);

    public static final RegistryObject<CreativeModeTab> MACHINE_TAB = TAB_REGISTER.register(
            "machine_tab",
            ()->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.creatingspace.machine_tab"))
                    .withTabsBefore(AllCreativeModeTabs.BUILDING_BLOCKS_TAB.getKey())
                    .icon(BlockInit.MECHANICAL_ELECTROLYZER::asStack)
                    .displayItems(($1,output)-> {
                        output.accept(BlockInit.CLAMPS);
                        output.accept(BlockInit.ROCKET_CASING);
                        output.accept(BlockInit.SMALL_ROCKET_ENGINE);
                        output.accept(BlockInit.BIG_ROCKET_ENGINE);
                        output.accept(BlockInit.ROCKET_CONTROLS);
                        output.accept(BlockInit.CHEMICAL_SYNTHESIZER);
                        output.accept(BlockInit.MECHANICAL_ELECTROLYZER);
                        output.accept(BlockInit.FLOW_METER);
                    })
                    .build());
    public static final RegistryObject<CreativeModeTab> COMPONENT_TAB = TAB_REGISTER.register(
                "component_tab",
                ()->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.creatingspace.component_tab"))
                        .withTabsBefore(MACHINE_TAB.getKey())
                        .icon(ItemInit.INJECTOR::asStack)
                    .displayItems(($1,output)-> {
                        output.accept(ItemInit.INJECTOR);
                        output.accept(ItemInit.REINFORCED_INJECTOR);
                        output.accept(ItemInit.INJECTOR_GRID);
                        output.accept(ItemInit.REINFORCED_INJECTOR_GRID);
                        output.accept(ItemInit.COPPER_COIL);
                        output.accept(ItemInit.BASIC_CATALYST);
                        output.accept(ItemInit.STARTER_CHARGE);
                        output.accept(ItemInit.COAL_DUST);
                        output.accept(ItemInit.STURDY_PROPELLER);
                        output.accept(ItemInit.COMBUSTION_CHAMBER);
                        output.accept(ItemInit.BELL_NOZZLE);
                        output.accept(ItemInit.RAW_NICKEL);
                        output.accept(ItemInit.CRUSHED_NICKEL_ORE);
                        output.accept(ItemInit.NICKEL_DUST);
                        output.accept(ItemInit.NICKEL_INGOT);
                        output.accept(ItemInit.NICKEL_NUGGET);
                        output.accept(ItemInit.NICKEL_SHEET);
                        output.accept(ItemInit.RAW_COBALT);
                        output.accept(ItemInit.CRUSHED_COBALT_ORE);
                        output.accept(ItemInit.COBALT_INGOT);
                        output.accept(ItemInit.COBALT_NUGGET);
                        output.accept(ItemInit.COBALT_SHEET);
                        output.accept(ItemInit.RAW_ALUMINUM);
                        output.accept(ItemInit.CRUSHED_ALUMINUM_ORE);
                        output.accept(ItemInit.ALUMINUM_INGOT);
                        output.accept(ItemInit.ALUMINUM_NUGGET);
                        output.accept(ItemInit.ALUMINUM_SHEET);
                    })
                    .build());
    public static final RegistryObject<CreativeModeTab> MINERALS_TAB = TAB_REGISTER.register("minerals_tab",
            ()->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.creatingspace.component_tab"))
                    .withTabsBefore(COMPONENT_TAB.getKey())
                    .icon(ItemInit.INJECTOR::asStack)
                    .displayItems(($1,output)-> {
                        output.accept(BlockInit.MOON_STONE);
                        output.accept(BlockInit.MOON_REGOLITH);
                        output.accept(BlockInit.MOON_SURFACE_REGOLITH);
                        output.accept(BlockInit.NICKEL_ORE);
                        output.accept(BlockInit.DEEPSLATE_NICKEL_ORE);
                        output.accept(BlockInit.MOON_NICKEL_ORE);
                        output.accept(BlockInit.RAW_NICKEL_BLOCK);
                        output.accept(BlockInit.MOON_COBALT_ORE);
                        output.accept(BlockInit.RAW_COBALT_BLOCK);
                        output.accept(BlockInit.MOON_ALUMINUM_ORE);
                        output.accept(BlockInit.RAW_ALUMINUM_BLOCK);
                        output.accept(FluidInit.CREATIVE_BUCKET_HYDROGEN);
                        output.accept(FluidInit.CREATIVE_BUCKET_OXYGEN);
                        output.accept(FluidInit.CREATIVE_BUCKET_METHANE);

                    })
                    .build());

    public static void init() {
    }
}
