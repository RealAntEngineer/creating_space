package com.rae.creatingspace.legacy.utilities.data;

import com.rae.creatingspace.legacy.utilities.CSNBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;

public class FlightDataHelper {

    //replace that on the rocket creation -> cleaner
    public record RocketAssemblyData(PropellantStatusData propellantStatusData, float thrust, float weight, boolean hasFailed){
        public static RocketAssemblyData create(HashMap<TagKey<Fluid>, Integer> massForEachPropellant, HashMap<TagKey<Fluid>,Integer> consumedMassForEachPropellant, float finalPropellantMass, float thrust,float weight){
            PropellantStatusData data = PropellantStatusData.createFromPropellantMap(massForEachPropellant, consumedMassForEachPropellant, finalPropellantMass);
            boolean hasFailed = data.status.isFailReason;
            hasFailed = hasFailed || thrust < weight;
            return new RocketAssemblyData(data,thrust,weight,hasFailed);
        }

        public static RocketAssemblyData fromNBT(CompoundTag tag){
            if (tag==null) return null;
            if (tag.isEmpty()) return null;
            HashMap<TagKey<Fluid>, Integer> massForEachPropellant = CSNBTUtil.fromNBTtoMapFluidTagsInteger(tag.getCompound("massForEachPropellant"));
            HashMap<TagKey<Fluid>,Integer> consumedMassForEachPropellant = CSNBTUtil.fromNBTtoMapFluidTagsInteger(tag.getCompound("consumedMassForEachPropellant"));
            float finalPropellantMass = tag.getFloat("finalPropellantMass");
            float thrust = tag.getFloat("thrust");
            float weight = tag.getFloat("weight");
            return create(massForEachPropellant,consumedMassForEachPropellant,finalPropellantMass,thrust,weight);
        }
        public static CompoundTag toNBT(RocketAssemblyData data) {
            CompoundTag newTag = new CompoundTag();
            if (data!=null) {
                newTag.put("massForEachPropellant", CSNBTUtil.fromMapFluidTagsIntegerToNBT(data.propellantStatusData().massForEachPropellant));
                newTag.put("consumedMassForEachPropellant", CSNBTUtil.fromMapFluidTagsIntegerToNBT(data.propellantStatusData().consumedMassForEachPropellant));
                newTag.putFloat("finalPropellantMass", data.propellantStatusData().finalPropellantMass);
                newTag.putFloat("thrust",data.thrust);
                newTag.putFloat("weight",data.weight);
            }

            return newTag;
        }


    }
    public enum PropellantStatus {
        ENOUGH_PROPELLANT(false),
        NOT_ENOUGH_PROPELLANT,
        ONE_OR_MORE_PROPELLANT_IS_MISSING;
        public final boolean isFailReason;
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
        public final boolean isFailReason;
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
            else {
                for (TagKey<Fluid> fluidTag : consumedMassForEachPropellant.keySet()) {
                    Integer mass = massForEachPropellant.get(fluidTag);
                    if (mass == null) {
                        status = PropellantStatus.ONE_OR_MORE_PROPELLANT_IS_MISSING;
                        break;
                    } else if (mass < consumedMassForEachPropellant.get(fluidTag)) {
                        status = PropellantStatus.ONE_OR_MORE_PROPELLANT_IS_MISSING;
                        break;
                    }
                }
            }
            return new PropellantStatusData(status,massForEachPropellant,consumedMassForEachPropellant,finalPropellantMass);
        }
    }

}
