package com.rae.creatingspace.init;

import com.rae.creatingspace.init.ingameobject.ItemInit;
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
        } else if (temperature > 2000 && temperature <= 4000) {
            temperatureBonus = 3;
        } else {
            temperatureBonus = 4;
        }
        return pressureBonus + temperatureBonus;
    }

    static {
        registerMaterial(0, "andesite");
        registerMaterial(1, "iron");
        registerMaterial(2, "copper");
        registerMaterial(3, "brass");
        registerMaterial(4, "reinforced_copper");
        registerMaterial(5, "copronickel");
        registerMaterial(6, "monel");
        registerMaterial(7, "inconel");
        registerMaterial(8, "hastelloy");
    }

    public static void registerMaterial(int level, String materialName) {
        ItemInit.registerEngineIngredientForMaterial(materialName);
        ArrayList<TagKey<Item>> collector = new ArrayList<>();
        collector.add(ItemTags.create(new ResourceLocation("forge", "ingots/" + materialName)));
        collector.add(ItemTags.create(new ResourceLocation("forge", "nuggets/" + materialName)));
        collector.add(ItemTags.create(new ResourceLocation("forge", "plates/" + materialName)));
        collector.add(ItemTags.create(new ResourceLocation("forge", "rod/" + materialName)));
        collector.add(ItemTags.create(new ResourceLocation("creatingspace", "blisk/" + materialName)));

        materials.put(level, collector);
    }

    public static void register() {

    }
}
