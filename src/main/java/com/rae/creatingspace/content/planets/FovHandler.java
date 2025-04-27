package com.rae.creatingspace.content.planets;

import net.minecraftforge.client.event.ComputeFovModifierEvent;

import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class FovHandler {
    private static float fov = 70.0F; // Default FOV value

    public FovHandler() {
        // Register this class to listen to events
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onFovModify(ViewportEvent.ComputeFov event) {
        // Capture the FOV from the event
        fov = (float) event.getFOV();
    }

    public static float getCurrentFov() {
        return fov;
    }
}
