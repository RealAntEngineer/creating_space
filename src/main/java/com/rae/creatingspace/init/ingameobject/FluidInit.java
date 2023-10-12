package com.rae.creatingspace.init.ingameobject;


import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.CreativeModeTabsInit;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class FluidInit {

    private static ResourceLocation customStill(String name){
        return CreatingSpace.resource("fluid/"+ name+"_still");
    }
    private static ResourceLocation customFlowing(String name){
        return CreatingSpace.resource("fluid/"+ name +"_flow");
    }

    private static FluidBuilder<VirtualFluid, CreateRegistrate> registrateCustomVirtualLiquid(String name){
        return CreatingSpace.REGISTRATE.virtualFluid(name,
                customStill(name),
                customFlowing(name),
                CreateRegistrate::defaultFluidType,
                VirtualFluid::new);
    }

    public static final FluidEntry<VirtualFluid> LIQUID_METHANE =
            registrateCustomVirtualLiquid("liquid_methane")
                    .properties(p -> p.viscosity(1000).temperature(90).density(423)
                            .canExtinguish(true))
                    .register();

    public static final ItemEntry<BucketItem> CREATIVE_BUCKET_METHANE =
            CreatingSpace.REGISTRATE.item("liquid_methane_bucket",
                    p-> new BucketItem(LIQUID_METHANE.get(),p))
                    .register();



                          /*fogColor(0.75f,0.21f,0.5f))*/
    public static final FluidEntry<VirtualFluid> LIQUID_OXYGEN =
            registrateCustomVirtualLiquid("liquid_oxygen")
                    .properties(p -> p.viscosity(1000).temperature(90).density(1141))
                    .register();
    public static final ItemEntry<BucketItem> CREATIVE_BUCKET_OXYGEN =
            CreatingSpace.REGISTRATE.item("liquid_oxygen_bucket",
                    p-> new BucketItem(LIQUID_OXYGEN.get(),p))
                    .register();
                    /*fogColor(0.08f,0.55f,0.81f))*/


    public static final FluidEntry<VirtualFluid> LIQUID_HYDROGEN =
            registrateCustomVirtualLiquid("liquid_hydrogen")
                    .properties(p -> p.viscosity(1000).temperature(10).density(70))
                    .register();

    public static final ItemEntry<BucketItem> CREATIVE_BUCKET_HYDROGEN =
            CreatingSpace.REGISTRATE.item("liquid_hydrogen_bucket",
                            p-> new BucketItem(LIQUID_HYDROGEN.get(),p))
                    .register();


    /*fogColor(0.69f,0.34f,0.96f))*/


    public static void register() {}
    public static void registerOpenEndedEffect() {
        OpenEndedPipe.registerEffectHandler(new CryogenicLiquidEffectHandler());
    }
    public static class CryogenicLiquidEffectHandler implements OpenEndedPipe.IEffectHandler {
        @Override
        public boolean canApplyEffects(OpenEndedPipe pipe, FluidStack fluid) {
            return fluid.getFluid().getFluidType().getTemperature() < 100;
        }

        @Override
        public void applyEffects(OpenEndedPipe pipe, FluidStack fluid) {
            Level world = pipe.getWorld();
            if (world.getGameTime() % 5 != 0)
                return;
            List<LivingEntity> entities =
                    world.getEntitiesOfClass(LivingEntity.class, pipe.getAOE(), LivingEntity::isAlive);

            for (LivingEntity entity : entities) {
                entity.setIsInPowderSnow(true);
                if (world.isClientSide) {
                    RandomSource randomsource = world.getRandom();

                    world.addParticle(ParticleTypes.SNOWFLAKE, entity.getX(), (entity.getY() + 1), entity.getZ(), (Mth.randomBetween(randomsource, -1.0F, 1.0F) * 0.083333336F), 0.05F, (Mth.randomBetween(randomsource, -1.0F, 1.0F) * 0.083333336F));
                }
            }
        }
    }

    public static void registerFluidInteractions() {

        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                LIQUID_HYDROGEN.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.OBSIDIAN.defaultBlockState();
                    } else {
                        return Blocks.COBBLESTONE.defaultBlockState();
                    }
                }
        ));
        FluidInteractionRegistry.addInteraction(ForgeMod.WATER_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                LIQUID_HYDROGEN.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.ICE.defaultBlockState();
                    } else {
                        return Blocks.SNOW_BLOCK.defaultBlockState();
                    }
                }
        ));

        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                LIQUID_OXYGEN.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.OBSIDIAN.defaultBlockState();
                    } else {
                        return Blocks.COBBLESTONE.defaultBlockState();
                    }
                }
        ));
        FluidInteractionRegistry.addInteraction(ForgeMod.WATER_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                LIQUID_OXYGEN.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.ICE.defaultBlockState();
                    } else {
                        return Blocks.SNOW_BLOCK.defaultBlockState();
                    }
                }
        ));

        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                LIQUID_METHANE.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.OBSIDIAN.defaultBlockState();
                    } else {
                        return Blocks.COBBLESTONE.defaultBlockState();
                    }
                }
        ));
        FluidInteractionRegistry.addInteraction(ForgeMod.WATER_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                LIQUID_METHANE.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.ICE.defaultBlockState();
                    } else {
                        return Blocks.SNOW_BLOCK.defaultBlockState();
                    }
                }
        ));
    }
}

