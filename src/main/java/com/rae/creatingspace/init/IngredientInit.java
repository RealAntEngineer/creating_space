package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.recipes.IntRangeNbtIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import org.jetbrains.annotations.ApiStatus;

//TODO register in CreatingSpace constructor
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class IngredientInit {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, CreatingSpace.MODID);

    public static final DeferredHolder<IngredientType<?>, IngredientType<IntRangeNbtIngredient>> INT_RANGE =
            INGREDIENT_TYPES.register("range_int", () -> new IngredientType<>(IntRangeNbtIngredient.CODEC));
    // Unused currently

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        INGREDIENT_TYPES.register(modEventBus);
    }
}
