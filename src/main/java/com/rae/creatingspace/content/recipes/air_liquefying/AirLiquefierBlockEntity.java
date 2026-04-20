package com.rae.creatingspace.content.recipes.air_liquefying;

import com.rae.creatingspace.init.RecipeInit;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AirLiquefierBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {
    protected Recipe<?> currentRecipe;
    private int processingTicks;
    private Object airLiquefyingRecipesKey;

    public AirLiquefierBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type,pos, state);
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

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    protected IFluidHandler fluidCapability;
    private boolean contentsChanged;
    protected SmartFluidTankBehaviour outputTank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 2, 1000, true)
                .whenFluidUpdates(() -> contentsChanged = true)
                .forbidInsertion();
        behaviours.add(outputTank);

        fluidCapability = new CombinedTankWrapper( outputTank.getCapability());
    }



    public @Nullable IFluidHandler getFluidInvCapability(@Nullable Direction side) {
            Direction localDir = this.getBlockState().getValue(AirLiquefierBlock.FACING);

            // Check if the side is either the back, top, or bottom
            if (side == localDir.getOpposite() || side == Direction.UP || side == Direction.DOWN) {
                return this.fluidCapability;
        }
        return null;
    }
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlockEntityInit.AIR_LIQUEFIER.get(),
                AirLiquefierBlockEntity::getFluidInvCapability
        );
    }
    @Override
    public void tick() {
        super.tick();
        assert getLevel() != null;
        if (!getLevel().isClientSide()) {
            if (syncCooldown > 0) {
                syncCooldown--;
                if (syncCooldown == 0 && queuedSync)
                    sendData();
            }
        }
        float speed = Math.abs(getSpeed());
        if ((!getLevel().isClientSide || isVirtual())) {
            if (processingTicks < 0) {
                float recipeSpeed = 1;
                if (currentRecipe instanceof ProcessingRecipe) {
                    int t = ((ProcessingRecipe<?,?>) currentRecipe).getProcessingDuration();
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

    protected boolean matchRecipe(Recipe<?> recipe) {
        if (recipe == null)
            return false;
        return AirLiquefyingRecipe.match(this, recipe);
    }

    protected Optional<CreateAdvancement> getProcessedRecipeTrigger() {
        return Optional.of(AllAdvancements.MIXER);
    }

    public void continueWithPreviousRecipe() {
    }

    public void notifyChangeOfContents() {
        contentsChanged = true;
    }

    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> r) {
        return r.value().getType() == RecipeInit.AIR_LIQUEFYING.getType();
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
    @Override
    protected void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt,registries,clientPacket);
        outputTank.read(nbt,registries, clientPacket);
        //OXYGEN_TANK.setFluid(new FluidStack(FluidInit.LIQUID_OXYGEN.get(), nbt.getInt("oxygenAmount")));

    }

    @Override
    protected void write(CompoundTag nbt,HolderLookup.Provider registries, boolean clientPacket) {
        //nbt.putInt("oxygenAmount",OXYGEN_TANK.getFluidAmount());
        outputTank.write(nbt,registries, clientPacket);
        super.write(nbt,registries, clientPacket);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = new LangBuilder("creatingspace").translate("generic.unit.millibuckets");
        LangBuilder mbs = new LangBuilder("creatingspace").translate("generic.unit.fluidflow");
        new LangBuilder("creatingspace").translate("gui.goggles.fluid_container")
                .forGoggles(tooltip);
        IFluidHandler fluids = outputTank.getCapability();
        for (int i = 0; i < fluids.getTanks(); i++) {

            FluidStack fluidStack = fluids.getFluidInTank(i);
            String fluidName = fluidStack.getTranslationKey();

            new LangBuilder("creatingspace").add(Component.translatable(fluidName))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);

            new LangBuilder("creatingspace")
                    .add(CreateLang.number(fluidStack.getAmount())
                            .add(mb)
                            .style(ChatFormatting.GOLD))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(fluids.getTankCapacity(i))
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        }
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
