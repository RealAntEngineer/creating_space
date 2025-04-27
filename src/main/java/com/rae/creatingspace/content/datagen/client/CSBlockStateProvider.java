package com.rae.creatingspace.content.datagen.client;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class CSBlockStateProvider extends BlockStateProvider {

    public CSBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), CreatingSpace.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
    }

    public void simpleBlock(Block block) {
        ResourceLocation blockName = ForgeRegistries.BLOCKS.getKey(block);
        ResourceLocation model = new ResourceLocation(CreatingSpace.MODID, "block/" + blockName.getPath());
        generateSimpleBlock(block, model);
    }

    private void generateSimpleBlock(Block block, ResourceLocation model) {
        ResourceLocation blockName = ForgeRegistries.BLOCKS.getKey(block);
        ModelFile modelFile = models().cubeAll(blockName.getPath(), model);
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(modelFile).build());
    }
}