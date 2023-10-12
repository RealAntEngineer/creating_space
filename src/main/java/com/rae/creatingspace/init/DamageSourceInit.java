package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.damageTypes.DamageTypeData;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

import static com.rae.creatingspace.CreatingSpace.resource;

public class DamageSourceInit {

    public static final DamageTypeData NO_OXYGEN = DamageTypeData.builder()
            .simpleId(resource("no_oxygen"))
            .tag(DamageTypeTags.BYPASSES_ARMOR)
            .build();


    public static void bootstrap(BootstapContext<DamageType> ctx) {
        DamageTypeData.allInNamespace(CreatingSpace.MODID).forEach(data -> data.register(ctx));
    }
}
