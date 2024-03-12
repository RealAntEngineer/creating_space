package com.rae.creatingspace.server.event;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.CSDamageSources;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.server.armor.OxygenBacktankUtil;
import com.rae.creatingspace.server.blocks.atmosphere.OxygenBlock;
import com.rae.creatingspace.utilities.CSDimensionUtil;
import com.rae.creatingspace.utilities.CustomTeleporter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = CreatingSpace.MODID)
public class CSEventHandler {
    public CSEventHandler() {
    }

    @SubscribeEvent
    public static void entityLivingEvent(LivingEvent.LivingTickEvent livingTickEvent){
        final LivingEntity entityLiving = livingTickEvent.getEntity();
        Level level = entityLiving.level();
        ResourceKey<Level> dimension = level.dimension();
        if (CSDimensionUtil.isOrbit(level.dimensionTypeId())){
            if (!level.isClientSide){
                if (entityLiving instanceof ServerPlayer player){
                    if (player.getY() < level.dimensionType().minY()+10){
                        ResourceKey<Level> dimensionToTeleport = CSDimensionUtil.planetUnder(dimension);

                        if (dimensionToTeleport!=null) {
                            ServerLevel destServerLevel = Objects.requireNonNull(level.getServer()).getLevel(dimensionToTeleport);

                            assert destServerLevel != null;
                            if (player.isPassenger()) {
                                Entity vehicle = player.getVehicle();
                                assert vehicle != null;
                                vehicle.ejectPassengers();
                                vehicle.changeDimension(destServerLevel, new CustomTeleporter(destServerLevel));
                                player.changeDimension(destServerLevel, new CustomTeleporter(destServerLevel));
                                player.startRiding(vehicle,true);
                            } else {
                                player.changeDimension(destServerLevel, new CustomTeleporter(destServerLevel));

                            }
                        }
                    }
                }
            }
        }

        if (entityLiving.tickCount % 20 == 0) {
            if (!isInO2(entityLiving)&&entityLiving.isAttackable()) {
                if (entityLiving instanceof ServerPlayer player)  {
                    if (playerNeedEquipment(player)) {
                        if (checkPlayerO2Equipment(player)) {
                            ItemStack tank = player.getItemBySlot(EquipmentSlot.CHEST);
                            OxygenBacktankUtil.consumeOxygen(player, tank, 1);
                        } else {
                            player.hurt(CSDamageSources.no_oxygen(level), 0.5f);

                        }
                    }
                }else if (!(TagsInit.CustomEntityTag.SPACE_CREATURES.matches(entityLiving))) {
                    entityLiving.hurt(CSDamageSources.no_oxygen(level), 0.5f);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event, event.player);
        }
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable Event event, Entity entity) {
        if (entity == null)
            return;
        if ((entity.level().dimension()) == Level.OVERWORLD) {
            entity.setNoGravity(false);
        }
        if ((entity.level().dimension()) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("creating_space_gravity_addon:deleted_mod_element")))) {
            entity.setNoGravity(true);
        } else {
            entity.setNoGravity(false);
        }
        if ((entity.level().dimension()) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("creating_space_gravity_addon:deleted_mod_element")))) {
            entity.setNoGravity(true);
        } else {
            entity.setNoGravity(false);
        }
        if ((entity.level().dimension()) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("creating_space_gravity_addon:deleted_mod_element")))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 1, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 2, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 6, false, false));
        } else {
            entity.setNoGravity(false);
        }
    }


    @SubscribeEvent
    public static void playerSleeping(SleepFinishedTimeEvent sleepFinishedEvent) {
        sleepFinishedEvent.getLevel().getServer().getLevel(Level.OVERWORLD).setDayTime(sleepFinishedEvent.getNewTime());
        /*for (ServerLevel serverlevel : sleepFinishedEvent.getLevel().getServer().getAllLevels()) {
            serverlevel.setDayTime(sleepFinishedEvent.getNewTime());
        }*/
    }

    public static boolean checkPlayerO2Equipment(ServerPlayer player){

        ItemStack chestPlate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots =  player.getItemBySlot(EquipmentSlot.FEET);

        if (TagsInit.CustomItemTags.OXYGEN_SOURCES.matches(chestPlate) && OxygenBacktankUtil.hasOxygenRemaining(chestPlate)){
            return TagsInit.CustomItemTags.SPACESUIT.matches(chestPlate)&&
                    TagsInit.CustomItemTags.SPACESUIT.matches(helmet)&&
                    TagsInit.CustomItemTags.SPACESUIT.matches(leggings)&&
                    TagsInit.CustomItemTags.SPACESUIT.matches(boots);
        }

        return false;
    }

    public static boolean playerNeedEquipment(ServerPlayer player){
        return !player.isCreative();
    }

    public static boolean isInO2(LivingEntity entity){
        Level level = entity.level();
        //TODO use this instead, with tags for the biome
        //  level.getBiome(entity.getOnPos()).getTagKeys().toList();
        if (CSDimensionUtil.hasO2Atmosphere(level.dimension())){
            return true;
        }
        AABB colBox = entity.getBoundingBox();
        Stream<BlockState> blockStateStream  = level.getBlockStates(colBox);
        for (BlockState state : blockStateStream.toList()) {
            if (isStateBreathable(state)){
                return true;
            }
        }
        return false;
    }


    private static boolean isStateBreathable(BlockState state) {
        return state.getBlock() instanceof OxygenBlock && state.getValue(OxygenBlock.BREATHABLE);
    }

}
