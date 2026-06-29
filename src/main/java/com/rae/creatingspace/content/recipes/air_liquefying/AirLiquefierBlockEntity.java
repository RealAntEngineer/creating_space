package com.rae.creatingspace.content.recipes.air_liquefying;

import com.rae.creatingspace.init.RecipeInit;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AirLiquefierBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {
    private static final int     SYNC_RATE = 8;
    protected Recipe<?> currentRecipe;
    protected            int     syncCooldown;
    protected            boolean queuedSync;
    protected LazyOptional<IFluidHandler> fluidCapability;
    protected SmartFluidTankBehaviour     outputTank;
    private   int       processingTicks;
    private   Object    airLiquefyingRecipesKey;
    private   boolean                     contentsChanged;
    public AirLiquefierBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            Direction localDir = this.getBlockState().getValue(AirLiquefierBlock.FACING);

            // Check if the side is either the back, top, or bottom
            if (side != localDir && !BlockInit.AIR_LIQUEFIER.get().hasShaftTowards(level, worldPosition, getBlockState(), side)) {
                return this.fluidCapability.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        super.tick();
        assert level != null;
        if (!level.isClientSide()) {
            if (syncCooldown > 0) {
                syncCooldown--;
                if (syncCooldown == 0 && queuedSync)
                    sendData();
            }
        }
        float speed = Math.abs(getSpeed());
        if ((!level.isClientSide || isVirtual())) {
            if (processingTicks < 0) {
                float recipeSpeed = 1;
                if (currentRecipe instanceof ProcessingRecipe) {
                    int t = ((ProcessingRecipe<?>) currentRecipe).getProcessingDuration();
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

    @Override
    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        super.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
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

    protected <C extends Container> boolean matchRecipe(Recipe<C> recipe) {
        if (recipe == null)
            return false;
        return AirLiquefyingRecipe.match(this, recipe);
    }

    public void continueWithPreviousRecipe() {
    }

    public void notifyChangeOfContents() {
        contentsChanged = true;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (isSpeedRequirementFulfilled()) {
            if (getSpeed() != 0) {
                if (level != null && !level.isClientSide) {
                    List<Recipe<?>> recipes = getMatchingRecipes();
                    if (!recipes.isEmpty()) {
                        currentRecipe = recipes.get(0);
                        sendData();
                    }
                }
            }
        }
    }

    protected List<Recipe<?>> getMatchingRecipes() {

        List<Recipe<?>> list = RecipeFinder.get(getRecipeCacheKey(), level, this::matchStaticFilters);
        return list.stream()
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

    protected <C extends Container> boolean matchStaticFilters(Recipe<C> r) {
        return r.getType() == RecipeInit.AIR_LIQUEFYING.getType();
    }

    @Override
    protected void write(CompoundTag nbt, boolean clientPacket) {
        //nbt.putInt("oxygenAmount",OXYGEN_TANK.getFluidAmount());
        outputTank.write(nbt, clientPacket);
        super.write(nbt, clientPacket);
    }

    @Override
    protected void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        outputTank.read(nbt, clientPacket);
        //OXYGEN_TANK.setFluid(new FluidStack(FluidInit.LIQUID_OXYGEN.get(), nbt.getInt("oxygenAmount")));

    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 2, 1000, true)
                .whenFluidUpdates(() -> contentsChanged = true)
                .forbidInsertion();
        behaviours.add(outputTank);

        fluidCapability = LazyOptional.of(() -> {
            LazyOptional<? extends IFluidHandler> outputCap = outputTank.getCapability();
            return new CombinedTankWrapper(outputCap.orElse(null));
        });
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");

        boolean added = false;
        for (int i = 0; i < fluidCapability.orElse(new FluidTank(0)).getTanks(); i++) {
            FluidStack fluidStack = fluidCapability.orElse(new FluidTank(0)).getFluidInTank(i);
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
        if (fluidCapability.orElse(new FluidTank(0)).getTanks() == 0)
            added = true;
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    public boolean acceptOutputs(List<FluidStack> outputFluids, boolean simulate) {
        outputTank.allowInsertion();
        boolean acceptOutputsInner = acceptOutputsInner(outputFluids, simulate);
        outputTank.forbidInsertion();
        return acceptOutputsInner;
    }

    private boolean acceptOutputsInner(List<FluidStack> outputFluids, boolean simulate) {

        for (FluidStack fluid : outputFluids) {
            float amount = fluidCapability.orElse(new FluidTank(0))
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
