package com.rae.creatingspace.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rae.creatingspace.compat.jei.AnimatedAirLiquefier;
import com.rae.creatingspace.content.recipes.air_liquefying.AirLiquefyingRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.createmod.catnip.gui.element.GuiGameElement;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.simibubi.create.compat.jei.category.animations.AnimatedKinetics.DEFAULT_LIGHTING;

@ParametersAreNonnullByDefault
public class AirLiquefyingCategory extends CreateRecipeCategory<AirLiquefyingRecipe> {
    private final AnimatedAirLiquefier airLiquefier = new AnimatedAirLiquefier();
    protected static final int SCALE = 24;


    public AirLiquefyingCategory(Info<AirLiquefyingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AirLiquefyingRecipe recipe, IFocusGroup focuses) {

        int i;

        int size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
        i = 0;

        for (FluidStack fluidResult : recipe.getFluidResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 51;
            addFluidSlot(builder, xPosition, yPosition, fluidResult);
            i++;
        }
    }

    @Override
    public void draw(AirLiquefyingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        HeatCondition requiredHeat = recipe.getRequiredHeat();

        boolean noHeat = requiredHeat == HeatCondition.NONE;

        int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;

        if (vRows <= 2)
            AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, -19 * (vRows - 1) + 32);

        AllGuiTextures shadow = noHeat ? AllGuiTextures.JEI_SHADOW : AllGuiTextures.JEI_LIGHT;
        shadow.render(graphics, 70, 58 + (noHeat ? 10 : 30));
        airLiquefier.draw(graphics, getBackground().getWidth() / 2 + 3, 34);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(80, 60, 0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-12.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        GuiGameElement.of(ForgeRegistries.BLOCKS.getValue(recipe.getBlockInFront()).defaultBlockState())
                .lighting(DEFAULT_LIGHTING)
                .scale(SCALE)
                .render(graphics);
        matrixStack.popPose();

    }
}
