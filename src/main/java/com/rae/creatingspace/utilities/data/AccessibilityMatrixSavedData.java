package com.rae.creatingspace.utilities.data;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;


//not used keeping it as a template
public class AccessibilityMatrixSavedData extends SavedData {

    //see if access to datapack can be done even when not reloading
    private Map<String, Map<String, AccessibilityMatrixReader.AccessibilityParameter>> accessibilityMatrix =  new HashMap<>();

    public Map<String, Map<String, AccessibilityMatrixReader.AccessibilityParameter>> getAccessibilityMatrix() {
        return accessibilityMatrix;
    }

    public void setAccessibilityMatrix(Map<String, Map<String, AccessibilityMatrixReader.AccessibilityParameter>> accessibilityMatrix) {
        this.accessibilityMatrix = accessibilityMatrix;
    }

    public static AccessibilityMatrixSavedData create() {
        AccessibilityMatrixSavedData savedData = new AccessibilityMatrixSavedData();
        return savedData;

    }

    public static AccessibilityMatrixSavedData load(CompoundTag tag) {
        AccessibilityMatrixSavedData data = create();
        CompoundTag accessibilityTag = tag.getCompound("accessibilityMatrix");
        DataResult<Map<String, Map<String, AccessibilityMatrixReader.AccessibilityParameter>>>
                result = AccessibilityMatrixReader.ACCESSIBILITY_MATRIX_CODEC.parse(
                        NbtOps.INSTANCE,accessibilityTag);
        data.setAccessibilityMatrix(result.get().left().orElse(new HashMap<>()));
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        System.out.println("saving data");
        DataResult<Tag> result =
                AccessibilityMatrixReader.ACCESSIBILITY_MATRIX_CODEC
                        .encodeStart(NbtOps.INSTANCE,accessibilityMatrix);

        tag.put("accessibilityMatrix",result.result().orElse(new CompoundTag()));
        return tag;
    }

    public static AccessibilityMatrixSavedData load(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(AccessibilityMatrixSavedData::load,
                        AccessibilityMatrixSavedData::create, "creatingspace_utilities");
    }

}