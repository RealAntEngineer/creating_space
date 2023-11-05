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

    public static HashMap<ResourceKey<Level>, HashMap<ResourceKey<Level>, AccessibilityParameter>> createFromStringList(List<String> ressourceList){

        HashMap<ResourceKey<Level>, HashMap<ResourceKey<Level>, AccessibilityParameter>> dimAccess = new HashMap<>();
        for (String string : ressourceList) {
            String[] firstArray = string.split("->",2);
            String key = firstArray[0];
            ResourceKey<Level> convertedOriginKey = ResourceKey.create(Registries.DIMENSION,
                    new ResourceLocation(key));
            dimAccess.put(
                    convertedOriginKey,
                    formStringToLocation(firstArray[1],convertedOriginKey));
        }

        return dimAccess;
    }

    public static HashMap<ResourceKey<Level>, AccessibilityParameter> formStringToLocation(String value, ResourceKey<Level> convertedOriginKey){
        HashMap<ResourceKey<Level>, AccessibilityParameter> integerHashMap = new HashMap<>();
        String[] strings = value.split(",");
        for (String ressourceKey : strings) {
            String[] values = ressourceKey.split("->",2);

            ResourceKey<Level> convertedDestinationKey = ResourceKey.create(Registries.DIMENSION,
                    new ResourceLocation(values[0]
                            .replace("{","")
                            .replace("}","")));
            integerHashMap.put(convertedDestinationKey,
                    AccessibilityParameter.fromString(
                            convertedOriginKey,
                            convertedDestinationKey,
                            values[1].replace("{","")
                                    .replace("}","")));
        }

        return integerHashMap;
    }

    public record AccessibilityParameter(ResourceKey<Level> origin,ResourceKey<Level> destination, int deltaV,int arrivalHeight){

        public static AccessibilityParameter fromString(ResourceKey<Level> origin,ResourceKey<Level> destination,String string){

            int deltaV = 500;
            int arrivalHeight = 200;

            String[] splited = string.replace("{","").replace("}","").split("\\|");
            if (splited.length == 1){
                if (CSUtil.isInteger(splited[0])){
                    deltaV = Integer.parseInt(splited[0]);
                }
            }
            for (String pair : splited){
                String[] keyAndValue =  pair.split("->",2);
                if (keyAndValue[0]== "deltaV"){
                    if (CSUtil.isInteger(splited[0]))
                        deltaV = Integer.parseInt(keyAndValue[1]);
                }
                if (keyAndValue[0]== "arrivalHeight"){
                    if (CSUtil.isInteger(splited[0]))
                        deltaV = Integer.parseInt(keyAndValue[1]);
                }
            }
            return new AccessibilityParameter(origin,destination,deltaV,arrivalHeight);
        }
    }
}
