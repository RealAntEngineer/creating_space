package com.rae.creatingspace.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.compat.jei.AnimatedCatalystCarrier;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GasExtractionCategory extends BasinCategory {
    private final AnimatedCatalystCarrier carrier = new AnimatedCatalystCarrier();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public static GasExtractionCategory standard(Info<BasinRecipe> info) {
        return new GasExtractionCategory(info);
    }

    protected GasExtractionCategory(Info<BasinRecipe> info) {
        super(info, true);
    }

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, matrixStack, mouseX, mouseY);

        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE)
            heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
                    .draw(matrixStack, getBackground().getWidth() / 2 + 3, 55);
        carrier.draw(matrixStack, getBackground().getWidth() / 2 + 3, 34);
    }
}
