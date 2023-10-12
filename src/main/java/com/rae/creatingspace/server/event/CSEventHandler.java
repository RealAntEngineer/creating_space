package com.rae.creatingspace.server.event;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.DamageSourceInit;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import com.rae.creatingspace.utilities.CustomTeleporter;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreatingSpace.MODID)
public class CSEventHandler {

    public CSEventHandler() {

    }
    @SubscribeEvent
    public static void entityLivingEvent(LivingEvent.LivingTickEvent livingTickEvent){
        final LivingEntity entityLiving = livingTickEvent.getEntity();
        Level level = entityLiving.level();
        ResourceKey<Level> dimension = level.dimension();
        if (entityLiving instanceof ServerPlayer player){
            if (DimensionInit.gravity(level.dimensionTypeId())==0){
                if (!level.isClientSide){
                    if (player.getY() < level.dimensionType().minY()+10){
                        ResourceKey<Level> dimensionToTeleport = null;
                        if (dimension == DimensionInit.EARTH_ORBIT_KEY){
                            dimensionToTeleport = Level.OVERWORLD;
                        }
                        if (dimension == DimensionInit.MOON_ORBIT_KEY){
                            dimensionToTeleport = DimensionInit.MOON_KEY;
                        }
                        if (dimensionToTeleport!=null){
                            ServerLevel destServerLevel = level.getServer().getLevel(dimensionToTeleport);

                            player.changeDimension(destServerLevel, new CustomTeleporter(destServerLevel));
                        }
                    }
                }
            }
        }

        if (entityLiving.tickCount % 20 == 0) {
            if (!(dimension == Level.OVERWORLD)&&(entityLiving.isAttackable())) {
                if (entityLiving instanceof ServerPlayer player) {
                    if (checkPlayerO2Equipment(player) && !player.isCreative()) {
                        ItemStack tank = player.getItemBySlot(EquipmentSlot.CHEST);
                        BacktankUtil.consumeAir(player, tank, 1);
                    }
                    else {
                        player.hurt(DamageSourceInit.NO_OXYGEN.source(level), 0.5f);

                    }
                }else if (!(entityLiving instanceof Skeleton)) {
                    entityLiving.hurt(DamageSourceInit.NO_OXYGEN.source(level), 0.5f);
                }
            }
        }
    }

    public static boolean checkPlayerO2Equipment(ServerPlayer player){

        ItemStack chestPlate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if ((AllItems.COPPER_BACKTANK.isIn(chestPlate)||AllItems.NETHERITE_BACKTANK.isIn(chestPlate))&& BacktankUtil.hasAirRemaining(chestPlate)){
            if(AllItems.NETHERITE_DIVING_HELMET.isIn(helmet)||AllItems.COPPER_DIVING_HELMET.isIn(helmet)){
                return true;
            }
        }

        return false;
    }
}
