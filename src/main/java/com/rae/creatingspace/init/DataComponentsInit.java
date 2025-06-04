package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.UnaryOperator;

public class DataComponentsInit {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreatingSpace.MODID);

    public static final DataComponentType<SimpleFluidContent> SIMPLE_FLUID_CONTENT = register(
            "simple_fluid_content",
            builder ->
                    builder.persistent(SimpleFluidContent.CODEC)
                            .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
    );

    public static final DataComponentType<Map<ResourceLocation, BlockPos>> INITIAL_POS_MAP = register(
            "initial_pos_map",
                builder -> builder.persistent(
                        RocketControlsBlockEntity.POS_MAP_CODEC
                ).networkSynchronized(
                        ByteBufCodecs.fromCodec(RocketControlsBlockEntity.POS_MAP_CODEC)
                )
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
