package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankUtil;
import com.rae.creatingspace.content.rocket.engine.EngineItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlock;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlock;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import it.unimi.dsi.fastutil.objects.*;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class CreativeModeTabsInit {
    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreatingSpace.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MACHINE_TAB = TAB_REGISTER.register(
            "machine_tab",
            ()->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.creatingspace.machine_tab"))
                    .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
                    .icon(BlockInit.MECHANICAL_ELECTROLYZER::asStack)
                    .displayItems(new RegistrateDisplayItemsGenerator(false, CreativeModeTabsInit.MACHINE_TAB))
                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> COMPONENT_TAB = TAB_REGISTER.register(
                "component_tab",
                ()->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.creatingspace.component_tab"))
                        .withTabsBefore(MACHINE_TAB.getKey())
                        .icon(ItemInit.INJECTOR::asStack)
                        .displayItems(new RegistrateDisplayItemsGenerator(true, CreativeModeTabsInit.COMPONENT_TAB))

                    .build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MINERALS_TAB = TAB_REGISTER.register("minerals_tab",
            ()->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.creatingspace.minerals_tab"))
                            .withTabsBefore(COMPONENT_TAB.getKey())
                            .icon(BlockInit.NICKEL_ORE::asStack)
                            .displayItems(new RegistrateDisplayItemsGenerator(false, CreativeModeTabsInit.MINERALS_TAB))
                    .build());


    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }
    private static class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
        private static final Predicate<Item> IS_ITEM_3D_PREDICATE;

        static {
            MutableObject<Predicate<Item>> isItem3d = new MutableObject<>(item -> false);
            if (CatnipServices.PLATFORM.getEnv().isClient())
                isItem3d.setValue(makeClient3dItemPredicate());
            IS_ITEM_3D_PREDICATE = isItem3d.getValue();
        }

        @OnlyIn(Dist.CLIENT)
        private static Predicate<Item> makeClient3dItemPredicate() {
            return item -> {
                ItemRenderer itemRenderer = Minecraft.getInstance()
                        .getItemRenderer();
                BakedModel model = itemRenderer.getModel(new ItemStack(item), null, null, 0);
                return model.isGui3d();
            };
        }

        private final boolean addItems;
        private final DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter;
        //TODO remake this to filter bwn 3 tabs -> ores, machines and components (the rest)
        public RegistrateDisplayItemsGenerator(boolean addItems, DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter) {
            this.addItems = addItems;
            this.tabFilter = tabFilter;
        }

        private static Predicate<Item> makeExclusionPredicate() {
            Set<Item> exclusions = new ReferenceOpenHashSet<>();

            List<ItemProviderEntry<?, ?>> simpleExclusions = List.of(
                    BlockInit.ENGINE_STRUCTURAL,
                    BlockInit.BIG_ENGINE_STRUCTURAL,
                    BlockInit.SMALL_ENGINE_STRUCTURAL,
                    ItemInit.STARTER_CHARGE,
                    ItemInit.REINFORCED_INJECTOR,
                    ItemInit.REINFORCED_INJECTOR_GRID,
                    ItemInit.INJECTOR,
                    ItemInit.INJECTOR_GRID,
                    ItemInit.COPPER_BACKTANK_PLACEABLE,
                    ItemInit.NETHERITE_BACKTANK_PLACEABLE
            );

            List<ItemEntry<TagDependentIngredientItem>> tagDependentExclusions = List.of(
                    AllItems.CRUSHED_OSMIUM,
                    AllItems.CRUSHED_PLATINUM,
                    AllItems.CRUSHED_SILVER,
                    AllItems.CRUSHED_TIN,
                    AllItems.CRUSHED_LEAD,
                    AllItems.CRUSHED_QUICKSILVER,
                    AllItems.CRUSHED_BAUXITE,
                    AllItems.CRUSHED_URANIUM,
                    AllItems.CRUSHED_NICKEL
            );

            exclusions.addAll(PackageStyles.RARE_BOXES);

            for (ItemProviderEntry<?, ?> entry : simpleExclusions) {
                exclusions.add(entry.asItem());
            }

            for (ItemEntry<TagDependentIngredientItem> entry : tagDependentExclusions) {
                TagDependentIngredientItem item = entry.get();
                if (item.shouldHide()) {
                    exclusions.add(entry.asItem());
                }
            }

            return exclusions::contains;
        }

        private static List<RegistrateDisplayItemsGenerator.ItemOrdering> makeOrderings() {
            List<RegistrateDisplayItemsGenerator.ItemOrdering> orderings = new ReferenceArrayList<>();

            Map<ItemProviderEntry<?, ?>, ItemProviderEntry<?, ?>> simpleBeforeOrderings = Map.of(
                    AllItems.EMPTY_BLAZE_BURNER, AllBlocks.BLAZE_BURNER,
                    AllItems.SCHEDULE, AllBlocks.TRACK_STATION
            );

            Map<ItemProviderEntry<?, ?>, ItemProviderEntry<?, ?>> simpleAfterOrderings = Map.of(
                    AllItems.VERTICAL_GEARBOX, AllBlocks.GEARBOX
            );

            simpleBeforeOrderings.forEach((entry, otherEntry) -> {
                orderings.add(RegistrateDisplayItemsGenerator.ItemOrdering.before(entry.asItem(), otherEntry.asItem()));
            });

            simpleAfterOrderings.forEach((entry, otherEntry) -> {
                orderings.add(RegistrateDisplayItemsGenerator.ItemOrdering.after(entry.asItem(), otherEntry.asItem()));
            });

            PackageStyles.STANDARD_BOXES.forEach(item -> {
                orderings.add(RegistrateDisplayItemsGenerator.ItemOrdering.after(item, AllBlocks.PACKAGER.asItem()));
            });

            return orderings;
        }

        private static Function<Item, Collection<ItemStack>> makeStackFunc(HolderLookup.Provider holders) {
            Map<Item, Function<Item, Collection<ItemStack>>> factories = new Reference2ReferenceOpenHashMap<>();

            Map<ItemProviderEntry<?, ?>, Function<Item, ItemStack>> simpleFactories = Map.of(
                    AllItems.COPPER_BACKTANK, item -> {
                        ItemStack stack = new ItemStack(item);
                        stack.set(AllDataComponents.BACKTANK_AIR, BacktankUtil.maxAirWithoutEnchants());
                        return stack;
                    },
                    AllItems.NETHERITE_BACKTANK, item -> {
                        ItemStack stack = new ItemStack(item);
                        stack.set(AllDataComponents.BACKTANK_AIR, BacktankUtil.maxAirWithoutEnchants());
                        return stack;
                    },
                    ItemInit.COPPER_OXYGEN_BACKTANK, item -> {
                        ItemStack stack = new ItemStack(item);
                        stack.set(DataComponentsInit.OXYGEN_LEVEL, OxygenBacktankUtil.maxOxygenWithoutEnchants());
                        return stack;
                    },
                    ItemInit.NETHERITE_OXYGEN_BACKTANK, item -> {
                        ItemStack stack = new ItemStack(item);
                        stack.set(DataComponentsInit.OXYGEN_LEVEL, OxygenBacktankUtil.maxOxygenWithoutEnchants());
                        return stack;
                    }
            );

            Map<ItemProviderEntry<?,?>, Function<Item, Collection<ItemStack>>> complexFactories = Map.of(
                    BlockInit.ROCKET_ENGINE, item -> {
                        Collection<ItemStack> itemStacks = new ArrayList<>();
                        itemStacks.add(item.getDefaultInstance());
                        itemStacks.add(((EngineItem)item).getItemStackFromInfo(1000000,0.8f,3000,CreatingSpace.resource("methalox")));

                        return itemStacks;
                    },
                    BlockInit.CRYOGENIC_TANK, item -> {
                        Collection<ItemStack> itemStacks = new ArrayList<>();
                        for (Fluid fluid : BuiltInRegistries.FLUID) {
                            String fluidName = BuiltInRegistries.FLUID.getKey(fluid).toString();
                            if (fluid.getFluidType().getTemperature() < 200 && !fluidName.contains("flowing")) {
                                ItemStack itemStack = item.getDefaultInstance();
                                FluidHandlerItemStack fluidTank = (FluidHandlerItemStack) itemStack.getCapability(Capabilities.FluidHandler.ITEM);
                                if (fluidTank != null) {
                                    fluidTank.fill(new FluidStack(fluid, 4000), IFluidHandler.FluidAction.EXECUTE);
                                    itemStacks.add(itemStack);
                                }
                            }
                        }
                        return itemStacks;
                    }
            );

            simpleFactories.forEach((entry, factory) -> {
                factories.put(entry.asItem(), factory.andThen(List::of));
            });
            complexFactories.forEach((entry, factory) -> {
                factories.put(entry.asItem(), factory);
            });

            return item -> {
                Function<Item, Collection<ItemStack>> factory = factories.get(item);
                if (factory != null) {
                    return factory.apply(item);
                }
                return List.of(new ItemStack(item));
            };
        }

        private static Function<Item, CreativeModeTab.TabVisibility> makeVisibilityFunc() {
            Map<Item, CreativeModeTab.TabVisibility> visibilities = new Reference2ObjectOpenHashMap<>();

            Map<ItemProviderEntry<?, ?>, CreativeModeTab.TabVisibility> simpleVisibilities = Map.of(
                    AllItems.BLAZE_CAKE_BASE, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY
            );

            simpleVisibilities.forEach((entry, factory) -> {
                visibilities.put(entry.asItem(), factory);
            });

            //this should be unnecessary
            for (BlockEntry<ValveHandleBlock> entry : AllBlocks.DYED_VALVE_HANDLES) {
                visibilities.put(entry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
            }

            for (BlockEntry<SeatBlock> entry : AllBlocks.SEATS) {
                SeatBlock block = entry.get();
                if (block.getColor() != DyeColor.RED) {
                    visibilities.put(entry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
                }
            }

            for (BlockEntry<TableClothBlock> entry : AllBlocks.TABLE_CLOTHS) {
                TableClothBlock block = entry.get();
                if (block.getColor() != DyeColor.RED) {
                    visibilities.put(entry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
                }
            }

            for (BlockEntry<PostboxBlock> entry : AllBlocks.PACKAGE_POSTBOXES) {
                PostboxBlock block = entry.get();
                if (block.getColor() != DyeColor.WHITE) {
                    visibilities.put(entry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
                }
            }

            for (BlockEntry<ToolboxBlock> entry : AllBlocks.TOOLBOXES) {
                ToolboxBlock block = entry.get();
                if (block.getColor() != DyeColor.BROWN) {
                    visibilities.put(entry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
                }
            }

            return item -> {
                CreativeModeTab.TabVisibility visibility = visibilities.get(item);
                if (visibility != null) {
                    return visibility;
                }
                return CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
            };
        }

        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
            Predicate<Item> exclusionPredicate = makeExclusionPredicate();
            List<RegistrateDisplayItemsGenerator.ItemOrdering> orderings = makeOrderings();
            Function<Item, Collection<ItemStack>> stackFunc = makeStackFunc(parameters.holders());
            Function<Item, CreativeModeTab.TabVisibility> visibilityFunc = makeVisibilityFunc();

            List<Item> items = new LinkedList<>();
            //first 3d items
            if (addItems) {
                items.addAll(collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE.negate())));
            }
            //then blocks
            items.addAll(collectBlocks(exclusionPredicate));
            //then normal items
            if (addItems) {
                items.addAll(collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE)));
            }

            applyOrderings(items, orderings);
            outputAll(output, items, stackFunc, visibilityFunc);
        }

        private List<Item> collectBlocks(Predicate<Item> exclusionPredicate) {
            List<Item> items = new ReferenceArrayList<>();
            for (RegistryEntry<Block, Block> entry : CreatingSpace.REGISTRATE.getAll(Registries.BLOCK)) {
                if (!CreateRegistrate.isInCreativeTab(entry, tabFilter))
                    continue;
                Item item = entry.get()
                        .asItem();
                if (item == Items.AIR)
                    continue;
                if (!exclusionPredicate.test(item))
                    items.add(item);
            }
            items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
            return items;
        }

        private List<Item> collectItems(Predicate<Item> exclusionPredicate) {
            List<Item> items = new ReferenceArrayList<>();
            for (RegistryEntry<Item, Item> entry : CreatingSpace.REGISTRATE.getAll(Registries.ITEM)) {
                if (!CreateRegistrate.isInCreativeTab(entry, tabFilter))
                    continue;
                Item item = entry.get();
                if (item instanceof BlockItem)
                    continue;
                if (item instanceof SequencedAssemblyItem)
                    continue;
                if (!exclusionPredicate.test(item))
                    items.add(item);
            }
            return items;
        }

        private static void applyOrderings(List<Item> items, List<RegistrateDisplayItemsGenerator.ItemOrdering> orderings) {
            for (RegistrateDisplayItemsGenerator.ItemOrdering ordering : orderings) {
                int anchorIndex = items.indexOf(ordering.anchor());
                if (anchorIndex != -1) {
                    Item item = ordering.item();
                    int itemIndex = items.indexOf(item);
                    if (itemIndex != -1) {
                        items.remove(itemIndex);
                        if (itemIndex < anchorIndex) {
                            anchorIndex--;
                        }
                    }
                    if (ordering.type() == RegistrateDisplayItemsGenerator.ItemOrdering.Type.AFTER) {
                        items.add(anchorIndex + 1, item);
                    } else {
                        items.add(anchorIndex, item);
                    }
                }
            }
        }

        private static void outputAll(CreativeModeTab.Output output, List<Item> items, Function<Item, Collection<ItemStack>> stackFunc, Function<Item, CreativeModeTab.TabVisibility> visibilityFunc) {
            for (Item item : items) {
                output.acceptAll(stackFunc.apply(item), visibilityFunc.apply(item));
            }
        }

        private record ItemOrdering(Item item, Item anchor, RegistrateDisplayItemsGenerator.ItemOrdering.Type type) {
            public static RegistrateDisplayItemsGenerator.ItemOrdering before(Item item, Item anchor) {
                return new RegistrateDisplayItemsGenerator.ItemOrdering(item, anchor, RegistrateDisplayItemsGenerator.ItemOrdering.Type.BEFORE);
            }

            public static RegistrateDisplayItemsGenerator.ItemOrdering after(Item item, Item anchor) {
                return new RegistrateDisplayItemsGenerator.ItemOrdering(item, anchor, RegistrateDisplayItemsGenerator.ItemOrdering.Type.AFTER);
            }

            public enum Type {
                BEFORE,
                AFTER;
            }
        }
    }

}
