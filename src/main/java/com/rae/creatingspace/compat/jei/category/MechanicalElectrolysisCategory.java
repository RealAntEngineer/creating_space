package com.rae.creatingspace.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.compat.jei.AnimatedMechanicalElectrolyzer;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MechanicalElectrolysisCategory extends BasinCategory {
    private final AnimatedMechanicalElectrolyzer electrolyzer = new AnimatedMechanicalElectrolyzer();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public static MechanicalElectrolysisCategory standard(Info<BasinRecipe> info) {
        return new MechanicalElectrolysisCategory(info);
    }

    protected MechanicalElectrolysisCategory(Info<BasinRecipe> info) {
        super(info, true);
    }

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, graphics, mouseX, mouseY);

        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE)
            heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
                    .draw(graphics, getBackground().getWidth() / 2 + 3, 55);
        electrolyzer.draw(graphics, getBackground().getWidth() / 2 + 3, 34);
    }
}
