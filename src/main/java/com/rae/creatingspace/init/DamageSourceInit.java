package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

public class DamageSourceInit {
    public static final ResourceKey<DamageType>
            NO_OXYGEN = key("no_oxygen");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, CreatingSpace.resource(name));
    }

    public static void bootstrap(BootstapContext<DamageType> ctx) {
        new DamageTypeBuilder(NO_OXYGEN)
                .scaling(DamageScaling.NEVER)
                .effects(DamageEffects.DROWNING)
                .exhaustion(0)
                .deathMessageType(DeathMessageType.DEFAULT)
                .msgId("creatingspace.no_oxygen")
                .register(ctx);
    }
}
