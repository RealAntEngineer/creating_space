package com.rae.creatingspace.content.rocket.contraption.behaviour.interaction;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;


public class RocketControlInteraction extends MovingInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos,
                                           AbstractContraptionEntity contraptionEntity) {
        if (contraptionEntity instanceof RocketContraptionEntity rocketContraption) {
            if ((player instanceof ServerPlayer serverPlayer)) {
                player.openMenu( rocketContraption);
                return true;
            }
            return true;
        }
        return false;
    }

    @Override
    public void handleEntityCollision(Entity entity, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
    }

}