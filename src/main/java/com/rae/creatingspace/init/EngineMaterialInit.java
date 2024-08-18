package com.rae.creatingspace.init;

import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class EngineMaterialInit {//will only be used for datagen
    public static HashMap<Integer, ArrayList<TagKey<Item>>> materials = new HashMap<>();

    //maybe a tag for level like for tools
    public static int getLevelFor(float temperature, float pressure) {//temperature in Celsius and pressure in bar
        int pressureBonus = 0;
        if (pressure > 3 && pressure <= 50) {
            pressureBonus = 1;
        } else if (pressure > 50 && pressure <= 200) {
            pressureBonus = 2;
        } else if (pressure > 200 && pressure <= 1000) {
            pressureBonus = 3;
        } else {
            pressureBonus = 4;
        }
        int temperatureBonus = 0;
        if (temperature > 300 && temperature <= 1000) {
            temperatureBonus = 1;
        } else if (temperature > 1000 && temperature <= 2000) {
            temperatureBonus = 2;
        } else if (temperature > 2000 && temperature <= 5000) {
            temperatureBonus = 3;
        } else {
            temperatureBonus = 4;
        }
        return pressureBonus + temperatureBonus;
    }


    public static ArrayList<ItemEntry<? extends Item>> registerMaterial(int level, String materialName) {
        ArrayList<TagKey<Item>> collector = new ArrayList<>();
        collector.add(ItemTags.create(new ResourceLocation("forge", "ingots/" + materialName)));
        collector.add(ItemTags.create(new ResourceLocation("forge", "nuggets/" + materialName)));
        collector.add(ItemTags.create(new ResourceLocation("forge", "plates/" + materialName)));
        collector.add(ItemTags.create(new ResourceLocation("forge", "rod/" + materialName)));
        collector.add(ItemTags.create(new ResourceLocation("creatingspace", "blisk/" + materialName)));

        materials.put(level, collector);
        return ItemInit.registerEngineIngredientForMaterial(materialName);
    }

    public static ArrayList<ItemEntry<? extends Item>> collectMaterials() {
        ArrayList<ItemEntry<? extends Item>> collector = new ArrayList<>();
        collector.addAll(registerMaterial(0, "andesite"));
        collector.addAll(registerMaterial(1, "iron"));
        collector.addAll(registerMaterial(2, "copper"));
        collector.addAll(registerMaterial(3, "brass"));
        collector.addAll(registerMaterial(4, "reinforced_copper"));
        collector.addAll(registerMaterial(5, "copronickel"));
        collector.addAll(registerMaterial(6, "monel"));
        collector.addAll(registerMaterial(7, "inconel"));
        collector.addAll(registerMaterial(8, "hastelloy"));

        return collector;
    }
}
