package com.rae.creatingspace.legacy.utilities.data;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public class SingleFileCodecJsonDataManager<T> extends AbstractCodecJsonDataManager<T>
{

    private final ResourceLocation location;

    public SingleFileCodecJsonDataManager(String folderName,ResourceLocation location, Codec<T> codec, Logger logger)
    {
        this(folderName,location, codec, logger, STANDARD_GSON);
    }
    public SingleFileCodecJsonDataManager(String folderName,ResourceLocation location, Codec<T> codec, Logger logger, Gson gson)
    {
        super(folderName,codec,logger,gson);
        this.location = location;

    }
    @Nullable
    public T getData()
    {
        return this.data.get(this.location);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public T getData(ResourceLocation id) {
        return getData();
    }
    @Override
    public Map<ResourceLocation, T> mapValues(Map<ResourceLocation, JsonElement> inputs)
    {
        // it's always the same location the map isn't needed
        Map<ResourceLocation, T> newMap = new HashMap<>();

        JsonElement element = inputs.get(this.location);
        if (element!=null) {
            // if we fail to parse json, log an error and continue
            // if we succeeded, add the resulting T to the map
            this.codec.decode(JsonOps.INSTANCE, element)
                    .get()
                    .ifLeft(result -> newMap.put(this.location, result.getFirst()))
                    .ifRight(partial -> this.logger.error("Failed to parse data json for {} due to: {}", this.location.toString(), partial.message()));
        }
        //may shut the error

        //it returns the last jsonElement with the right resource location -> I need to put a ArrayList thing here...
        return newMap;
    }
}
