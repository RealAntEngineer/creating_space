package com.rae.creatingspace.utilities.data;

import com.rae.creatingspace.CreatingSpace;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;


//Not usefull : class to be deleted
//@Mod.EventBusSubscriber(modid = CreatingSpace.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataStorageEvents {

    //keeping it as a template in case I need to have saved Data

    //@SubscribeEvent
    public static void worldSave(LevelEvent.Save event) {
        //does it work without that ? -> test when changing the datapack inWorld
        /*if (!AccessibilityMatrixReader.compressedAccessibilityMatrix.isEmpty()){
            AccessibilityMatrixSavedData accessibilityMatrixSavedData =
                    AccessibilityMatrixSavedData.load(Objects.requireNonNull(event.getLevel().getServer()));
            accessibilityMatrixSavedData.setAccessibilityMatrix(AccessibilityMatrixReader.compressedAccessibilityMatrix);
            accessibilityMatrixSavedData.setDirty();
        }*/
    }
    //@SubscribeEvent
    public static void worldLoad(LevelEvent.Load event) {
        if(!event.getLevel().isClientSide()) {

            /*if (!AccessibilityMatrixReader.compressedAccessibilityMatrix.isEmpty()){
            //is this useful if we save it already ? -> it will never be full at world load right ?
                AccessibilityMatrixSavedData accessibilityMatrixSavedData = AccessibilityMatrixSavedData.load(Objects.requireNonNull(event.getLevel().getServer()));

                accessibilityMatrixSavedData.setAccessibilityMatrix(AccessibilityMatrixReader.compressedAccessibilityMatrix);
                accessibilityMatrixSavedData.setDirty();
            // may be modify the saved data at dataReload ?
            } else {
            AccessibilityMatrixReader.compressedAccessibilityMatrix =
                    AccessibilityMatrixSavedData
                            .load(Objects.requireNonNull(
                                    event.getLevel().getServer())).getAccessibilityMatrix();
            }*/

        }
    }

}
