package com.rae.creatingspace.configs;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import org.jetbrains.annotations.NotNull;

public abstract class CSConfigBase extends ConfigBase {
    public void registerAll(@NotNull Builder builder) {
        super.registerAll(builder);
    }
}
