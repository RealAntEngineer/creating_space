package com.rae.creatingspace.content.recipes.air_liquefying;

import com.rae.creatingspace.init.RecipeInit;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AirLiquefierBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {
    private static final Object    airLiquefyingRecipesKey = new Object();
    protected            Recipe<?> currentRecipe;
    protected IFluidHandler           fluidCapability;
    protected SmartFluidTankBehaviour outputTank;
    private              int       processingTicks;
    public AirLiquefierBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlockEntityInit.AIR_LIQUEFIER.get(),
                AirLiquefierBlockEntity::getFluidInvCapability
        );
    }

    public @Nullable IFluidHandler getFluidInvCapability(@Nullable Direction side) {
        Direction localDir = this.getBlockState().getValue(AirLiquefierBlock.FACING);

        if (side != localDir && !BlockInit.AIR_LIQUEFIER.get().hasShaftTowards(level, worldPosition, getBlockState(), side)) {
            return this.fluidCapability;
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        assert getLevel() != null;

        float speed = Math.abs(getSpeed());
        if ((!getLevel().isClientSide || isVirtual())) {
            if (processingTicks < 0) {
                float recipeSpeed = 1;
                if (currentRecipe instanceof ProcessingRecipe) {
                    int t = ((ProcessingRecipe<?, ?>) currentRecipe).getProcessingDuration();
                    if (t != 0)
                        recipeSpeed = t / 100f;
                }

                processingTicks = Mth.clamp((Mth.log2((int) (512 / speed))) * Mth.ceil(recipeSpeed * 15) + 1, 1, 512);
            } else {
                processingTicks--;
                if (processingTicks == 0) {
                    processingTicks = -1;
                    applyRecipe();
                    sendData();
                }
            }
        }
    }

    protected void applyRecipe() {
        if (currentRecipe == null)
            return;
        if (!AirLiquefyingRecipe.apply(this, currentRecipe))
            return;
        getProcessedRecipeTrigger().ifPresent(this::award);
        // Continue mixing

        if (matchRecipe(currentRecipe)) {
            continueWithPreviousRecipe();
            sendData();
        }
        this.notifyChangeOfContents();
    }

    protected Optional<CreateAdvancement> getProcessedRecipeTrigger() {
        return Optional.of(AllAdvancements.MIXER);
    }

    protected boolean matchRecipe(Recipe<?> recipe) {
        if (recipe == null)
            return false;
        return AirLiquefyingRecipe.match(this, recipe);
    }

    public void continueWithPreviousRecipe() {
    }

    public void notifyChangeOfContents() {
    }

    @Override
    protected void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 2, 1000, true)
                .whenFluidUpdates(() -> {
                })
                .forbidInsertion();
        behaviours.add(outputTank);

        fluidCapability = new CombinedTankWrapper(outputTank.getCapability());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        if (fluidCapability == null)
            fluidCapability = new FluidTank(0);
        boolean added = false;
        for (int i = 0; i < fluidCapability.getTanks(); i++) {
            FluidStack fluidStack = fluidCapability.getFluidInTank(i);
            if (fluidStack.isEmpty())
                continue;
            CreateLang.text("")
                    .add(CreateLang.fluidName(fluidStack)
                            .add(CreateLang.text(" "))
                            .style(ChatFormatting.GRAY)
                            .add(CreateLang.number(fluidStack.getAmount())
                                    .add(mb)
                                    .style(ChatFormatting.BLUE)))
                    .forGoggles(tooltip, 1);
        }
        if (fluidCapability.getTanks() == 0)
            added = true;
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking) || added;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (isSpeedRequirementFulfilled()) {
            if (getSpeed() != 0) {
                if (level != null && !level.isClientSide) {
                    List<Recipe<?>> recipes = getMatchingRecipes();
                    if (!recipes.isEmpty()) {
                        currentRecipe = recipes.getFirst();
                        sendData();
                    }
                }
            }
        }
    }

    protected List<Recipe<?>> getMatchingRecipes() {

        List<RecipeHolder<? extends Recipe<?>>> list = RecipeFinder.get(getRecipeCacheKey(), level, this::matchStaticFilters);
        return list.stream()
                .map(RecipeHolder::value)
                .filter(this::matchRecipe)
                .sorted((r1, r2) -> r2.getIngredients()
                        .size()
                        - r1.getIngredients()
                        .size())
                .collect(Collectors.toList());
    }

    protected Object getRecipeCacheKey() {
        return airLiquefyingRecipesKey;
    }

    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> r) {
        return r.value().getType() == RecipeInit.AIR_LIQUEFYING.getType();
    }

    public boolean acceptOutputs(List<FluidStack> outputFluids, boolean simulate) {
        outputTank.allowInsertion();
        boolean acceptOutputsInner = acceptOutputsInner(outputFluids, simulate);
        outputTank.forbidInsertion();
        return acceptOutputsInner;
    }

    private boolean acceptOutputsInner(List<FluidStack> outputFluids, boolean simulate) {

        for (FluidStack fluid : outputFluids) {
            float amount = outputTank.getCapability()
                    .fill(fluid,
                            simulate ?
                                    IFluidHandler.FluidAction.SIMULATE :
                                    IFluidHandler.FluidAction.EXECUTE);
            if (amount == 0) {
                return false;
            }
        }
        return true;
    }
}
