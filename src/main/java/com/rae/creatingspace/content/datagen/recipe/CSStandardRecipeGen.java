package com.rae.creatingspace.content.datagen.recipe;

import com.jcraft.jorbis.Block;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.datagen.CSMetalSets;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CSStandardRecipeGen extends RecipeProvider {
    private String suffix;

    public CSStandardRecipeGen(PackOutput output, CompletableFuture
            <HolderLookup.Provider> helper) {
        super(output, helper);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        Marker RESOURCES = enterFolder("resources");


        for (CSMetalSets.MetalSet metal : CSMetalSets.ALL) {
            CSMetalRecipeHelper.generateMetalRecipes(recipeOutput, metal);
        }

        /*
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.ALUMINUM_NUGGET.get(), 9)
                .requires(AllTags.commonItemTag("ingots/aluminum"))
                .unlockedBy("has_aluminum_ingot", has(ItemInit.ALUMINUM_INGOT)).save(recipeOutput, createSimpleLocation("aluminum_nugget"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.ALUMINUM_INGOT.get(), 9)
                .requires(AllTags.commonItemTag("storage_blocks/aluminum"))
                .unlockedBy("has_aluminum_ingot", has(ItemInit.ALUMINUM_INGOT)).save(recipeOutput, withSuffix("_from_block").createLocation("aluminum_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemInit.ALUMINUM_INGOT.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("nuggets/aluminum"))
                .unlockedBy("has_aluminum_ingot", has(ItemInit.ALUMINUM_INGOT))
                .save(recipeOutput, withSuffix("_from_nuggets").createLocation("aluminum_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.ALUMINUM_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("ingots/aluminum"))
                .unlockedBy("has_aluminum_ingot", has(ItemInit.ALUMINUM_INGOT))
                .save(recipeOutput, createSimpleLocation("aluminum_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.NICKEL_NUGGET.get(), 9)
                .requires(AllTags.commonItemTag("ingots/nickel"))
                .unlockedBy("has_nickel_ingot", has(ItemInit.NICKEL_INGOT)).save(recipeOutput, createSimpleLocation("nickel_nugget"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.NICKEL_INGOT.get(), 9)
                .requires(AllTags.commonItemTag("storage_blocks/nickel"))
                .unlockedBy("has_nickel_ingot", has(ItemInit.NICKEL_INGOT)).save(recipeOutput, withSuffix("_from_block").createLocation("nickel_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemInit.NICKEL_INGOT.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("nuggets/nickel"))
                .unlockedBy("has_nickel_ingot", has(ItemInit.NICKEL_INGOT))
                .save(recipeOutput, withSuffix("_from_nuggets").createLocation("nickel_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.NICKEL_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("ingots/nickel"))
                .unlockedBy("has_nickel_ingot", has(ItemInit.NICKEL_INGOT))
                .save(recipeOutput, createSimpleLocation("nickel_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.COBALT_NUGGET.get(), 9)
                .requires(AllTags.commonItemTag("ingots/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT)).save(recipeOutput, createSimpleLocation("cobalt_nugget"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemInit.COBALT_INGOT.get(), 9)
                .requires(AllTags.commonItemTag("storage_blocks/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT)).save(recipeOutput, withSuffix("_from_block").createLocation("cobalt_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemInit.COBALT_INGOT.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("nuggets/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT))
                .save(recipeOutput, withSuffix("_from_nuggets").createLocation("cobalt_ingot"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.COBALT_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', AllTags.commonItemTag("ingots/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(ItemInit.COBALT_INGOT))
                .save(recipeOutput, createSimpleLocation("cobalt_block"));
         */

        Marker ARMOR = enterFolder("armor");

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemInit.ADVANCED_SPACESUIT_BOOTS.get())
                .pattern("F F")
                .pattern("F F")
                .define('F', ItemInit.ADVANCED_SPACESUIT_FABRIC.get())
                .unlockedBy("has_advanced_spacesuit_fabric", has(ItemInit.ADVANCED_SPACESUIT_FABRIC))
                .save(recipeOutput, createSimpleLocation("advanced_spacesuit_boots"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemInit.ADVANCED_SPACESUIT_HELMET.get())
                .pattern("FFF")
                .pattern("FGF")
                .define('F', ItemInit.ADVANCED_SPACESUIT_FABRIC.get())
                .define('G', AllTags.commonItemTag("plates/gold"))
                .unlockedBy("has_advanced_spacesuit_fabric", has(ItemInit.ADVANCED_SPACESUIT_FABRIC))
                .save(recipeOutput, createSimpleLocation("advanced_spacesuit_helmet"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemInit.ADVANCED_SPACESUIT_LEGGINGS.get())
                .pattern("FFF")
                .pattern("F F")
                .pattern("F F")
                .define('F', ItemInit.ADVANCED_SPACESUIT_FABRIC.get())
                .unlockedBy("has_advanced_spacesuit_fabric", has(ItemInit.ADVANCED_SPACESUIT_FABRIC))
                .save(recipeOutput, createSimpleLocation("advanced_spacesuit_leggings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemInit.BASIC_SPACESUIT_BOOTS.get())
                .pattern("F F")
                .pattern("F F")
                .define('F', ItemInit.BASIC_SPACESUIT_FABRIC.get())
                .unlockedBy("has_basic_spacesuit_fabric", has(ItemInit.BASIC_SPACESUIT_FABRIC))
                .save(recipeOutput, createSimpleLocation("basic_spacesuit_boots"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemInit.BASIC_SPACESUIT_HELMET.get())
                .pattern("FFF")
                .pattern("FGF")
                .define('F', ItemInit.BASIC_SPACESUIT_FABRIC.get())
                .define('G', AllTags.commonItemTag("plates/gold"))
                .unlockedBy("has_basic_spacesuit_fabric", has(ItemInit.BASIC_SPACESUIT_FABRIC))
                .save(recipeOutput, createSimpleLocation("basic_spacesuit_helmet"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemInit.BASIC_SPACESUIT_LEGGINGS.get())
                .pattern("FFF")
                .pattern("F F")
                .pattern("F F")
                .define('F', ItemInit.BASIC_SPACESUIT_FABRIC.get())
                .unlockedBy("has_basic_spacesuit_fabric", has(ItemInit.BASIC_SPACESUIT_FABRIC))
                .save(recipeOutput, createSimpleLocation("basic_spacesuit_leggings"));

        Marker MACHINES = enterFolder("machines");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.AIR_LIQUEFIER.get())
                .pattern(" S ")
                .pattern("CPC")
                .pattern(" T ")
                .define('S', AllTags.commonItemTag("plates/brass"))
                .define('T', AllBlocks.FLUID_TANK.get())
                .define('C', AllBlocks.BRASS_CASING.get())
                .define('P', AllBlocks.ENCASED_FAN.get())
                .unlockedBy("has_fluid_tank", has(AllBlocks.FLUID_TANK.get()))
                .save(recipeOutput, createSimpleLocation("air_liquefier"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.CATALYST_CARRIER.get())
                .pattern(" G ")
                .pattern("NPN")
                .pattern(" G ")
                .define('N', AllTags.commonItemTag("ingots/nickel"))
                .define('G', AllTags.commonItemTag("plates/gold"))
                .define('P', AllBlocks.MECHANICAL_PRESS.get())
                .unlockedBy("has_mechanical_press", has(AllBlocks.MECHANICAL_PRESS.get()))
                .save(recipeOutput, createSimpleLocation("catalyst_carrier"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.CLAMPS.get(), 4)
                .pattern("ICI")
                .pattern("CIC")
                .pattern("ICI")
                .define('I', AllTags.commonItemTag("storage_blocks/iron"))
                .define('C', AllBlocks.COPPER_CASING.get())
                .unlockedBy("has_copper_casing", has(AllBlocks.COPPER_CASING.get()))
                .save(recipeOutput, createSimpleLocation("clamps"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.CRYOGENIC_TANK.get())
                .pattern("NWN")
                .pattern("WTW")
                .pattern("NWN")
                .define('N', AllTags.commonItemTag("plates/nickel"))
                .define('W', Items.RED_WOOL)
                .define('T', AllBlocks.FLUID_TANK.get())
                .unlockedBy("has_fluid_tank", has(AllBlocks.FLUID_TANK.get()))
                .save(recipeOutput, createSimpleLocation("cryogenic_tank"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.FLIGHT_RECORDER.get())
                .pattern(" B ")
                .pattern("AKA")
                .pattern(" B ")
                .define('B', AllBlocks.BRASS_CASING.get())
                .define('A', AllBlocks.SHAFT.get())
                .define('K', Items.DRIED_KELP_BLOCK)
                .unlockedBy("has_brass_casing", has(AllBlocks.BRASS_CASING.get()))
                .save(recipeOutput, createSimpleLocation("flight_recorder"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.FLOW_METER.get())
                .pattern("G")
                .pattern("C")
                .define('C', AllBlocks.COPPER_CASING.get())
                .define('G', Items.COMPASS)
                .unlockedBy("has_copper_casing", has(AllBlocks.COPPER_CASING.get()))
                .save(recipeOutput, createSimpleLocation("flow_meter"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.MECHANICAL_ELECTROLYZER.get())
                .pattern("XSX")
                .pattern("CCC")
                .pattern("GTG")
                .define('C', ItemInit.COPPER_COIL.get())
                .define('X', AllBlocks.COPPER_CASING.get())
                .define('T', AllBlocks.FLUID_TANK.get())
                .define('S', AllBlocks.SHAFT.get())
                .define('G', AllTags.commonItemTag("plates/gold"))
                .unlockedBy("has_fluid_tank", has(AllBlocks.FLUID_TANK.get()))
                .save(recipeOutput, createSimpleLocation("mechanical_electrolyzer"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.OXYGEN_SEALER.get())
                .pattern("NPN")
                .pattern("CTC")
                .pattern("CCC")
                .define('P', AllItems.PROPELLER.get())
                .define('C', AllBlocks.COPPER_CASING.get())
                .define('T', AllBlocks.FLUID_TANK.get())
                .define('N', AllTags.commonItemTag("plates/nickel"))
                .unlockedBy("has_fluid_tank", has(AllBlocks.FLUID_TANK.get()))
                .save(recipeOutput, createSimpleLocation("oxygen_sealer"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.ROCKET_CASING.get())
                .pattern("CSC")
                .pattern("SCS")
                .pattern("CSC")
                .define('S', AllTags.commonItemTag("plates/aluminum"))
                .define('C', AllTags.commonItemTag("ingots/cobalt"))
                .unlockedBy("has_cobalt_ingot", has(AllTags.commonItemTag("ingots/cobalt")))
                .save(recipeOutput, createSimpleLocation("rocket_casing"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BlockInit.ROCKET_CONTROLS.get())
                .requires(BlockInit.ROCKET_CONTROLS.get())
                .unlockedBy("has_rocket_controls", has(BlockInit.ROCKET_CONTROLS.get()))
                .save(recipeOutput, withSuffix("_reset").createLocation("rocket_controls"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.ROCKET_CONTROLS.get())
                .pattern("ERE")
                .pattern("ETE")
                .pattern("SSS")
                .define('E', AllItems.ELECTRON_TUBE.get())
                .define('R', AllBlocks.REDSTONE_LINK.get())
                .define('T', AllBlocks.TRAIN_CONTROLS.get())
                .define('S', AllItems.STURDY_SHEET.get())
                .unlockedBy("has_train_controls", has(AllBlocks.TRAIN_CONTROLS.get()))
                .save(recipeOutput, createSimpleLocation("rocket_controls"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockInit.ROCKET_ENGINEER_TABLE.get())
                .pattern("WWW")
                .pattern("WWW")
                .pattern(" S ")
                .define('W', ItemTags.WOODEN_SLABS)
                .define('S', Items.SMOOTH_STONE)
                .unlockedBy("has_smooth_stone", has(Items.SMOOTH_STONE))
                .save(recipeOutput, createSimpleLocation("rocket_engineer_table"));

        Marker MISC = enterFolder("misc");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemInit.COPPER_COIL.get())
                .pattern("CCC")
                .pattern("C C")
                .pattern("CCC")
                .define('C', AllTags.commonItemTag("ingots/copper"))
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(recipeOutput, createSimpleLocation("copper_coil"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemInit.STARTER_CHARGE.get())
                .pattern("PGP")
                .pattern("PGP")
                .pattern("PGP")
                .define('P', Items.PAPER)
                .define('G', Items.GUNPOWDER)
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
                .save(recipeOutput, createSimpleLocation("starter_charge"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemInit.STURDY_PROPELLER.get())
                .pattern(" S ")
                .pattern("SIS")
                .pattern(" S ")
                .define('I', AllTags.commonItemTag("ingots/iron"))
                .define('S', AllItems.STURDY_SHEET)
                .unlockedBy("has_sturdy_sheet", has(AllItems.STURDY_SHEET.get()))
                .save(recipeOutput, createSimpleLocation("sturdy_propeller"));
    }



    CSStandardRecipeGen withSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    String currentFolder = "";

    Marker enterFolder(String folder) {
        currentFolder = folder;
        return new Marker();
    }

    protected static class Marker {
    }
    private ResourceLocation createSimpleLocation(String recipeName){
        return ResourceLocation.fromNamespaceAndPath(CreatingSpace.MODID,currentFolder + "/" + recipeName);
    }

    private ResourceLocation createLocation(String recipeName) {
        return ResourceLocation.fromNamespaceAndPath(CreatingSpace.MODID, currentFolder + "/" + recipeName + suffix);
    }

}