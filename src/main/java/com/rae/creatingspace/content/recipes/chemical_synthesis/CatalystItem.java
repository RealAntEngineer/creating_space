package com.rae.creatingspace.content.recipes.chemical_synthesis;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CatalystItem extends Item {
    final PartialModel model;

    //PartialModel exist in DedicatedServer but should be used...
    public CatalystItem(Properties properties, PartialModel model) {
        super(properties);
        this.model = model;
    }

    @Override
    public boolean isDamageable(@NotNull ItemStack stack) {
        return true;
    }

    public @NotNull PartialModel getModel(){
        return model;
    };
}
