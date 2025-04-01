package com.rae.colony_api.data.managers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.rae.colony_api.data.Event.getSideAwareRegistry;

public class FloatMapDataLoader<T> extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final String FOLDER = "float_map";
    private final ResourceKey<Registry<T>> registryKey;
    private final ResourceLocation FILE_NAME;
    private final HashMap<ResourceLocation, Float> FLOAT_MAP = new HashMap<>();

    public FloatMapDataLoader(String modId, String fileName, ResourceKey<Registry<T>> registryKey) {
        super(GSON, FOLDER);
        FILE_NAME = new ResourceLocation(modId, fileName);
        this.registryKey = registryKey;
    }
    public static final Logger LOGGER = LogUtils.getLogger();
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        LOGGER.info("Reloading FloatMapDataLoader for: " + FILE_NAME);
        boolean replace = false;
        Map<ResourceLocation, Float> newTemperatures = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            if (!entry.getKey().equals(FILE_NAME)) continue;
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "float data");
                replace = GsonHelper.getAsBoolean(json, "replace", false);
                JsonObject values = GsonHelper.getAsJsonObject(json, "values");

                for (Map.Entry<String, JsonElement> valueEntry : values.entrySet()) {
                    float temperature = valueEntry.getValue().getAsFloat();
                    newTemperatures.put(new ResourceLocation(valueEntry.getKey()), temperature);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load float data from {}", entry.getKey(), e);
            }
        }

        if (replace) {
            FLOAT_MAP.clear();
        }
        FLOAT_MAP.putAll(newTemperatures);
    }

    private float getValue(ResourceLocation id, float defaultValue) {
        return FLOAT_MAP.getOrDefault(id, defaultValue);
    }

    public float getValue(T registryEntry, float defaultValue) {
        Registry<T> registry = getSideAwareRegistry(registryKey);
        if (registry != null) {
            ResourceLocation id = registry.getKey(registryEntry);
            if (id != null) {
                return getValue(id, defaultValue);
            }
        }
        return defaultValue;
    }
}