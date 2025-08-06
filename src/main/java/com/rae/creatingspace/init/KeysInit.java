package com.rae.creatingspace.init;

import java.util.function.BiConsumer;

import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.Create;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public enum KeysInit {

    PITCH_DOWN("pitch_down", GLFW.GLFW_KEY_I, "pitch down"),
    PITCH_UP("pitch_up", GLFW.GLFW_KEY_K, "pitch up"),
    YAW_LEFT("yaw_left", GLFW.GLFW_KEY_J, "rotate left"),
    YAW_RIGHT("yaw_right", GLFW.GLFW_KEY_L, "rotate right"),
    SWITCH_MODE("switch_mode", GLFW.GLFW_KEY_M, "switch flight mode"),
    ROCKET_INVENTORY("rocket_inventory", GLFW.GLFW_KEY_R, "rocket inventory"),//rocket manager -> will do everything at once
    ROCKET_SCHEDULE("rocket_schedule", GLFW.GLFW_KEY_F, "rocket schedule")

    ;

    private KeyMapping keybind;
    private final String description;
    private final String translation;
    private final int key;
    private final boolean modifiable;

    KeysInit(int defaultKey) {
        this("", defaultKey, "");
    }

    KeysInit(String description, int defaultKey, String translation) {
        this.description = CreatingSpace.MODID + ".keyinfo." + description;
        this.key = defaultKey;
        this.modifiable = !description.isEmpty();
        this.translation = translation;
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (KeysInit key : values())
            if (key.modifiable)
                consumer.accept(key.description, key.translation);
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        for (KeysInit key : values()) {
            key.keybind = new KeyMapping(key.description, key.key, CreatingSpace.NAME);
            if (!key.modifiable)
                continue;

            event.register(key.keybind);
        }
    }

    public KeyMapping getKeybind() {
        return keybind;
    }

    public boolean isPressed() {
        if (!modifiable)
            return isKeyDown(key);
        return keybind.isDown();
    }

    public String getBoundKey() {
        return keybind.getTranslatedKeyMessage()
                .getString()
                .toUpperCase();
    }

    public int getBoundCode() {
        return keybind.getKey()
                .getValue();
    }

    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance()
                .getWindow()
                .getWindow(), key);
    }

    public static boolean isMouseButtonDown(int button) {
        return GLFW.glfwGetMouseButton(Minecraft.getInstance()
                .getWindow()
                .getWindow(), button) == 1;
    }

    public static boolean ctrlDown() {
        return Screen.hasControlDown();
    }

    public static boolean shiftDown() {
        return Screen.hasShiftDown();
    }

    public static boolean altDown() {
        return Screen.hasAltDown();
    }

}

