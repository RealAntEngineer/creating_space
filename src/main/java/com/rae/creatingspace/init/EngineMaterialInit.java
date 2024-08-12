package com.rae.creatingspace.init;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;

public class EngineMaterialInit {
    public static HashMap<Integer, TagKey<Item>> materials = new HashMap<>();

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

    public void registerMaterial(int level, TagKey<Item> tagKey) {
        materials.put(level, tagKey);
    }
}
