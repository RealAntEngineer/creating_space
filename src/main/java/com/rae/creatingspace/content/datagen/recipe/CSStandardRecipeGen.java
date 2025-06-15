package com.rae.creatingspace.content.datagen.recipe;

import com.google.common.base.Supplier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.content.datagen.CSMetalSets;
import com.rae.creatingspace.content.datagen.CSRecipeProvider;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.recipe.CompatMetals;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.data.recipe.PressingRecipeGen;
import com.simibubi.create.foundation.mixin.accessor.MappedRegistryAccessor;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.rae.creatingspace.CreatingSpace.resource;

public class CSStandardRecipeGen extends CSRecipeProvider {

        private Marker RESOURCES = enterFolder("resources");

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


        private Marker ARMOR = enterFolder("armor");

        GeneratedRecipe

                ADVANCED_SPACESUIT_BOOTS = create(ItemInit.ADVANCED_SPACESUIT_BOOTS).returns(1)
                .unlockedBy(ItemInit.ADVANCED_SPACESUIT_FABRIC::get)
                .viaShaped(b -> b.define('F', ItemInit.ADVANCED_SPACESUIT_FABRIC::get)
                        .pattern("F F")
                        .pattern("F F")),

                ADVANCED_SPACESUIT_HELMET = create(ItemInit.ADVANCED_SPACESUIT_HELMET).returns(1)
                        .unlockedBy(ItemInit.ADVANCED_SPACESUIT_FABRIC::get)
                        .viaShaped(b -> b.define('F', ItemInit.ADVANCED_SPACESUIT_FABRIC::get)
                                .define('G', AllItems.GOLDEN_SHEET::get)
                                .pattern("FFF")
                                .pattern("FGF")),

                ADVANCED_SPACESUIT_LEGGINGS = create(ItemInit.ADVANCED_SPACESUIT_LEGGINGS).returns(1)
                        .unlockedBy(ItemInit.ADVANCED_SPACESUIT_FABRIC::get)
                        .viaShaped(b -> b.define('F', ItemInit.ADVANCED_SPACESUIT_FABRIC::get)
                                .pattern("FFF")
                                .pattern("F F")
                                .pattern("F F")),

                BASIC_SPACESUIT_BOOTS = create(ItemInit.BASIC_SPACESUIT_BOOTS).returns(1)
                        .unlockedBy(ItemInit.BASIC_SPACESUIT_FABRIC::get)
                        .viaShaped(b -> b.define('F', ItemInit.BASIC_SPACESUIT_FABRIC::get)
                                .pattern("F F")
                                .pattern("F F")),

                BASIC_SPACESUIT_HELMET = create(ItemInit.BASIC_SPACESUIT_HELMET).returns(1)
                        .unlockedBy(ItemInit.BASIC_SPACESUIT_FABRIC::get)
                        .viaShaped(b -> b.define('F', ItemInit.BASIC_SPACESUIT_FABRIC::get)
                                .define('G', AllItems.GOLDEN_SHEET::get)
                                .pattern("FFF")
                                .pattern("FGF")),

                BASIC_SPACESUIT_LEGGINGS = create(ItemInit.BASIC_SPACESUIT_LEGGINGS).returns(1)
                        .unlockedBy(ItemInit.BASIC_SPACESUIT_FABRIC::get)
                        .viaShaped(b -> b.define('F', ItemInit.BASIC_SPACESUIT_FABRIC::get)
                                .pattern("FFF")
                                .pattern("F F")
                                .pattern("F F"))
        ;

        Marker MACHINES = enterFolder("machines");

        GeneratedRecipe

                AIR_LIQUIFIER = create(BlockInit.AIR_LIQUEFIER).returns(1)
                .unlockedBy(AllBlocks.FLUID_TANK::get)
                .viaShaped(b -> b.define('S', AllItems.BRASS_SHEET::get)
                        .define('T', AllBlocks.FLUID_TANK.get())
                        .define('C', AllBlocks.BRASS_CASING.get())
                        .define('P', AllBlocks.ENCASED_FAN.get())
                        .pattern(" S ")
                        .pattern("CPC")
                        .pattern(" T ")),

