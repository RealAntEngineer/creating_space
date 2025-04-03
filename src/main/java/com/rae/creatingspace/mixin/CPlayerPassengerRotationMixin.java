package com.rae.creatingspace.mixin;

import com.rae.creatingspace.api.contraption.Synced2AxisContraptionEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.seat.ContraptionPlayerPassengerRotation;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContraptionPlayerPassengerRotation.class)
public class CPlayerPassengerRotationMixin {

    @Shadow(remap = false)
    static boolean active;

    @Shadow(remap = false)
    static int prevId;

    @Shadow(remap = false)
    static float prevYaw;

    @Shadow(remap = false)
    static float prevPitch;

    @Inject(method = "tick", at = @At("HEAD"), remap = false, cancellable = true)
    private static void frame(CallbackInfo ci){
        Player player = Minecraft.getInstance().player;
        if (!active)
            return;
        if (player == null || !player.isPassenger()) {
            prevId = 0;
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof Synced2AxisContraptionEntity contraptionEntity))
            return;

        AbstractContraptionEntity.ContraptionRotationState rotationState = contraptionEntity.getRotationState();

        float yaw = contraptionEntity.getViewYRot(AnimationTickHolder.getPartialTicks());
        float pitch =contraptionEntity.getViewXRot(AnimationTickHolder.getPartialTicks());

        if (prevId != contraptionEntity.getId()) {
            prevId = contraptionEntity.getId();
            prevYaw = yaw;
            prevPitch = pitch;
        }

        float yawDiff = AngleHelper.getShortestAngleDiff(yaw, prevYaw);
        float pitchDiff = AngleHelper.getShortestAngleDiff(pitch, prevPitch);

        prevYaw = yaw;
        prevPitch = pitch;

        float yawRelativeToTrain = Mth.abs(AngleHelper.getShortestAngleDiff(player.getYRot(), -yaw - 90));
        if (yawRelativeToTrain > 120)
            pitchDiff *= -1;
        else if (yawRelativeToTrain > 60)
            pitchDiff *= 0;

        player.setYRot((float) (player.getYRot() + yawDiff));
        player.setXRot((float) (player.getXRot() + pitchDiff));
        ci.cancel();

    }
}
