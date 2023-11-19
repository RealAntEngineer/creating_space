package com.rae.creatingspace.server.event;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.DamageSourceInit;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import com.rae.creatingspace.server.armor.OxygenBacktankUtil;
import com.rae.creatingspace.server.blocks.atmosphere.OxygenBlock;
import com.rae.creatingspace.utilities.CustomTeleporter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = CreatingSpace.MODID)
public class CSEventHandler {
    public CSEventHandler() {
    }

    @SubscribeEvent
    public static void entityLivingEvent(LivingEvent.LivingTickEvent livingTickEvent){
        final LivingEntity entityLiving = livingTickEvent.getEntity();
        Level level = entityLiving.getLevel();
        ResourceKey<Level> dimension = level.dimension();
        if (entityLiving instanceof ServerPlayer player){
            if (DimensionInit.isOrbit(level.dimensionTypeId())){
                if (!level.isClientSide){
                    if (player.getY() < level.dimensionType().minY()+10){
                        ResourceKey<Level> dimensionToTeleport = DimensionInit.planetUnder(dimension);

                        if (dimensionToTeleport!=null){
                            ServerLevel destServerLevel = Objects.requireNonNull(level.getServer()).getLevel(dimensionToTeleport);

                            assert destServerLevel != null;
                            player.changeDimension(destServerLevel, new CustomTeleporter(destServerLevel));
                        }
                    }
                }
            }
        }

        if (entityLiving.tickCount % 20 == 0) {
            if (!isInO2(entityLiving) && entityLiving.isAttackable()) {
                if (entityLiving instanceof ServerPlayer player)  {
                    if (playerNeedEquipment(player)) {
                        if (checkPlayerO2Equipment(player)) {
                            ItemStack tank = player.getItemBySlot(EquipmentSlot.CHEST);
                            OxygenBacktankUtil.consumeOxygen(player, tank, 1);
                        } else {
                            player.hurt(DamageSourceInit.NO_OXYGEN, 0.5f);

                        }
                    }
                }else if (!(TagsInit.CustomEntityTag.SPACE_CREATURES.matches(entityLiving))) {
                    entityLiving.hurt(DamageSourceInit.NO_OXYGEN, 0.5f);
                }
            }
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

    public static boolean isInO2(LivingEntity entity){
        Level level = entity.getLevel();
        if (DimensionInit.hasO2Atmosphere(level.dimension())){
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


    @SubscribeEvent
    public static void onWaterSourceCreated(BlockEvent.CreateFluidSourceEvent fluidSourceEvent){
        Level level = (Level) fluidSourceEvent.getLevel();
        if (!level.isClientSide()){
            if (!DimensionInit.hasO2Atmosphere(level.dimension())){
                fluidSourceEvent.setCanceled(true);
            }
        }
    }



    private static boolean isStateBreathable(BlockState state) {
        return state.getBlock() instanceof OxygenBlock && state.getValue(OxygenBlock.BREATHABLE);
    }

    /*@SubscribeEvent
    public static void onFluidUpdate(BlockEvent.NeighborNotifyEvent neighborNotifyEvent){
        Level level = (Level) neighborNotifyEvent.getLevel();
        BlockPos pos = neighborNotifyEvent.getPos();
        if (!level.isClientSide()) {
            if (!DimensionInit.hasO2Atmosphere(level.dimension())) {
                FluidState fluidState = level.getFluidState(pos);
                if (!fluidState.isEmpty()) {
                    if (TagsInit.CustomFluidTags.DISSIPATE_IN_SPACE.matches(fluidState)) {
                        boolean needToDissipate = true;
                        for (Direction direction : neighborNotifyEvent.getNotifiedSides()) {
                            if (level.isStateAtPosition(pos.relative(direction), CSEventHandler::isStateBreathable)) {
                                needToDissipate = false;
                                break;
                            }
                        }
                        if (needToDissipate) {
                            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }
    }
   */
}
