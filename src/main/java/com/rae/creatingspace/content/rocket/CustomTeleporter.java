package com.rae.creatingspace.content.rocket;

import com.rae.creatingspace.content.planets.CSDimensionUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
//TODO ask Chatgpt
public class CustomTeleporter {
    protected final ServerLevel level;

    public CustomTeleporter(ServerLevel level) {
        this.level = level;
    }
    public static DimensionTransition getTransition(Entity entity, ServerLevel destWorld) {
        double height;
        height = CSDimensionUtil.arrivalHeight(destWorld.dimension().location());
        Vec3 position;
        if ( entity instanceof RocketContraptionEntity rocketContraptionEntity){

            position = new Vec3(
                    rocketContraptionEntity.getInitialPosMap().get(destWorld.dimension().location()).getX(),
                    height,
                    rocketContraptionEntity.getInitialPosMap().get(destWorld.dimension().location()).getZ());

        }
        else {
            position = new Vec3(
                    entity.getX(),
                    height,
                    entity.getZ());
        }
        return new DimensionTransition(destWorld,position, Vec3.ZERO, entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING);
    }
}
