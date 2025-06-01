package com.rae.creatingspace.content.event;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.fluids.storage.CryogenicTankBlockEntity;
import com.rae.creatingspace.content.fluids.storage.CryogenicTankItem;
import com.rae.creatingspace.content.life_support.INeedOxygen;
import com.rae.creatingspace.content.life_support.sealer.RoomPressuriserBlockEntity;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankBlockEntity;
import com.rae.creatingspace.init.CSDamageSources;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.legacy.saved.DesignCommands;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankUtil;
import com.rae.creatingspace.legacy.server.blockentities.ChemicalSynthesizerBlockEntity;
import com.rae.creatingspace.legacy.server.blocks.atmosphere.OxygenBlock;
import com.rae.creatingspace.content.life_support.sealer.RoomAtmosphere;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import com.rae.creatingspace.content.rocket.CustomTeleporter;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@EventBusSubscriber(modid = CreatingSpace.MODID)
public class CSEventHandler {
    public CSEventHandler() {
    }

    @SubscribeEvent
    public static void entityLivingEvent(EntityTickEvent livingTickEvent){
        if (livingTickEvent.getEntity() instanceof LivingEntity entityLiving) {
            Level level = entityLiving.level();
            ResourceLocation dimension = level.dimension().location();
            //fall from orbit
            if (CSDimensionUtil.isOrbit(level.dimension().location())) {
                if (!level.isClientSide) {
                    if (entityLiving instanceof ServerPlayer player) {
                        if (player.getY() < level.dimensionType().minY() + 10) {
                            ResourceKey<Level> dimensionToTeleport = CSDimensionUtil.planetUnder(dimension);

                            if (dimensionToTeleport != null) {
                                ServerLevel destServerLevel = Objects.requireNonNull(level.getServer()).getLevel(dimensionToTeleport);

                                assert destServerLevel != null;
                                if (player.isPassenger()) {
                                    Entity vehicle = player.getVehicle();
                                    assert vehicle != null;
                                    vehicle.ejectPassengers();
                                    vehicle.changeDimension(CustomTeleporter.getTransition(vehicle,destServerLevel));
                                    player.changeDimension(CustomTeleporter.getTransition(player,destServerLevel));
                                    player.startRiding(vehicle, true);
                                } else {
                                    player.changeDimension(CustomTeleporter.getTransition(player,destServerLevel));

                                }
                            }
                        }
                    }
                }
            }
            //suffocating
            if (entityLiving.tickCount % 20 == 0) {
                if (!inO2(entityLiving) && entityLiving.isAttackable()) {
                    if (entityLiving instanceof ServerPlayer player) {
                        if (playerNeedEquipment(player)) {
                            if (checkPlayerO2Equipment(player)) {
                                ItemStack tank = player.getItemBySlot(EquipmentSlot.CHEST);
                                OxygenBacktankUtil.consumeOxygen(player, tank, 1);
                            } else {
                                player.hurt(CSDamageSources.no_oxygen(level), 0.5f);

                            }
                        }
                    } else if (!(TagsInit.CustomEntityTag.SPACE_CREATURES.matches(entityLiving))) {
                        entityLiving.hurt(CSDamageSources.no_oxygen(level), 0.5f);
                    }
                }
            }
            //overheating
            if (entityLiving.tickCount % 20 == 0 && !inO2(entityLiving) && entityLiving.isAttackable()) {
                if (entityLiving instanceof ServerPlayer player) {
                    if (playerNeedEquipment(player) && player.level().dimension().location().toString().equals("creatingspace:venus") && !checkPlayerO2Equipment(player)) {
                        player.hurt(CSDamageSources.over_heat(level), 0.5F);
                    }
                } else if (!TagsInit.CustomEntityTag.SPACE_CREATURES.matches(entityLiving)) {
                    entityLiving.hurt(CSDamageSources.over_heat(level), 0.5F);
                }
            }
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

    public static boolean inO2(LivingEntity entity) {
        Level level = entity.level();
        //TODO use this instead, with tags for the biome
        //  level.getBiome(entity.getOnPos()).getTagKeys().toList();
        if (CSDimensionUtil.hasO2Atmosphere(level.getBiome(entity.getOnPos()))) {
            return true;
        }
        /*
        AABB colBox = entity.getBoundingBox();
        Stream<BlockState> blockStateStream  = level.getBlockStates(colBox);
        for (BlockState state : blockStateStream.toList()) {
            if (isStateBreathable(state)){
                return true;
            }
        }
        List<RoomAtmosphere> entityStream = level.getEntitiesOfClass(RoomAtmosphere.class, colBox);
        for (RoomAtmosphere atmosphere : entityStream) {
            if (atmosphere.getShape().inside(colBox) && atmosphere.breathable()) {
                return true;
            }
        }*/
        boolean flag = ((INeedOxygen)entity).insideOxygenRoom();
        ((INeedOxygen)entity).setInsideOxygenRoom(false);
        return flag;

    }
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        DesignCommands.register(event.getDispatcher());
    }
    //for legacy purpose
    private static boolean isStateBreathable(BlockState state) {
        return state.getBlock() instanceof OxygenBlock && state.getValue(OxygenBlock.BREATHABLE);
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.NeighborNotifyEvent event) {
        boolean blockPlaced = false;
        Level level = (Level) event.getLevel();
        AABB colBoxInside = new AABB(event.getPos());

        List<RoomAtmosphere> entityStream = level.getEntitiesOfClass(RoomAtmosphere.class, colBoxInside);
        for (RoomAtmosphere atmosphere : entityStream) {
            if (atmosphere.getShape().inside(colBoxInside)) {
                blockPlaced = true;
            }
        }
        if (blockPlaced) {
            for (RoomAtmosphere atmosphere : entityStream) {
                if (CSConfigs.SERVER.smarterSearch.get()){
                    atmosphere.regenerateRoom(event.getPos());
                } else {
                    atmosphere.resetShape();
                    atmosphere.regenerateRoom(atmosphere.getOnPos());
                }
            }
        }
        else {
            for (Direction direction :
                    event.getNotifiedSides()) {
                AABB colBoxOutside = new AABB(event.getPos().relative(direction));
                entityStream = level.getEntitiesOfClass(RoomAtmosphere.class, colBoxOutside);

                for (RoomAtmosphere atmosphere : entityStream) {
                    if (CSConfigs.SERVER.smarterSearch.get()) {
                        atmosphere.regenerateRoom(event.getPos().relative(direction));
                    } else {
                        atmosphere.resetShape();
                        atmosphere.regenerateRoom(atmosphere.getOnPos());
                    }
                }
            }
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        //TODO this is kinda ugly and should be clean/ made better
        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            ChemicalSynthesizerBlockEntity.registerCapabilities(event);
            RoomPressuriserBlockEntity.registerCapabilities(event);
            CryogenicTankBlockEntity.registerCapabilities(event);
            CryogenicTankItem.registerCapabilities(event);
            OxygenBacktankBlockEntity.registerCapabilities(event);
        }

    }
}