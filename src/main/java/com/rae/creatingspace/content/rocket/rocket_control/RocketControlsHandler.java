package com.rae.creatingspace.content.rocket.rocket_control;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsInputPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.utility.ControlsUtil;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

//can we just extends ControlsHandler ? The issue is that we need our own key mapping (we are adding a bunch of them and
// might add joystick handling) -> no we can't because teh entity ref is private, we would need a getter.
//TODO add accessors to the ControlsHandler so we can expand it and change the tick methode.

/**
 * this is a client only class, that's why the methode are static and not by player entity : we don't need that because
 * only one player does inputs per client.
 */
public class RocketControlsHandler {//this is client only ?

    public static Collection<Integer> currentlyPressed = new HashSet<>();

    public static int PACKET_RATE = 5;
    private static int packetCooldown;

    private static WeakReference<AbstractContraptionEntity> entityRef = new WeakReference<>(null);
    private static BlockPos controlsPos;

    public static void levelUnloaded(LevelAccessor level) {
        packetCooldown = 0;
        entityRef = new WeakReference<>(null);
        controlsPos = null;
        currentlyPressed.clear();
    }

    public static void startControlling(AbstractContraptionEntity entity, BlockPos controllerLocalPos) {
        entityRef = new WeakReference<>(entity);
        controlsPos = controllerLocalPos;
        //todo : what about
        Minecraft.getInstance().player.displayClientMessage(
                CreateLang.translateDirect("contraption.controls.start_controlling", entity.getContraptionName()), true);
    }

    public static void stopControlling() {
        ControlsUtil.getControls()
                .forEach(kb -> kb.setDown(ControlsUtil.isActuallyPressed(kb)));
        AbstractContraptionEntity abstractContraptionEntity = entityRef.get();

        if (!currentlyPressed.isEmpty() && abstractContraptionEntity != null)
            AllPackets.getChannel().sendToServer(new ControlsInputPacket(currentlyPressed, false,
                    abstractContraptionEntity.getId(), controlsPos, false));

        packetCooldown = 0;
        entityRef = new WeakReference<>(null);
        controlsPos = null;
        currentlyPressed.clear();

        Minecraft.getInstance().player.displayClientMessage(CreateLang.translateDirect("contraption.controls.stop_controlling"),
                true);
    }

    //for now it's just a blatant copy but I plan to expand on it
    @OnlyIn(Dist.CLIENT)
    public static void tick() {
        AbstractContraptionEntity entity = entityRef.get();
        if (entity == null)
            return;
        if (packetCooldown > 0)
            packetCooldown--;

        if (entity.isRemoved() || InputConstants.isKeyDown(Minecraft.getInstance()
                .getWindow()
                .getWindow(), GLFW.GLFW_KEY_ESCAPE)) {
            BlockPos pos = controlsPos;
            stopControlling();
            AllPackets.getChannel()
                    .sendToServer(new ControlsInputPacket(currentlyPressed, false, entity.getId(), pos, true));
            return;
        }

        Vector<KeyMapping> controls = RocketControlsUtil.getControls();
        Collection<Integer> pressedKeys = new HashSet<>();
        for (int i = 0; i < controls.size(); i++) {
            if (RocketControlsUtil.isActuallyPressed(controls.get(i)))
                pressedKeys.add(i);
        }

        Collection<Integer> newKeys = new HashSet<>(pressedKeys);
        Collection<Integer> releasedKeys = currentlyPressed;
        newKeys.removeAll(releasedKeys);
        releasedKeys.removeAll(pressedKeys);

        // Released Keys
        if (!releasedKeys.isEmpty()) {
            AllPackets.getChannel()
                    .sendToServer(new ControlsInputPacket(releasedKeys, false, entity.getId(), controlsPos, false));
//			AllSoundEvents.CONTROLLER_CLICK.playAt(player.level, player.blockPosition(), 1f, .5f, true);
        }

        // Newly Pressed Keys
        if (!newKeys.isEmpty()) {
            AllPackets.getChannel().sendToServer(new ControlsInputPacket(newKeys, true, entity.getId(), controlsPos, false));
            packetCooldown = PACKET_RATE;
//			AllSoundEvents.CONTROLLER_CLICK.playAt(player.level, player.blockPosition(), 1f, .75f, true);
        }

        // Keepalive Pressed Keys
        if (packetCooldown == 0) {
//			if (!pressedKeys.isEmpty()) {
            AllPackets.getChannel()
                    .sendToServer(new ControlsInputPacket(pressedKeys, true, entity.getId(), controlsPos, false));
            packetCooldown = PACKET_RATE;
//			}
        }

        currentlyPressed = pressedKeys;
        controls.forEach(kb -> kb.setDown(false));
    }

    @Nullable
    public static AbstractContraptionEntity getContraption() {
        return entityRef.get();
    }

    @Nullable
    public static BlockPos getControlsPos() {//used for rendering
        return controlsPos;
    }

}
