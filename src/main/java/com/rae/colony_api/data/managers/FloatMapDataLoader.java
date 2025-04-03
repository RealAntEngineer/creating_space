package com.rae.colony_api.data.managers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
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
    public static final Logger LOGGER = LogUtils.getLogger();
    private final HashMap<TagKey<T>, Float> TAG_FLOAT_MAP = new HashMap<>();
    private boolean tagLoaded = false;
    public FloatMapDataLoader(String modId, String fileName, ResourceKey<Registry<T>> registryKey) {
        super(GSON, FOLDER);
        FILE_NAME = new ResourceLocation(modId, fileName);
        this.registryKey = registryKey;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        LOGGER.info("Reloading FloatMapDataLoader for: {}", FILE_NAME);
        boolean replace = false;
        Map<ResourceLocation, Float> newValues = new HashMap<>();
        Map<TagKey<T>, Float> newTagValues = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            if (!entry.getKey().equals(FILE_NAME)) continue;
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "float data");
                replace = GsonHelper.getAsBoolean(json, "replace", false);
                JsonObject values = GsonHelper.getAsJsonObject(json, "values");

                for (Map.Entry<String, JsonElement> valueEntry : values.entrySet()) {
                    String key = valueEntry.getKey();
                    float value = valueEntry.getValue().getAsFloat();

                    if (key.startsWith("#")) {
                        // Handle tags
                        ResourceLocation tagId = new ResourceLocation(key.substring(1)); // Remove '#'
                        newTagValues.put(TagKey.create(registryKey, tagId), value);
                    } else {
                        // Handle normal entries
                        newValues.put(new ResourceLocation(key), value);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load float data from {}", entry.getKey(), e);
            }
        }

        if (replace) {
            FLOAT_MAP.clear();
        }
        FLOAT_MAP.putAll(newValues);
        TAG_FLOAT_MAP.putAll(newTagValues);
    }

    public float getValue(@NotNull T registryEntry, float defaultValue)
    {
        Registry<T> registry = getSideAwareRegistry(registryKey);
        if (!tagLoaded){
            for (Map.Entry<TagKey<T>, Float> entry : TAG_FLOAT_MAP.entrySet()){
                for (Holder<T>holder : registry.getTagOrEmpty(entry.getKey())){
                    ResourceLocation id = registry.getKey(holder.get());
                    FLOAT_MAP.putIfAbsent(id, entry.getValue());
                }
            }
            tagLoaded = true;
        }
        if (registry != null) {
            ResourceLocation id = registry.getKey(registryEntry);
            if (id != null) {
                Float value = FLOAT_MAP.get(id);
                if (value != null) {
                    return value;
                }
            }
        }
        return defaultValue;
    }
}