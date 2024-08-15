package com.rae.creatingspace.mixin.recipe;

import com.rae.creatingspace.recipes.IMoreNbtConditions;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

@Mixin(value = ProcessingRecipe.class)
public class ProcessingRecipeMixin implements IMoreNbtConditions {
    @Unique
    public ArrayList<String> nbtKeys = new ArrayList<>();
    @Unique
    public boolean cS_1_19_2$matchNbt = false;

    public void setKeepNbt(ArrayList<String> nbtKeys) {
        this.nbtKeys = nbtKeys;
    }

    public void setMachNbt(boolean value) {
        cS_1_19_2$matchNbt = value;
    }

    @Override
    public boolean isKeepNbt() {
        return nbtKeys.isEmpty();
    }

    @Override
    public boolean isMachNbt() {
        return cS_1_19_2$matchNbt;
    }

}
