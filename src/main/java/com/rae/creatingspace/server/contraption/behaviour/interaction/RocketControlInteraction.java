package com.rae.creatingspace.server.contraption.behaviour.interaction;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.client.gui.menu.RocketMenu;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.MountedStorageInteraction;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class RocketControlInteraction extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos,
                                           AbstractContraptionEntity contraptionEntity) {
        if (contraptionEntity instanceof RocketContraptionEntity rocketContraption) {
            if ((player instanceof ServerPlayer serverPlayer)) {
                /*serverPlayer.openMenu(
                        new SimpleMenuProvider((id, inv, p) ->
                        RocketMenu.create(id, inv, rocketContraption), Component.translatable("container.my_item_menu")));*/
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inv, p) -> RocketMenu.create(id, inv, rocketContraption), Component.translatable("container.my_item_menu")), buf ->
                        buf.writeVarInt(rocketContraption.getId()));
                return true;
            }
            CreatingSpace.LOGGER.info("client can't open gui, it's the server that does");
            return true;
        }
        return false;
    }

    @Override
    public void handleEntityCollision(Entity entity, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
    }

}