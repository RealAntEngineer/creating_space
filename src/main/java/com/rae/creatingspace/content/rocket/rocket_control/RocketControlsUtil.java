package com.rae.creatingspace.content.rocket.rocket_control;

import com.mojang.blaze3d.platform.InputConstants;
import com.rae.creatingspace.init.KeysInit;
import com.simibubi.create.AllKeys;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

import java.util.Vector;

public class RocketControlsUtil {

    private static Vector<KeyMapping> standardControls;
    //todo it should be a map not a list, it's beginning to be hard to manage (magic numbers)
    public static Vector<KeyMapping> getControls() {
        if (standardControls == null) {
            Options gameSettings = Minecraft.getInstance().options;
            standardControls = new Vector<>(6);
            standardControls.add(gameSettings.keyUp);//0
            standardControls.add(gameSettings.keyDown);//1
            standardControls.add(gameSettings.keyLeft);//2
            standardControls.add(gameSettings.keyRight);//3
            standardControls.add(gameSettings.keyJump);//4
            standardControls.add(gameSettings.keyShift);//5
            standardControls.add(KeysInit.PITCH_UP.getKeybind());//6
            standardControls.add(KeysInit.PITCH_DOWN.getKeybind());//7
            standardControls.add(KeysInit.YAW_LEFT.getKeybind());//8
            standardControls.add(KeysInit.YAW_RIGHT.getKeybind());//9
            standardControls.add(KeysInit.SWITCH_MODE.getKeybind());//10
            standardControls.add(gameSettings.keySprint);//11
            standardControls.add(KeysInit.ROCKET_INVENTORY.getKeybind());//12
            standardControls.add(KeysInit.ROCKET_SCHEDULE.getKeybind());//13

        }
        return standardControls;
    }

    public static boolean isActuallyPressed(KeyMapping kb) {
        InputConstants.Key key = kb.getKey();
        if (key.getType() == InputConstants.Type.MOUSE) {
            return AllKeys.isMouseButtonDown(key.getValue());
        } else {
            return AllKeys.isKeyDown(key.getValue());
        }
    }
}
