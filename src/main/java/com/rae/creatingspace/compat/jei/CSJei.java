package com.rae.creatingspace.compat.jei;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.compat.jei.category.AirLiquefyingCategory;
import com.rae.creatingspace.compat.jei.category.ChemicalSynthesisCategory;
import com.rae.creatingspace.compat.jei.category.MechanicalElectrolysisCategory;
import com.rae.creatingspace.init.RecipeInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.recipes.AirLiquefyingRecipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.*;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class CSJei implements IModPlugin {
    private static final ResourceLocation ID = CreatingSpace.resource("jei_plugin");
    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
    private IIngredientManager ingredientManager;

    private void loadCategories() {
        allCategories.clear();

        CreateRecipeCategory<?> chemical =
                builder(BasinRecipe.class)
                        .addTypedRecipes(RecipeInit.CHEMICAL_SYNTHESIS)
                        .catalyst(BlockInit.CATALYST_CARRIER::get)
                        .catalyst(AllBlocks.BASIN::get)
                        .doubleItemIcon(BlockInit.CATALYST_CARRIER.get(), AllBlocks.BASIN.get())
                        .emptyBackground(177, 103)
                        .build("chemical", ChemicalSynthesisCategory::standard);

        CreateRecipeCategory<?> electrolysis =
                builder(BasinRecipe.class)
                        .addTypedRecipes(RecipeInit.MECHANICAL_ELECTROLYSIS)
                        .catalyst(BlockInit.MECHANICAL_ELECTROLYZER::get)
                        .catalyst(AllBlocks.BASIN::get)
                        .doubleItemIcon(BlockInit.MECHANICAL_ELECTROLYZER.get(), AllBlocks.BASIN.get())
                        .emptyBackground(177, 103)
                        .build("electrolysis", MechanicalElectrolysisCategory::standard);

        CreateRecipeCategory<?> airLiquefying =
                builder(AirLiquefyingRecipe.class)
                        .addTypedRecipes(RecipeInit.AIR_LIQUEFYING)
                        .catalyst(BlockInit.AIR_LIQUEFIER::get)
                        .itemIcon(BlockInit.AIR_LIQUEFIER.get())
                        .emptyBackground(177, 103)
                        .build("air_liquefying", AirLiquefyingCategory::new);

    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private class CategoryBuilder<T extends Recipe<?>> {
        private final Class<? extends T> recipeClass;
        private Predicate<CRecipes> predicate = cRecipes -> true;
        private IDrawable background;
        private IDrawable icon;

        private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

        public CategoryBuilder(Class<? extends T> recipeClass) {
            this.recipeClass = recipeClass;
        }

        public CategoryBuilder<T> enableIf(Predicate<CRecipes> predicate) {
            this.predicate = predicate;
            return this;
        }

        public CategoryBuilder<T> enableWhen(Function<CRecipes, ConfigBase.ConfigBool> configValue) {
            predicate = c -> configValue.apply(c).get();
            return this;
        }

        public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
            recipeListConsumers.add(consumer);
            return this;
        }

        public CategoryBuilder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
            return addTypedRecipes(recipeTypeEntry::getType);
        }

        public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
            return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
        }

        public CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
            catalysts.add(supplier);
            return this;
        }

        public CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
            return catalystStack(() -> new ItemStack(supplier.get()
                    .asItem()));
        }

        public CategoryBuilder<T> icon(IDrawable icon) {
            this.icon = icon;
            return this;
        }

        public CategoryBuilder<T> itemIcon(ItemLike item) {
            icon(new ItemIcon(() -> new ItemStack(item)));
            return this;
        }

        public CategoryBuilder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
            icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
            return this;
        }

        public CategoryBuilder<T> background(IDrawable background) {
            this.background = background;
            return this;
        }

        public CategoryBuilder<T> emptyBackground(int width, int height) {
            background(new EmptyBackground(width, height));
            return this;
        }

        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            Supplier<List<T>> recipesSupplier;
            if (predicate.test(AllConfigs.server().recipes)) {
                recipesSupplier = () -> {
                    List<T> recipes = new ArrayList<>();
                    for (Consumer<List<T>> consumer : recipeListConsumers)
                        consumer.accept(recipes);
                    return recipes;
                };
            } else {
                recipesSupplier = () -> Collections.emptyList();
            }

            CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
                    new mezz.jei.api.recipe.RecipeType<>(CreatingSpace.resource(name), recipeClass),
                    Component.translatable(CreatingSpace.MODID + ".recipe." + name), background, icon, recipesSupplier, catalysts);
            CreateRecipeCategory<T> category = factory.create(info);
            allCategories.add(category);
            return category;
        }
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();

        allCategories.forEach(c -> c.registerRecipes(registration));

        registration.addRecipes(RecipeTypes.CRAFTING, ToolboxColoringRecipeMaker.createRecipes().toList());
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        IModPlugin.super.registerIngredients(registration);

    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        CryoSubtypeInterpreter interpreter = new CryoSubtypeInterpreter();
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BlockInit.CRYOGENIC_TANK.get().asItem(),interpreter);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> c.registerCatalysts(registration));
    }
}
