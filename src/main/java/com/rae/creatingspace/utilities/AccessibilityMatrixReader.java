package com.rae.creatingspace.utilities;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccessibilityMatrixReader {

    public static HashMap<ResourceKey<Level>, ResourceKey<Level>[]> createFromStringList(List<String> ressourceList){

        HashMap<ResourceKey<Level>,ResourceKey<Level>[]> dimAccess = new HashMap<>();
        for (String string : ressourceList) {
            String[] firstArray = string.split("-");
            String key = firstArray[0];
            dimAccess.put(
                    ResourceKey.create(Registries.DIMENSION,
                    new ResourceLocation(key)),

                    formStringToLocation(firstArray[1]));
        }

        return dimAccess;
    }

    public static ResourceKey<Level>[] formStringToLocation(String string){
        ArrayList<ResourceKey<Level>> resourceKeys = new ArrayList<>();
        String[] strings = string.split(",");
        for (String value :
                strings) {
                    resourceKeys.add(ResourceKey.create(Registries.DIMENSION,
                    new ResourceLocation(value
                            .replace("{","")
                            .replace("}",""))));
        }

        return resourceKeys.toArray(new ResourceKey[0]);
    }

    public record AccessibilityParameter(ResourceLocation dimensionLocation){

    }
}
