package com.rae.creatingspace.content.event;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.life_support.INeedOxygen;
import com.rae.creatingspace.init.CSDamageSources;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.CommandsInit;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankUtil;
import com.rae.creatingspace.content.life_support.sealer.RoomAtmosphere;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import com.rae.creatingspace.content.rocket.CustomTeleporter;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllItems;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = CreatingSpace.MODID)
public class CSEventHandler {
    public CSEventHandler() {
    }

    @SubscribeEvent
    public static void missingEntries(MissingMappingsEvent event){

        // Remap items
        for (MissingMappingsEvent.Mapping<Item> mapping : event.getMappings(Registries.ITEM, "creatingspace")) {
            switch (mapping.getKey().getPath()) {
                case "crystal_shard" -> mapping.remap(ItemInit.NICKEL_SULFATE_SHARD.get());
                case "crystal_block" -> mapping.remap(BlockInit.NICKEL_SULFATE_BLOCK.get().asItem());
                case "crystal_cluster" -> mapping.remap(BlockInit.NICKEL_SULFATE_CLUSTER.get().asItem());
                case "budding_crystal" -> mapping.remap(BlockInit.BUDDING_NICKEL_SULFATE.get().asItem());
                case "large_crystal_bug" -> mapping.remap(BlockInit.LARGE_NICKEL_SULFATE_BUD.get().asItem());
                case "medium_crystal_bug" -> mapping.remap(BlockInit.MEDIUM_NICKEL_SULFATE_BUD.get().asItem());
                case "small_crystal_bug" -> mapping.remap(BlockInit.SMALL_NICKEL_SULFATE_BUD.get().asItem());
            }
        }

        // Remap blocks
        for (MissingMappingsEvent.Mapping<Block> mapping : event.getMappings(Registries.BLOCK, "creatingspace")) {
            switch (mapping.getKey().getPath()) {
                case "crystal_block" -> mapping.remap(BlockInit.NICKEL_SULFATE_BLOCK.get());
                case "crystal_cluster" -> mapping.remap(BlockInit.NICKEL_SULFATE_CLUSTER.get());
                case "budding_crystal" -> mapping.remap(BlockInit.BUDDING_NICKEL_SULFATE.get());
                case "large_crystal_bug" -> mapping.remap(BlockInit.LARGE_NICKEL_SULFATE_BUD.get());
                case "medium_crystal_bug" -> mapping.remap(BlockInit.MEDIUM_NICKEL_SULFATE_BUD.get());
                case "small_crystal_bug" -> mapping.remap(BlockInit.SMALL_NICKEL_SULFATE_BUD.get());
            }
        }
    }

    @SubscribeEvent
    public static void entityLivingEvent(LivingEvent.LivingTickEvent livingTickEvent){
        final LivingEntity entityLiving = livingTickEvent.getEntity();
        Level level = entityLiving.level();
        ResourceLocation dimension = level.dimension().location();
        //fall from orbit
        if (CSDimensionUtil.isOrbit(level.dimension().location())){
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
        //suffocating
        if (entityLiving.tickCount % 20 == 0) {
            if (!inO2(entityLiving) && entityLiving.isAttackable()) {
                if (entityLiving instanceof ServerPlayer player)  {
                    if (playerNeedEquipment(player)) {
                        if (checkPlayerO2Equipment(player)) {
                            ItemStack tank = player.getItemBySlot(EquipmentSlot.CHEST);
                            OxygenBacktankUtil.consumeOxygen(player, tank, 1);
                        } else {
                            player.hurt(CSDamageSources.no_oxygen(level), 2f);

                        }
                    }
                } else if (!(TagsInit.CustomEntityTag.SPACE_CREATURES.matches(entityLiving))) {
                    entityLiving.hurt(CSDamageSources.no_oxygen(level), 2f);
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

    @SubscribeEvent
    public static void playerSleeping(SleepFinishedTimeEvent sleepFinishedEvent) {
        //the overworld control the clock for every dimensions, so if we sleep it's the overworld that need to get updated
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

        boolean flag = ((INeedOxygen)entity).insideOxygenRoom();
        ((INeedOxygen)entity).setInsideOxygenRoom(false);
        return flag;

    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandsInit.register(event.getDispatcher());
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
}