package com.rae.creatingspace.compat.jei;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.compat.jei.category.AirLiquefyingCategory;
import com.rae.creatingspace.compat.jei.category.ChemicalSynthesisCategory;
import com.rae.creatingspace.compat.jei.category.MechanicalElectrolysisCategory;
import com.rae.creatingspace.content.recipes.air_liquefying.AirLiquefyingRecipe;
import com.rae.creatingspace.init.RecipeInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.*;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

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
                        .build(CreatingSpace.resource("chemical"), ChemicalSynthesisCategory::standard);

        CreateRecipeCategory<?> electrolysis =
                builder(BasinRecipe.class)
                        .addTypedRecipes(RecipeInit.MECHANICAL_ELECTROLYSIS)
                        .catalyst(BlockInit.MECHANICAL_ELECTROLYZER::get)
                        .catalyst(AllBlocks.BASIN::get)
                        .doubleItemIcon(BlockInit.MECHANICAL_ELECTROLYZER.get(), AllBlocks.BASIN.get())
                        .emptyBackground(177, 103)
                        .build(CreatingSpace.resource("electrolysis"), MechanicalElectrolysisCategory::standard);

        CreateRecipeCategory<?> airLiquefying =
                builder(AirLiquefyingRecipe.class)
                        .addTypedRecipes(RecipeInit.AIR_LIQUEFYING)
                        .catalyst(BlockInit.AIR_LIQUEFIER::get)
                        .itemIcon(BlockInit.AIR_LIQUEFIER.get())
                        .emptyBackground(177, 103)
                        .build(CreatingSpace.resource("air_liquefying"), AirLiquefyingCategory::new);

    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private class CategoryBuilder<T extends Recipe<?>>  extends CreateRecipeCategory.Builder<T> {

        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public @NotNull CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
            CreateRecipeCategory<T> category = super.build(id, factory);
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
        //CryoSubtypeInterpreter interpreter = new CryoSubtypeInterpreter();
        //registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BlockInit.CRYOGENIC_TANK.get().asItem(), interpreter);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> c.registerCatalysts(registration));
    }
}
