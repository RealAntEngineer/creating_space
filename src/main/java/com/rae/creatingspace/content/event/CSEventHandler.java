package com.rae.creatingspace.content.event;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.life_support.INeedOxygen;
import com.rae.creatingspace.init.CSDamageSources;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.content.rocket.engine.design.DesignCommands;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankUtil;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.Math.abs;

@Mod.EventBusSubscriber(modid = CreatingSpace.MODID)
public class CSEventHandler {
    public CSEventHandler() {
    }

    @SubscribeEvent
    public static void entityLivingEvent(LivingEvent.LivingTickEvent livingTickEvent){
        final LivingEntity entityLiving = livingTickEvent.getEntity();
        Level level = entityLiving.level();
        ResourceLocation dimension = level.dimension().location();
        //fall from orbit
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
        //suffocating
        if (entityLiving.tickCount % 20 == 0) {
            if (!inO2(entityLiving) && entityLiving.isAttackable()) {
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
        /*System.out.println(0.5-sleepFinishedEvent.getLevel().getTimeOfDay(sleepFinishedEvent.getNewTime()));
        float newTime = dichotomy((t)-> (float) (0.5-sleepFinishedEvent.getLevel().getTimeOfDay(t)),
                sleepFinishedEvent.getNewTime(),sleepFinishedEvent.getNewTime()*1000,1);
*/
        Objects.requireNonNull(Objects.requireNonNull(sleepFinishedEvent.getLevel().getServer()).getLevel(Level.OVERWORLD))
                .setDayTime(sleepFinishedEvent.getNewTime());
    }
    //@SubscribeEvent
    public static void blockChange(BlockEvent.NeighborNotifyEvent event){
        event.getNotifiedSides().forEach(
                direction -> {
                    List<RoomAtmosphere> list = event.getLevel().getEntitiesOfClass(RoomAtmosphere.class,new AABB(event.getPos().relative(direction)));
                    System.out.println(list);
                }
        );
    }
    //TODO put in the formic API
    private static float dichotomy(Function<Float, Float> function, float a, float b, float epsilon) {
        try {
            if (function.apply(a) * function.apply(b) > 0) {  //On vérifie l 'encadrement de la fonction
                throw new RuntimeException("Mauvais choix de a ou b.");
            } else {
                float m = (float) ((a + b) / 2.);
                while (abs(a - b) > epsilon) {
                    if (function.apply(m) == 0.0) {
                        return m;
                    } else if (function.apply(a) * function.apply(m) > 0) {
                        a = m;
                    } else {
                        b = m;
                    }
                    m = (a + b) / 2;
                }
                return m;
            }
        } catch (RuntimeException e) {
            System.out.println(e);
            return 0;
        }
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