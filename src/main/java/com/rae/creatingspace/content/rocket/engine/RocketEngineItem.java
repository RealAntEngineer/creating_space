package com.rae.creatingspace.content.rocket.engine;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.configs.CSCfgClient;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.legacy.utilities.CSUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class RocketEngineItem extends BlockItem {
    public RocketEngineItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }

    public static void appendEngineDependentText(List<Component> components, int ISP, int thrust) {
        components.add(Component.translatable("creatingspace.science.isp")
                .append(Component.literal(" : " + ISP))
                .append(Component.translatable("creatingspace.science.unit.second")));
        components.add(Component.translatable("creatingspace.science.thrust")
                .append(Component.literal(" : " + CSUtil.scientificNbrFormatting((float) thrust, 10)))
                .append(Component.translatable("creatingspace.science.unit.newton")));

    }

    public static void appendEngineDependentText(List<Component> components, int ISP, int mass, int thrust) {
        components.add(Component.translatable("creatingspace.science.isp")
                .append(Component.literal(" : " + ISP))
                .append(Component.translatable("creatingspace.science.unit.second")));
        components.add(Component.translatable("creatingspace.science.mass").append(" : " + mass).append(Component.translatable("creatingspace.science.unit.kilo_gramme")));
        components.add(Component.translatable("creatingspace.science.thrust")
                .append(Component.literal(" : " + CSUtil.scientificNbrFormatting((float) thrust, 10)))
                .append(Component.translatable("creatingspace.science.unit.newton")));

    }

    public static void appendEngineDependentText(List<Component> components, PropellantType propellantType, int ISP, int thrust) {
        appendEngineDependentText(components, ISP, thrust);
        appendFluidInfo(components, propellantType);
    }

    public static void appendEngineDependentText(List<Component> components, PropellantType propellantType, int ISP, int mass, int thrust) {
        appendEngineDependentText(components, ISP, mass, thrust);
        appendFluidInfo(components, propellantType);
    }

    private static void appendFluidInfo(List<Component> components, PropellantType propellantType) {
        components.add(Component.literal("ratio of fluid consumed :"));
        if (CSConfigs.CLIENT.recorder_measurement.get().equals(CSCfgClient.Measurement.MASS)) {
            for (TagKey<Fluid> fluidTagkey : propellantType.getPropellantRatio().keySet()) {
                components.add(Component.translatable("fluid." + fluidTagkey.location().toLanguageKey()).withStyle(ChatFormatting.AQUA)
                        .append(" : ")
                        .append(Component.literal(String.valueOf(((int) (propellantType.getPropellantRatio().get(fluidTagkey) * 1000) / 10f))))
                        .append("%")
                );
            }
        }
        if (CSConfigs.CLIENT.recorder_measurement.get().equals(CSCfgClient.Measurement.VOLUMETRIC)) {
            HashMap<TagKey<Fluid>, Float> collector = new HashMap<>();
            float total = 0;
            for (TagKey<Fluid> fluidTagkey : propellantType.getPropellantRatio().keySet()) {
                AtomicReference<Fluid> fluidRef = new AtomicReference<>();

                ForgeRegistries.FLUIDS.getEntries().forEach(
                        resourceKeyFluidEntry -> {
                            if (resourceKeyFluidEntry.getValue().is(fluidTagkey)) {
                                fluidRef.set(resourceKeyFluidEntry.getValue());
                            }
                        }
                );
                if (fluidRef.get() == null) {
                    CreatingSpace.LOGGER.warn("Warning : failed to find a fluid in game data");
                } else {
                    collector.put(fluidTagkey, propellantType.getPropellantRatio().get(fluidTagkey) / fluidRef.get().getFluidType().getDensity()); //in minecraft's bucket
                    total += propellantType.getPropellantRatio().get(fluidTagkey) / fluidRef.get().getFluidType().getDensity();
                }
            }

            float finalTotal = total;
            collector.forEach((k, v) -> {
                        components.add(Component.translatable("fluid." + k.location().toLanguageKey()).withStyle(ChatFormatting.AQUA)
                                .append(" : ")
                                .append(Component.literal(String.valueOf(((int) (collector.get(k) / finalTotal * 1000) / 10f))))
                                .append("%")
                        );
                    }
            );
        }
    }
}
