package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class PaintingInit {
    public  static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(
            Registries.PAINTING_VARIANT,
            CreatingSpace.MODID);

    public static final DeferredHolder<PaintingVariant,PaintingVariant> BLANK_PAINTING = PAINTINGS.register(
            "blank_painting",
            () -> new PaintingVariant(16,16,
                    CreatingSpace.resource("blank_painting"))
            );

    public static void register(IEventBus bus) {
        PAINTINGS.register(bus);
    }
}