                CATALYST_CARRIER = create(BlockInit.CATALYST_CARRIER).returns(1)
                        .unlockedBy(AllBlocks.MECHANICAL_PRESS::get)
                        .viaShaped(b -> b
                                .define('N', AllTags.commonItemTag("ingots/nickel"))
                                .define('G', AllTags.commonItemTag("plates/gold"))
                                .define('P', AllBlocks.MECHANICAL_PRESS.get())
                                .pattern(" G ")
                                .pattern("NPN")
                                .pattern(" G ")),

                CLAMPS = create(BlockInit.CLAMPS).returns(4)
                        .unlockedBy(AllBlocks.COPPER_CASING::get)
                        .viaShaped(b -> b
                                .define('I', Blocks.IRON_BLOCK)
                                .define('C', AllBlocks.COPPER_CASING.get())
                                .pattern("ICI")
                                .pattern("CIC")
                                .pattern("ICI")),

                CRYOGENIC_TANK = create(BlockInit.CRYOGENIC_TANK).returns(1)
                        .unlockedBy(AllBlocks.FLUID_TANK::get)
                        .viaShaped(b -> b
                                .define('N', AllTags.commonItemTag("plates/nickel"))
                                .define('W', Items.RED_WOOL)
                                .define('T', AllBlocks.FLUID_TANK.get())
                                .pattern("NWN")
                                .pattern("WTW")
                                .pattern("NWN")),

                FLIGHT_RECORDER = create(BlockInit.FLIGHT_RECORDER).returns(1)
                        .unlockedBy(AllBlocks.BRASS_CASING::get)
                        .viaShaped(b -> b
                                .define('B', AllBlocks.BRASS_CASING.get())
                                .define('A', AllBlocks.SHAFT.get())
                                .define('K', Items.DRIED_KELP_BLOCK)
                                .pattern(" B ")
                                .pattern("AKA")
                                .pattern(" B ")),

                FLOW_METER = create(BlockInit.FLOW_METER).returns(1)
                        .unlockedBy(AllBlocks.COPPER_CASING::get)
                        .viaShaped(b -> b
                                .define('C', AllBlocks.COPPER_CASING.get())
                                .define('G', Items.COMPASS)
                                .pattern("G")
                                .pattern("C")),

                MECHANICAL_ELECTROLYZER = create(BlockInit.MECHANICAL_ELECTROLYZER).returns(1)
                        .unlockedBy(AllBlocks.FLUID_TANK::get)
                        .viaShaped(b -> b
                                .define('C', ItemInit.COPPER_COIL.get())
                                .define('X', AllBlocks.COPPER_CASING.get())
                                .define('T', AllBlocks.FLUID_TANK.get())
                                .define('S', AllBlocks.SHAFT.get())
                                .define('G', AllTags.commonItemTag("plates/gold"))
                                .pattern("XSX")
                                .pattern("CCC")
                                .pattern("GTG")),

                OXYGEN_SEALER = create(BlockInit.OXYGEN_SEALER).returns(1)
                        .unlockedBy(AllBlocks.FLUID_TANK::get)
                        .viaShaped(b -> b
                                .define('P', AllItems.PROPELLER.get())
                                .define('C', AllBlocks.COPPER_CASING.get())
                                .define('T', AllBlocks.FLUID_TANK.get())
                                .define('N', AllTags.commonItemTag("plates/nickel"))
                                .pattern("NPN")
                                .pattern("CTC")
                                .pattern("CCC")),

                ROCKET_CASING = create(BlockInit.ROCKET_CASING).returns(1)
                        .unlockedBy(ItemInit.COBALT_INGOT::get)
                        .viaShaped(b -> b
                                .define('S', AllTags.commonItemTag("plates/aluminum"))
                                .define('C', AllTags.commonItemTag("ingots/cobalt"))
                                .pattern("CSC")
                                .pattern("SCS")
                                .pattern("CSC")),

