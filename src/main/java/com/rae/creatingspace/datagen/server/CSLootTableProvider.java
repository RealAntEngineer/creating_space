package com.rae.creatingspace.datagen.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class CSLootTableProvider extends LootTableProvider {
    public CSLootTableProvider (DataGenerator generator){
        super(generator);
    }

    public void generateLootTable(BiConsumer<ResourceLocation, LootTable> consumer, Block block) {
        new BlockLoot() {
            @Override
            protected void addTables() {
                dropSelf(block);
            }

            @Override
            protected Iterable<Block> getKnownBlocks() {
                return Set.of(block);
            }

            // Overriding the generate method to accept the consumer
            public void generate(BiConsumer<ResourceLocation, LootTable> consumer) {
                addTables();
                getKnownBlocks().forEach(b -> {
                    LootTable.Builder tableBuilder = createSingleItemTable(b);
                    consumer.accept(b.getLootTable(), tableBuilder.build());
                });
            }
        }.generate(consumer);
    }
}
