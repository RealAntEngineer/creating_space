package com.rae.creatingspace.mixin.level;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.planets.PlanetsPositionsHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelTimeAccess;
import net.minecraft.world.level.dimension.DimensionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static com.rae.creatingspace.api.planets.OrbitParameter.BASE_BODY;


@Mixin(Level.class)
public abstract class LevelMixin implements LevelTimeAccess{

    @Shadow public abstract DimensionType dimensionType();

    @Override
    public float getTimeOfDay(float partialTick) {
        if ((Object)this instanceof Level level) {
            ResourceLocation dimensionId = level.dimension().location();
            // Custom logic for modifying time based on dimension
            if (PlanetsPositionsHandler.getOrbitParam(dimensionId)!=null){
                return cS_1_19_2$calculateCustomTime(dimensionId, (level.getDayTime() + partialTick) / 24000);
            }
            else{
                return this.dimensionType().timeOfDay(this.dayTime());
            }
        }
        return 0;
    }

    /**
     *
     * @param dimensionId
     * @param time
     * @return 0 is morning and 0.5 is night
     */
    @Unique
    private float cS_1_19_2$calculateCustomTime(ResourceLocation dimensionId, float time) {
        PlanetsPositionsHandler.SkyPos pos = PlanetsPositionsHandler.getSkyPos(dimensionId, BASE_BODY, time);
        float rotOffset = PlanetsPositionsHandler.getOrbitParam(dimensionId).getRotAngle(time) % Mth.TWO_PI;
        float theta = pos.getTheta() - rotOffset;

        // normalize theta to [0, 2*PI)
        float normalizedTheta = (theta % Mth.TWO_PI + Mth.TWO_PI) % Mth.TWO_PI;

        // convert to fraction of the day
        float dayFraction = normalizedTheta / Mth.TWO_PI;

        return dayFraction;
    }
}