                ROCKET_CONTROLS_RESET = create(BlockInit.ROCKET_CONTROLS).withSuffix("_reset").returns(1)
                        .unlockedBy(BlockInit.ROCKET_CONTROLS::get)
                        .viaShapeless(b -> b
                                .requires(BlockInit.ROCKET_CONTROLS.get())),

                ROCKET_CONTROLS = create(BlockInit.ROCKET_CONTROLS).returns(1)
                        .unlockedBy(AllBlocks.TRAIN_CONTROLS::get)
                        .viaShaped(b -> b
                                .define('E', AllItems.ELECTRON_TUBE.get())
                                .define('R', AllBlocks.REDSTONE_LINK.get())
                                .define('T', AllBlocks.TRAIN_CONTROLS.get())
                                .define('S', AllItems.STURDY_SHEET.get())
                                .pattern("ERE")
                                .pattern("ETE")
                                .pattern("SSS")),

                ROCKET_ENGINEER_TABLE = create(BlockInit.ROCKET_ENGINEER_TABLE).returns(1)
                        .unlockedBy(BlockInit.ROCKET_ENGINEER_TABLE::get)
                        .viaShaped(b -> b
                                .define('W', ItemTags.WOODEN_SLABS)
                                .define('S', Items.SMOOTH_STONE)
                                .pattern("WWW")
                                .pattern("WWW")
                                .pattern(" S "))
        ;

        private Marker MISC = enterFolder("misc");

        GeneratedRecipe

                COPPER_COIL = create(ItemInit.COPPER_COIL).returns(1)
                .unlockedBy(ItemInit.COPPER_COIL::get)
                .viaShaped(b -> b
                        .define('C', AllTags.commonItemTag("ingots/copper"))
                        .pattern("CCC")
                        .pattern("C C")
                        .pattern("CCC")),

                STARTER_CHARGE = create(ItemInit.STARTER_CHARGE).returns(1)
                        .unlockedBy(ItemInit.STARTER_CHARGE::get)
                        .viaShaped(b -> b
                                .define('P', Items.PAPER)
                                .define('G', Items.GUNPOWDER)
                                .pattern("PGP")
                                .pattern("PGP")
                                .pattern("PGP")),

                STURDY_PROPELLER = create(ItemInit.STURDY_PROPELLER).returns(1)
                        .unlockedBy(AllItems.STURDY_SHEET::get)
                        .viaShaped(b -> b
                                .define('I', Items.IRON_INGOT)
                                .define('S', AllItems.STURDY_SHEET)
                                .pattern(" S ")
                                .pattern("SIS")
                                .pattern(" S "));

    String currentFolder = "";

    Marker enterFolder(String folder) {
        currentFolder = folder;
        return new Marker();
    }

    GeneratedRecipeBuilder create(Supplier<ItemLike> result) {
        return new GeneratedRecipeBuilder(currentFolder, result);
    }

    GeneratedRecipeBuilder create(ResourceLocation result) {
        return new GeneratedRecipeBuilder(currentFolder, result);
    }

    GeneratedRecipeBuilder create(ItemProviderEntry<? extends ItemLike, ? extends ItemLike> result) {
        return create(result::get);
    }

    GeneratedRecipe createSpecial(Function<CraftingBookCategory, Recipe<?>> builder, String recipeType, String path) {
        ResourceLocation location = resource(recipeType + "/" + currentFolder + "/" + path);
        return register(consumer -> {
            SpecialRecipeBuilder b = SpecialRecipeBuilder.special(builder);
            b.save(consumer, location.toString());
        });
    }

    GeneratedRecipe blastCrushedMetal(Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> ingredient) {
        return create(result::get).withSuffix("_from_crushed")
                .viaCooking(ingredient)
                .rewardXP(.1f)
                .inBlastFurnace();
    }

