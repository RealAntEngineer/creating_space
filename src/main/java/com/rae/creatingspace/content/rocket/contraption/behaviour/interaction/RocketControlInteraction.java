package com.rae.creatingspace.content.rocket.contraption.behaviour.interaction;

import com.google.common.base.Objects;
import com.rae.creatingspace.content.rocket.contraption.entity.RocketContraptionEntity;
import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsHandler;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.UUID;

public class RocketControlInteraction extends MovingInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos,
                                           AbstractContraptionEntity contraptionEntity) {
        if (contraptionEntity instanceof RocketContraptionEntity rocketContraption) {
            /*
            if ((player instanceof ServerPlayer serverPlayer)) {

                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inv, p) -> RocketMenu.create(id, inv, rocketContraption), Component.translatable("container.my_item_menu")), buf ->
                        buf.writeVarInt(rocketContraption.getId()));
                return true;
            }
            return true;
             */
            if (AllItems.WRENCH.isIn(player.getItemInHand(activeHand)))
                return false;

            UUID currentlyControlling = contraptionEntity.getControllingPlayer()
                    .orElse(null);

            if (currentlyControlling != null) {
                contraptionEntity.stopControlling(localPos);
                if (Objects.equal(currentlyControlling, player.getUUID()))
                    return true;
            }

            if (!contraptionEntity.startControlling(localPos, player))
                return false;

            contraptionEntity.setControllingPlayer(player.getUUID());
            if (player.level().isClientSide)
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> RocketControlsHandler.startControlling(contraptionEntity, localPos));
            return true;
        }
        return false;
    }

    @Override
    public void handleEntityCollision(Entity entity, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
    }

}