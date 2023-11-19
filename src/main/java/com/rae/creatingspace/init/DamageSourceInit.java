package com.rae.creatingspace.init;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSourceInit {
    public static final DamageSource NO_OXYGEN  = (new DamageSource("no_oxygen"))
            .bypassArmor();
}