    GeneratedRecipe blastModdedCrushedMetal(ItemEntry<? extends Item> ingredient, CompatMetals metal) {
        for (Mods mod : metal.getMods()) {
            String metalName = metal.getName(mod);
            ResourceLocation ingot = mod.ingotOf(metalName);
            String modId = mod.getId();
            create(ingot).withSuffix("_compat_" + modId)
                    .whenModLoaded(modId)
                    .viaCooking(ingredient::get)
                    .rewardXP(.1f)
                    .inBlastFurnace();
        }
        return null;
    }

    GeneratedRecipe recycleGlass(BlockEntry<? extends Block> ingredient) {
        return create(() -> Blocks.GLASS).withSuffix("_from_" + ingredient.getId()
                        .getPath())
                .viaCooking(ingredient::get)
                .forDuration(50)
                .inFurnace();
    }

    GeneratedRecipe recycleGlassPane(BlockEntry<? extends Block> ingredient) {
        return create(() -> Blocks.GLASS_PANE).withSuffix("_from_" + ingredient.getId()
                        .getPath())
                .viaCooking(ingredient::get)
                .forDuration(50)
                .inFurnace();
    }

    GeneratedRecipe metalCompacting(List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>> variants,
                                                         List<Supplier<TagKey<Item>>> ingredients) {
        GeneratedRecipe result = null;
        for (int i = 0; i + 1 < variants.size(); i++) {
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> currentEntry = variants.get(i);
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> nextEntry = variants.get(i + 1);
            Supplier<TagKey<Item>> currentIngredient = ingredients.get(i);
            Supplier<TagKey<Item>> nextIngredient = ingredients.get(i + 1);

            result = create(nextEntry).withSuffix("_from_compacting")
                    .unlockedBy(currentEntry::get)
                    .viaShaped(b -> b.pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .define('#', currentIngredient.get()));

            result = create(currentEntry).returns(9)
                    .withSuffix("_from_decompacting")
                    .unlockedBy(nextEntry::get)
                    .viaShapeless(b -> b.requires(nextIngredient.get()));
        }
        return result;
    }

    GeneratedRecipe conversionCycle(List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>> cycle) {
        GeneratedRecipe result = null;
        for (int i = 0; i < cycle.size(); i++) {
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> currentEntry = cycle.get(i);
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> nextEntry = cycle.get((i + 1) % cycle.size());
            result = create(nextEntry).withSuffix("_from_conversion")
                    .unlockedBy(currentEntry::get)
                    .viaShapeless(b -> b.requires(currentEntry.get()));
        }
        return result;
    }

    GeneratedRecipe clearData(ItemProviderEntry<? extends ItemLike, ? extends ItemLike> item) {
        return create(item).withSuffix("_clear")
                .unlockedBy(item::get)
                .viaShapeless(b -> b.requires(item.get()));
    }

    class GeneratedRecipeBuilder {

        private String path;
        private String suffix;
        private Supplier<? extends ItemLike> result;
        private ResourceLocation compatDatagenOutput;
        List<ICondition> recipeConditions;

        private Supplier<ItemPredicate> unlockedBy;
        private int amount;

        private GeneratedRecipeBuilder(String path) {
            this.path = path;
            this.recipeConditions = new ArrayList<>();
            this.suffix = "";
            this.amount = 1;
        }

        public GeneratedRecipeBuilder(String path, Supplier<? extends ItemLike> result) {
            this(path);
            this.result = result;
        }

        public GeneratedRecipeBuilder(String path, ResourceLocation result) {
            this(path);
            this.compatDatagenOutput = result;
        }

        GeneratedRecipeBuilder returns(int amount) {
            this.amount = amount;
            return this;
        }

        GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(item.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder unlockedByTag(Supplier<TagKey<Item>> tag) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(tag.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder whenModLoaded(String modid) {
            return withCondition(new ModLoadedCondition(modid));
        }

        GeneratedRecipeBuilder whenModMissing(String modid) {
            return withCondition(new NotCondition(new ModLoadedCondition(modid)));
        }

        GeneratedRecipeBuilder withCondition(ICondition condition) {
            recipeConditions.add(condition);
            return this;
        }

        GeneratedRecipeBuilder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return register(consumer -> {
                ShapedRecipeBuilder b =
                        builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return register(recipeOutput -> {
                ShapelessRecipeBuilder b =
                        builder.apply(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));

                RecipeOutput conditionalOutput = recipeOutput.withConditions(recipeConditions.toArray(new ICondition[0]));

                b.save(recipeOutput, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaNetheriteSmithing(Supplier<? extends Item> base, Supplier<Ingredient> upgradeMaterial) {
            return register(consumer -> {
                SmithingTransformRecipeBuilder b =
                        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.of(base.get()), upgradeMaterial.get(), RecipeCategory.COMBAT, result.get()
                                        .asItem());
                b.unlocks("has_item", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(base.get())
                        .build()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        private ResourceLocation createSimpleLocation(String recipeType) {
            return resource(recipeType + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation createLocation(String recipeType) {
            return resource(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation getRegistryName() {
            return compatDatagenOutput == null ? RegisteredObjectsHelper.getKeyOrThrow(result.get()
                    .asItem()) : compatDatagenOutput;
        }

        GeneratedCookingRecipeBuilder viaCooking(Supplier<? extends ItemLike> item) {
            return unlockedBy(item).viaCookingIngredient(() -> Ingredient.of(item.get()));
        }

        GeneratedCookingRecipeBuilder viaCookingTag(Supplier<TagKey<Item>> tag) {
            return unlockedByTag(tag).viaCookingIngredient(() -> Ingredient.of(tag.get()));
        }

        GeneratedCookingRecipeBuilder viaCookingIngredient(Supplier<Ingredient> ingredient) {
            return new GeneratedCookingRecipeBuilder(ingredient);
        }

        class GeneratedCookingRecipeBuilder {

            private Supplier<Ingredient> ingredient;
            private float exp;
            private int cookingTime;

            GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
                this.ingredient = ingredient;
                cookingTime = 200;
                exp = 0;
            }

            GeneratedCookingRecipeBuilder forDuration(int duration) {
                cookingTime = duration;
                return this;
            }

            GeneratedCookingRecipeBuilder rewardXP(float xp) {
                exp = xp;
                return this;
            }

            GeneratedRecipe inFurnace() {
                return inFurnace(b -> b);
            }

            GeneratedRecipe inFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                return create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
            }

            GeneratedRecipe inSmoker() {
                return inSmoker(b -> b);
            }

            GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
                create(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, builder, CampfireCookingRecipe::new, 3);
                return create(RecipeSerializer.SMOKING_RECIPE, builder, SmokingRecipe::new, .5f);
            }

            GeneratedRecipe inBlastFurnace() {
                return inBlastFurnace(b -> b);
            }

            GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1);
                return create(RecipeSerializer.BLASTING_RECIPE, builder, BlastingRecipe::new, .5f);
            }

            private <T extends AbstractCookingRecipe> GeneratedRecipe create(RecipeSerializer<T> serializer,
                                                                             UnaryOperator<SimpleCookingRecipeBuilder> builder, AbstractCookingRecipe.Factory<T> factory, float cookingTimeModifier) {
                return register(recipeOutput -> {
                    boolean isOtherMod = compatDatagenOutput != null;

                    SimpleCookingRecipeBuilder b = builder.apply(SimpleCookingRecipeBuilder.generic(ingredient.get(),
                            RecipeCategory.MISC, isOtherMod ? Items.DIRT : result.get(), exp,
                            (int) (cookingTime * cookingTimeModifier), serializer, factory));
                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));

                    RecipeOutput conditionalOutput = recipeOutput.withConditions(recipeConditions.toArray(new ICondition[0]));

                    b.save(
                            isOtherMod ? new ModdedCookingRecipeOutput(conditionalOutput, compatDatagenOutput) : conditionalOutput,
                            createSimpleLocation(RegisteredObjectsHelper.getKeyOrThrow(serializer).getPath())
                    );
                });
            }
        }
    }

    public String getthisName() {
        return "CreatingSpace Standard Recipes";
    }

        public CSStandardRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    class ModdedCookingRecipeOutputShim implements Recipe<RecipeInput> {

        private static final Map<RecipeType<?>, Serializer> serializers = new ConcurrentHashMap<>();

        private final Recipe<?> wrapped;
        private final ResourceLocation overrideID;

        ModdedCookingRecipeOutputShim(Recipe<?> wrapped, ResourceLocation overrideID) {
            this.wrapped = wrapped;
            this.overrideID = overrideID;
        }

        @Override
        public boolean matches(RecipeInput recipeInput, Level level) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public boolean canCraftInDimensions(int pWidth, int pHeight) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public ItemStack getResultItem(HolderLookup.Provider registries) {
            throw new AssertionError("Only for datagen output");
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return serializers.computeIfAbsent(
                    getType(),
                    t -> Serializer.create(wrapped)
            );
        }

        @Override
        public RecipeType<?> getType() {
            return wrapped.getType();
        }

        private record Serializer(
                MapCodec<Recipe<?>> wrappedCodec) implements RecipeSerializer<ModdedCookingRecipeOutputShim> {
            private static Serializer create(Recipe<?> wrapped) {
                RecipeSerializer<?> wrappedSerializer = wrapped.getSerializer();
                @SuppressWarnings("unchecked")
                Serializer serializer = new Serializer((MapCodec<Recipe<?>>) wrappedSerializer.codec());

                // Need to do some registry injection to get the Recipe/Registry#byNameCodec to encode the right type for this
                // getResourceKey and getId
                // byValue and toId
                // Holder.Reference: key
                if (BuiltInRegistries.RECIPE_SERIALIZER instanceof MappedRegistryAccessor<?> mra) {
                    @SuppressWarnings("unchecked")
                    MappedRegistryAccessor<RecipeSerializer<?>> mra$ = (MappedRegistryAccessor<RecipeSerializer<?>>) mra;

                    int wrappedId = mra$.getToId().getOrDefault(wrappedSerializer, -1);
                    ResourceKey<RecipeSerializer<?>> wrappedKey = mra$.getByValue().get(wrappedSerializer).key();

                    mra$.getToId().put(serializer, wrappedId);
                    //noinspection DataFlowIssue - it is ok to pass null as the owner, because this is only being used for serialization
                    mra$.getByValue().put(serializer, Holder.Reference.createStandAlone(null, wrappedKey));
                } else {
                    throw new AssertionError("ModdedCookingRecipeOutputShim will not be able to" +
                            " serialize without injecting into a registry. Expected" +
                            " BuiltInRegistries.RECIPE_SERIALIZER to be of class MappedRegistry, is of class " +
                            BuiltInRegistries.RECIPE_SERIALIZER.getClass()
                    );
                }
                return serializer;
            }

            @Override
            public MapCodec<ModdedCookingRecipeOutputShim> codec() {
                return RecordCodecBuilder.mapCodec(instance -> instance.group(
                        wrappedCodec.forGetter(i -> i.wrapped),
                        FakeItemStack.CODEC.fieldOf("result").forGetter(i -> new FakeItemStack(i.overrideID))
                ).apply(instance, (wrappedRecipe, fakeItemStack) -> {
                    throw new AssertionError("Only for datagen output");
                }));
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, ModdedCookingRecipeOutputShim> streamCodec() {
                throw new AssertionError("Only for datagen output");
            }
        }

        private record FakeItemStack(ResourceLocation id) {
            public static Codec<FakeItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(FakeItemStack::id)
            ).apply(instance, FakeItemStack::new));
        }
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    record ModdedCookingRecipeOutput(RecipeOutput wrapped, ResourceLocation outputOverride) implements RecipeOutput {

        @Override
        public Advancement.Builder advancement() {
            return wrapped.advancement();
        }

        @Override
        public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
            wrapped.accept(id, new ModdedCookingRecipeOutputShim(recipe, outputOverride), advancement, conditions);
        }
    }