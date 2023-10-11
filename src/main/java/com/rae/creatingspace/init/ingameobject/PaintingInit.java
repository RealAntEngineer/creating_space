package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PaintingInit {
    public  static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(
            ForgeRegistries.PAINTING_VARIANTS,
            CreatingSpace.MODID);

    public static final RegistryObject<PaintingVariant> BLANK_PAINTING = PAINTINGS.register(
            "blank_painting",
            () -> new PaintingVariant(16,16)
            );

    public static void register(IEventBus bus) {
        PAINTINGS.register(bus);
    }
}
