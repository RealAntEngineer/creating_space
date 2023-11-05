package com.rae.creatingspace.configs;

import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public abstract class CSConfigBase extends ConfigBase {

    public void registerAll(ForgeConfigSpec.Builder builder) {
        super.registerAll(builder);
    }

    public class ConfigList<T> extends CValue<List<T>, ForgeConfigSpec.ConfigValue<List<T>>>{

        public ConfigList(String name,List<T> defaultValue, String... comment) {
            super(name, builder -> builder.define(name,defaultValue), comment);
        }
    }

    protected <T extends List> ConfigList l(T defaultValue, String name, String... comment) {
        return new ConfigList<>(name, defaultValue, comment);
    }
}
