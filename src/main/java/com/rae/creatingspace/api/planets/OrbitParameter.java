package com.rae.creatingspace.api.planets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;


/**
 * orbitedBody  this is the dimension location for the dimension supposed to be bellow (for exemple in an orbit dimension)
 *                     used in both server and client (used when teleporting from an orbit to the planet bellow + rendering of dimension effect)
 * i            inclination of the orbit plane in degrees (yes it's inconsistent.)
 * omega        rotation of the apogee compared to the positive X (start offset because it's a circle)
 * r            radius of the orbit in 1000 km (only circles are supported)
 * orbT         length of time to do a full orbit
 * rotationAxis axis of rotation for the  rotation of the planet
 * rotT         length of time to do a full rotation
 */
public class OrbitParameter {
    @NotNull ResourceLocation orbitedBody;
    float size;
    float i;
    float omega;
    float r;
    float orbT;
    float orbStart;
    Vec3 rotationAxis;
    float rotT;
    float rotStart;
    Quaternionf alignToAxis;
    public static final ResourceLocation BASE_BODY = new ResourceLocation("sun");
    public static final Codec<OrbitParameter> CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                                    ResourceLocation.CODEC.optionalFieldOf("orbitedBody", BASE_BODY).forGetter(p -> p.orbitedBody),
                                    Codec.FLOAT.optionalFieldOf("size",1.0f).forGetter(p->p.size),
                                    Codec.FLOAT.optionalFieldOf("i",0f).forGetter(p->p.i),
                                    Codec.FLOAT.optionalFieldOf("omega",0f).forGetter(p->p.omega),
                                    Codec.FLOAT.fieldOf("r").forGetter(p->p.r),
                                    Codec.FLOAT.fieldOf("orbT").forGetter(p->p.orbT),
                                    Codec.FLOAT.optionalFieldOf("orbStart",0f).forGetter(p->p.orbStart),
                                    Vec3.CODEC.optionalFieldOf("rotationAxis", new Vec3(0,1,0)).forGetter(p->p.rotationAxis),
                                    Codec.FLOAT.fieldOf("rotT").forGetter(p->p.rotT),
                                    Codec.FLOAT.optionalFieldOf("rotStart",0f).forGetter(p->p.rotStart))
                            .apply(instance, OrbitParameter::new));
    public OrbitParameter(@NotNull ResourceLocation orbitedBody, float size, float i, float omega, float r, float orbT,float orbStart, Vec3 rotationAxis, float rotT, float rotStart, Quaternionf alignToAxis) {
        this.orbitedBody = orbitedBody;
        this.size = size;
        this.i = i;
        this.omega = omega;
        this.r = r;
        this.orbT = orbT;
        this.orbStart = orbStart;
        this.rotationAxis = rotationAxis;
        this.rotT = rotT;
        this.rotStart = rotStart;
        this.alignToAxis = alignToAxis;
    }

    public OrbitParameter(ResourceLocation orbitedBody, float size, float i, float omega, float r, float orbT,float orbStart, Vec3 rotationAxis, float rotT, float rotStart){
        this(orbitedBody,size,  i,  omega,  r, orbT,orbStart, rotationAxis, rotT, rotStart,new Quaternionf().rotationTo(new Vector3f(0, 1, 0),rotationAxis.toVector3f()));
    }

    public @NotNull ResourceLocation getOrbitedBody() {
        return orbitedBody;
    }

    public float getSize() {
        return size;
    }

    public float getI() {
        return i;
    }

    public float getOmega() {
        return omega;
    }

    public float getR() {
        return r;
    }

    public Quaternionf getRotQuad(float time) {
        return new Quaternionf().fromAxisAngleRad(rotationAxis.toVector3f(),getRotAngle(time)).mul(alignToAxis);
    }
    public float getRotAngle(float time){
        return (time / rotT + rotStart) * Mth.TWO_PI;
    }
    public float getOrbAngle(float time){
        return (time / orbT + orbStart) * Mth.TWO_PI;
    }
}
