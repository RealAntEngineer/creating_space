package com.rae.creatingspace.utilities.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;

public class FlightDataHelper {

    //replace that on the rocket creation -> cleaner
    public record RocketAssemblyData(PropellantStatusData propellantStatusData, boolean hasFailed){
        public static RocketAssemblyData createFromPropellantMap(HashMap<TagKey<Fluid>, Integer> massForEachPropellant, HashMap<TagKey<Fluid>,Integer> consumedMassForEachPropellant, float finalPropellantMass){
            PropellantStatusData data = PropellantStatusData.createFromPropellantMap(massForEachPropellant, consumedMassForEachPropellant, finalPropellantMass);
            boolean hasFailed = data.status.isFailReason;
            return new RocketAssemblyData(data,hasFailed);
        }

    }
    public enum PropellantStatus {
        ENOUGH_PROPELLANT(false),
        NOT_ENOUGH_PROPELLANT,
        ONE_OR_MORE_PROPELLANT_IS_MISSING;
        final boolean isFailReason;
        PropellantStatus() {
            this.isFailReason = true;
        }
        PropellantStatus(boolean isFailReason) {
            this.isFailReason = isFailReason;
        }
    }
    public enum CollisionStatus {
        NO_COLLISION(false),//to do -> make the rocket controls register unmovable block;
        COLLISION_ON_DESCENT(false),
        COLLISION_ON_ASCENT;
        final boolean isFailReason;
        CollisionStatus() {
            this.isFailReason = true;
        }
        CollisionStatus(boolean isFailReason) {
            this.isFailReason = isFailReason;
        }
    }
    public record PropellantStatusData(
            PropellantStatus status,HashMap<TagKey<Fluid>,
            Integer> massForEachPropellant,
            HashMap<TagKey<Fluid>,Integer> consumedMassForEachPropellant,
            float finalPropellantMass){
        public static PropellantStatusData createFromPropellantMap(HashMap<TagKey<Fluid>, Integer> massForEachPropellant, HashMap<TagKey<Fluid>,Integer> consumedMassForEachPropellant, float finalPropellantMass){
            PropellantStatus status = PropellantStatus.ENOUGH_PROPELLANT;
            if (finalPropellantMass < 0){
                status = PropellantStatus.NOT_ENOUGH_PROPELLANT;
            }
            for (TagKey<Fluid> fluidTag: consumedMassForEachPropellant.keySet()){
                Integer mass = massForEachPropellant.get(fluidTag);
                if (mass==null){
                    status = PropellantStatus.ONE_OR_MORE_PROPELLANT_IS_MISSING;
                    break;
                }else if(mass < consumedMassForEachPropellant.get(fluidTag)){
                    status = PropellantStatus.ONE_OR_MORE_PROPELLANT_IS_MISSING;
                    break;
                }
            }
            return new PropellantStatusData(status,massForEachPropellant,consumedMassForEachPropellant,finalPropellantMass);
        }
    }

}
