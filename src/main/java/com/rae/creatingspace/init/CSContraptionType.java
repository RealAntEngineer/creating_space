package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.contraption.RocketContraption;
import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.function.Supplier;


public class CSContraptionType  {
    public static final Holder.Reference<ContraptionType> ROCKET = register(CreatingSpace.resource("rocket").toString(), RocketContraption::new);

    private static Holder.Reference<ContraptionType> register(String name, Supplier<? extends Contraption> factory) {
        ContraptionType type = new ContraptionType(factory);
        return Registry.registerForHolder(CreateBuiltInRegistries.CONTRAPTION_TYPE, Create.asResource(name), type);
    }
    public CSContraptionType() {
    }

    public static void prepare() {
    }
}
