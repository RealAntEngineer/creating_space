package com.rae.creatingspace.api.contraption;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.world.phys.Vec3;

public class LinearLerpedVec3 {

    LerpedFloat lerpedX;
    LerpedFloat lerpedY;
    LerpedFloat lerpedZ;

    public LinearLerpedVec3(float x, float y, float z) {
        lerpedX = LerpedFloat.linear().startWithValue(0);
        lerpedY = LerpedFloat.linear().startWithValue(0);
        lerpedZ = LerpedFloat.linear().startWithValue(0);
        lerpedX.setValue(x);
        lerpedY.setValue(y);
        lerpedZ.setValue(z);
    }
    public void tickChaser(){
        lerpedX.tickChaser();
        lerpedY.tickChaser();
        lerpedZ.tickChaser();
    }

    public void chase(float x, float y, float z, int time){
        lerpedX.chaseTimed(x,time);
        lerpedY.chaseTimed(y,time);
        lerpedZ.chaseTimed(z,time);
    }
    public void chase(Vec3 vec, int speed){
        chase((float) vec.x, (float) vec.y, (float) vec.z, speed);
    }

    public Vec3 getVec3(float partialTicks) {
        return new Vec3(lerpedX.getValue(partialTicks), lerpedY.getValue(partialTicks), lerpedZ.getValue(partialTicks));
    }
}
