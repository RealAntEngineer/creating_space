package com.rae.creatingspace.content.datagen;

import com.rae.creatingspace.init.ingameobject.ItemInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static com.rae.creatingspace.CreatingSpace.resource;

@SuppressWarnings("unused")
public class CSLootTableProvider extends LootTableProvider {
    public CSLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(CSTreasureLootTables::new, LootContextParamSets.CHEST)
        ), registries);
    }

    private static class CSTreasureLootTables implements LootTableSubProvider {
        private final HolderLookup.Provider registries;

        public CSTreasureLootTables(HolderLookup.Provider registries) {
            this.registries = registries;
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> p_251236_) {
            p_251236_.accept(register("chests/crashed_rocket_loot"),
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(blueprint("creatingspace:ox_rich_staged_cycle", "creatingspace:power_pack_type"))
                                    .add(blueprint("creatingspace:fuel_rich_staged_cycle", "creatingspace:power_pack_type"))
                                    .add(blueprint("creatingspace:aerospike", "creatingspace:exhaust_pack_type"))
                                    .add(blueprint("creatingspace:full_flow_staged_cycle", "creatingspace:power_pack_type"))
                            )
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(ItemInit.NICKEL_INGOT.get())
                                            .setWeight(2)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 6.0F)))
                                    )
                            )
            );

            p_251236_.accept(register("chests/abandoned_moon_base_loot"),
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(blueprint("creatingspace:ox_rich_staged_cycle", "creatingspace:power_pack_type"))
                                    .add(blueprint("creatingspace:fuel_rich_staged_cycle", "creatingspace:power_pack_type"))
                                    .add(blueprint("creatingspace:aerospike", "creatingspace:exhaust_pack_type"))
                                    .add(blueprint("creatingspace:full_flow_staged_cycle", "creatingspace:power_pack_type"))
                            )
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(ItemInit.COBALT_INGOT.get())
                                            .setWeight(2)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                    )
                            )
            );

            p_251236_.accept(register("chests/underground_mars_outpost"),
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(blueprint("creatingspace:catalyse_cycle", "creatingspace:power_pack_type"))
                            )
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(ItemInit.COBALT_INGOT.get())
                                            .setWeight(2)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 6.0F)))
                                    )
                            )
            );
        }

        private ResourceKey<LootTable> register(String path) {
            return ResourceKey.create(Registries.LOOT_TABLE, resource(path));
        }

        private LootItem.Builder blueprint(String design, String designType) {
            CompoundTag customDataTag = new CompoundTag();
            customDataTag.putString("design", design);
            customDataTag.putString("design_type", designType);
            CustomData customDataComponent = CustomData.of(customDataTag);

            return LootItem.lootTableItem(ItemInit.DESIGN_BLUEPRINT.get())
                    .apply(SetComponentsFunction.setComponent(DataComponents.CUSTOM_DATA, customDataComponent));
        }
    }
}
