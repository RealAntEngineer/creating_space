package com.rae.creatingspace.init;

import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.*;

public class EngineMaterialInit {//will only be used for datagen

    public static ArrayList<ItemEntry<? extends Item>> collectMaterials() {
        ArrayList<ItemEntry<? extends Item>> collector = new ArrayList<>();
        for (EngineMaterial material : EngineMaterial.values()) {
            collector.addAll(ItemInit.registerEngineIngredientForMaterial(material.materialName()));
        }
        return collector;
    }

    public static ArrayList<ItemEntry<? extends Item>> collectMetals() {
        ArrayList<ItemEntry<? extends Item>> collector = new ArrayList<>();
        collector.addAll(ItemInit.registerMetalVariants(EngineMaterial.REINFORCED_COPPER.materialName()));
        collector.addAll(ItemInit.registerMetalVariants(EngineMaterial.COPRONICKEL.materialName()));
        collector.addAll(ItemInit.registerMetalVariants(EngineMaterial.MONEL.materialName()));
        collector.addAll(ItemInit.registerMetalVariants(EngineMaterial.INCONEL.materialName()));
        collector.addAll(ItemInit.registerMetalVariants(EngineMaterial.HASTELLOY.materialName()));
        return collector;
    }


    public enum EngineMaterial {//used by the table
        ANDESITE(3f, 900f),
        IRON(50f, 1200f),
        COPPER(80f, 2200f),
        BRASS(150f, 2500f),
        REINFORCED_COPPER(250f, 2800f),
        COPRONICKEL(400f, 3200f),
        MONEL(650f, 3800f),
        INCONEL(900f, 4500f),
        HASTELLOY(1200f, 5500f);

        static {
            // Dev-time safety net: every tier must strictly dominate the one below it.
            EngineMaterial[] values = values();
            for (int i = 1; i < values.length; i++) {
                EngineMaterial lower  = values[i - 1];
                EngineMaterial higher = values[i];
                if (higher.maxPressure <= lower.maxPressure
                        || higher.maxTemperature <= lower.maxTemperature) {
                    throw new IllegalStateException(String.format(
                            "Yield curve for %s does not strictly dominate %s",
                            higher, lower));
                }
            }
        }

        private final String             materialName;
        private final String             translationKey;
        private final float              maxPressure;
        private final float              maxTemperature;
        private final List<TagKey<Item>> tags;

        EngineMaterial(float maxPressure, float maxTemperature) {
            this.materialName = name().toLowerCase(Locale.ROOT);
            this.translationKey = "creatingspace.material." + materialName;
            this.maxPressure = maxPressure;
            this.maxTemperature = maxTemperature;
            this.tags = List.of(
                    ItemTags.create(new ResourceLocation("forge", "ingots/" + materialName)),
                    ItemTags.create(new ResourceLocation("forge", "nuggets/" + materialName)),
                    ItemTags.create(new ResourceLocation("forge", "plates/" + materialName)),
                    ItemTags.create(new ResourceLocation("forge", "rod/" + materialName)),
                    ItemTags.create(new ResourceLocation("creatingspace", "blisk/" + materialName))
            );
        }


        /**
         * Lowest-tier material that can handle the given conditions, if any.
         */
        public static Optional<EngineMaterial> lowestFor(float temperature, float pressure) {
            return Arrays.stream(values())
                    .filter(m -> m.allowsConditions(temperature, pressure))
                    .findFirst(); // values() is already in declaration/level order
        }

        public boolean allowsConditions(float temperature, float pressure) {
            if (temperature < 0 || pressure < 0) return false;
            return pressure <= pressureLimitAt(temperature);
        }

        /**
         * Max pressure (bar) this material can hold at a given temperature (Celsius).
         */
        public float pressureLimitAt(float temperatureCelsius) {
            if (temperatureCelsius <= 0) return maxPressure;
            if (temperatureCelsius >= maxTemperature) return 0f;
            return Math.max(0f, maxPressure * (1f - temperatureCelsius / maxTemperature));
        }

        public Component displayComponent() {
            return Component.translatable(translationKey);
        }

        public String translationKey(){
            return translationKey;
        }

        /**
         * Ordinal doubles as "level" — declaration order IS tier order.
         */
        public int level() {
            return ordinal();
        }

        public String materialName() {
            return materialName;
        }

        public List<TagKey<Item>> tags() {
            return tags;
        }
    }

}