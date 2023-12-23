package com.rae.creatingspace.utilities.data;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class CodecJsonDataManager<T> extends AbstractCodecJsonDataManager<T>
{
    public CodecJsonDataManager(String folderName, Codec<T> codec, Logger logger)
    {
        super(folderName, codec, logger);
    }
    public CodecJsonDataManager(String folderName, Codec<T> codec, Logger logger, Gson gson)
    {
        super(folderName,codec,logger,gson);
    }

    @Override
    public Map<ResourceLocation, T> mapValues(Map<ResourceLocation, JsonElement> inputs)
        {
            Map<ResourceLocation, T> newMap = new HashMap<>();

            for (Entry<ResourceLocation, JsonElement> entry : inputs.entrySet())
            {
                ResourceLocation key = entry.getKey();
                JsonElement element = entry.getValue();
                // if we fail to parse json, log an error and continue -> does it mean it's not meant to be read by this instance ?
                // if we succeeded, add the resulting T to the map
                this.codec.decode(JsonOps.INSTANCE, element)
                        .get()
                        .ifLeft(result -> newMap.put(key, result.getFirst()))
                        .ifRight(partial -> this.logger.error("Failed to parse data json for {} due to: {}", key.toString(), partial.message()));
                //may shut the error
            }
            //it returns the last jsonElement with the right resource location -> I need to put a ArrayList thing here...
            return newMap;
        }

}
