package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.rae.creatingspace.server.armor.OxygenBacktankUtil;
import com.rae.creatingspace.server.items.engine.EngineItem;
import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeModeTabsInit {
    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreatingSpace.MODID);

    public static final RegistryObject<CreativeModeTab> MACHINE_TAB = TAB_REGISTER.register(
            "machine_tab",
            ()->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.creatingspace.machine_tab"))
                    .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
                    .icon(BlockInit.MECHANICAL_ELECTROLYZER::asStack)
                    .displayItems(($1,output)-> {
                        output.accept(BlockInit.ROCKET_ENGINEER_TABLE);
                        output.accept(BlockInit.CLAMPS);
                        output.accept(BlockInit.ROCKET_CASING);
                        output.accept(BlockInit.SMALL_ROCKET_ENGINE);
                        output.accept(BlockInit.BIG_ROCKET_ENGINE);
                        output.acceptAll(makeStackEngineListFunc().apply(BlockInit.ROCKET_ENGINE.asItem()));
                        output.accept(BlockInit.ROCKET_CONTROLS);
                        output.accept(BlockInit.CATALYST_CARRIER);
                        output.accept(BlockInit.MECHANICAL_ELECTROLYZER);
                        output.accept(BlockInit.ROCKET_GENERATOR);
                        output.accept(BlockInit.FLOW_METER);
                        output.accept(BlockInit.OXYGEN_SEALER);
                        output.accept(BlockInit.AIR_LIQUEFIER);
                        output.accept(BlockInit.FLIGHT_RECORDER);
                        output.acceptAll(makeStackCryoTankListFunc().apply(BlockInit.CRYOGENIC_TANK.asItem()));
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
                        output.accept(ItemInit.COPPER_COIL);
                        output.accept(ItemInit.BASIC_CATALYST);
                        output.accept(ItemInit.COAL_DUST);
                        output.accept(ItemInit.STURDY_PROPELLER);
                        ItemInit.COMBUSTION_CHAMBER.forEach(output::accept);
                        ItemInit.BELL_NOZZLE.forEach(output::accept);
                        ItemInit.AEROSPIKE_PLUG.forEach(output::accept);
                        ItemInit.AEROSPIKE_PLUG.forEach(output::accept);
                        ItemInit.EXHAUST_PACK.forEach(output::accept);
                        ItemInit.POWER_PACK.forEach(output::accept);
                        ItemInit.ENGINE_INGREDIENTS.forEach(output::accept);
                        ItemInit.METALS_INGREDIENTS.forEach(output::accept);
                        output.accept(ItemInit.RAW_NICKEL);
                        //output.accept(ItemInit.CRUSHED_NICKEL_ORE);
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
                        //output.accept(ItemInit.CRUSHED_ALUMINUM_ORE);
                        output.accept(ItemInit.ALUMINUM_INGOT);
                        output.accept(ItemInit.ALUMINUM_NUGGET);
                        output.accept(ItemInit.ALUMINUM_SHEET);
                        output.accept(ItemInit.ALUMINUM_SHEET);
                        output.accept(ItemInit.BASIC_SPACESUIT_FABRIC);
                        output.accept(ItemInit.ADVANCED_SPACESUIT_FABRIC);
                        output.accept(ItemInit.BASIC_SPACESUIT_HELMET);
                        output.accept(makeStackBackTankFunc().apply(ItemInit.COPPER_OXYGEN_BACKTANK.get()));
                        output.accept(ItemInit.BASIC_SPACESUIT_LEGGINGS);
                        output.accept(ItemInit.BASIC_SPACESUIT_BOOTS);
                        output.accept(ItemInit.ADVANCED_SPACESUIT_HELMET);
                        output.accept(makeStackBackTankFunc().apply(ItemInit.NETHERITE_OXYGEN_BACKTANK.get()));
                        output.accept(ItemInit.ADVANCED_SPACESUIT_LEGGINGS);
                        output.accept(ItemInit.ADVANCED_SPACESUIT_BOOTS);

                    })
                    .build());
    public static final RegistryObject<CreativeModeTab> MINERALS_TAB = TAB_REGISTER.register("minerals_tab",
            ()->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.creatingspace.minerals_tab"))
                    .withTabsBefore(COMPONENT_TAB.getKey())
                    .icon(BlockInit.NICKEL_ORE::asStack)
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
                        //output.accept(FluidInit.CREATIVE_BUCKET_HYDROGEN);
                        //output.accept(FluidInit.CREATIVE_BUCKET_OXYGEN);
                        //output.accept(FluidInit.CREATIVE_BUCKET_METHANE);
                    })
                    .build());

    private static Function<Item, ItemStack> makeStackBackTankFunc() {
        Map<Item, Function<Item, ItemStack>> factories = new Reference2ReferenceOpenHashMap<>();

        Map<ItemProviderEntry<?>, Function<Item, ItemStack>> simpleFactories = Map.of(
                ItemInit.COPPER_OXYGEN_BACKTANK, item -> {
                    ItemStack stack = new ItemStack(item);
                    CompoundTag nbt = new CompoundTag();
                    nbt.putFloat("Oxygen", OxygenBacktankUtil.maxOxygenWithoutEnchants());
                    nbt.putFloat("prevOxygen",OxygenBacktankUtil.maxOxygenWithoutEnchants());
                    stack.setTag(nbt);
                    return stack;
                },
                ItemInit.NETHERITE_OXYGEN_BACKTANK, item -> {
                    ItemStack stack = new ItemStack(item);
                    CompoundTag nbt = new CompoundTag();
                    nbt.putFloat("Oxygen", OxygenBacktankUtil.maxOxygenWithoutEnchants());
                    nbt.putFloat("prevOxygen",OxygenBacktankUtil.maxOxygenWithoutEnchants());
                    stack.setTag(nbt);
                    return stack;
                }
        );

        simpleFactories.forEach((entry, factory) -> {
            factories.put(entry.asItem(), factory);
        });

        return item -> {
            Function<Item, ItemStack> factory = factories.get(item);
            if (factory != null) {
                return factory.apply(item);
            }
            return new ItemStack(item);
        };
    }

    private static Function<Item, Collection<ItemStack>> makeStackCryoTankListFunc() {
        Map<Item, Function<Item, Collection<ItemStack>>> factories = new Reference2ReferenceOpenHashMap<>();

        Map<ItemProviderEntry<?>, Function<Item, Collection<ItemStack>>> simpleFactories = Map.of(
            BlockInit.CRYOGENIC_TANK, item -> {
                Collection<ItemStack> itemStacks = new ArrayList<>();
                    for (Fluid fluid : ForgeRegistries.FLUIDS) {
                        String fluidName = ForgeRegistries.FLUIDS.getKey(fluid).toString();
                        if (fluid.getFluidType().getTemperature() < 200 && !fluidName.contains("flowing")) {
                            ItemStack itemStack = item.getDefaultInstance();
                            CompoundTag tag = itemStack.getOrCreateTag();
                            FluidStack fluidStack = new FluidStack(fluid, 4000);
                            tag.put(FLUID_NBT_KEY, fluidStack.writeToNBT(new CompoundTag()));
                            itemStack.setTag(tag);
                            itemStacks.add(itemStack);
                        }
                    }
                    return itemStacks;
                }
        );

        simpleFactories.forEach((entry, factory) -> {
            factories.put(entry.asItem(), factory);
        });

        return item -> {
            Function<Item, Collection<ItemStack>> factory = factories.get(item);
            if (factory != null) {
                return factory.apply(item);
            }
            return Collections.singleton(new ItemStack(item));
        };
    }
    private static Function<Item, Collection<ItemStack>> makeStackEngineListFunc() {
        Map<Item, Function<Item, Collection<ItemStack>>> factories = new Reference2ReferenceOpenHashMap<>();

        Map<ItemProviderEntry<?>, Function<Item, Collection<ItemStack>>> simpleFactories = Map.of(
                BlockInit.ROCKET_ENGINE, item -> {
                    Collection<ItemStack> itemStacks = new ArrayList<>();
                            itemStacks.add(item.getDefaultInstance());
                            itemStacks.add(((EngineItem)item).getItemStackFromInfo(1000000,0.8f,3000,new ResourceLocation("creatingspace:methalox")));


                    return itemStacks;
                }
        );

        simpleFactories.forEach((entry, factory) -> {
            factories.put(entry.asItem(), factory);
        });

        return item -> {
            Function<Item, Collection<ItemStack>> factory = factories.get(item);
            if (factory != null) {
                return factory.apply(item);
            }
            return Collections.singleton(new ItemStack(item));
        };
    }

    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }

}
