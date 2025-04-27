package com.rae.creatingspace.api.contraption;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.world.phys.Vec2;

public class AngleLerpedVec2 {
    LerpedFloat pitch;
    LerpedFloat yaw;

    public AngleLerpedVec2(float x, float y, float z) {
        pitch = LerpedFloat.angular();
        yaw = LerpedFloat.angular();
        pitch.setValue(x);
        yaw.setValue(y);
    }
    public void tickChaser(){
        pitch.tickChaser();
        yaw.tickChaser();
    }

    public void chase(float pitch, float yaw, float speed){
        this.pitch.chase(pitch,speed, LerpedFloat.Chaser.LINEAR);
        this.yaw.chase(yaw,speed, LerpedFloat.Chaser.LINEAR);
    }

    public Vec2 getVec3(float partialTicks) {
        return new Vec2(pitch.getValue(partialTicks), yaw.getValue(partialTicks));
    }
}
