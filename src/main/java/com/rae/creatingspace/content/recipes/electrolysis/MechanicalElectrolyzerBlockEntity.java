package com.rae.creatingspace.content.recipes.electrolysis;

import com.rae.creatingspace.init.RecipeInit;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class MechanicalElectrolyzerBlockEntity extends BasinOperatingBlockEntity {

    private static final Object shapelessOrMixingRecipesKey = new Object();

    public int runningTicks;
    public int processingTicks;
    public boolean running;
    private ItemStack electrode;

    public MechanicalElectrolyzerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public float getRenderedHeadOffset(float partialTicks) {
        int localTick;
        float offset = 0;
        float objective = 16 / 16f;
        if (running) {

            //men, your so dum, like just use a sin/ well no your not
            if (runningTicks < 20) {
                localTick = runningTicks;
                float num = (localTick + partialTicks) / 40f;
                offset = (Mth.sin((float) (num * Math.PI))) * objective; // the ??
            } else if (runningTicks <= 20) {
                offset = objective;
            } else {
                localTick = 40 - runningTicks;
                float num = (localTick - partialTicks) / 40f;
                offset = (Mth.sin((float) (num * Math.PI))) * objective;
            }
        }
        return offset;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        registerAwardables(behaviours, AllAdvancements.MIXER);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).expandTowards(0, -1.5, 0);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        running = compound.getBoolean("Running");
        runningTicks = compound.getInt("Ticks");
        super.read(compound, clientPacket);

        CompoundTag electrode = (CompoundTag) compound.get("electrode");
        if (electrode != null) {
            if (electrode.isEmpty()) {
                this.electrode = null;
            } else {
                this.electrode  = ItemStack.of(electrode);
            }
        }

        if (clientPacket && hasLevel())
            getBasin().ifPresent(bte -> bte.setAreFluidsMoving(running && runningTicks <= 20));
    }

    //TODO solve the saving issue with electrode (same for catalyst)
    // seems like the client packet doesn't receive the right data on read
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("Running", running);
        compound.putInt("Ticks", runningTicks);
        assert level != null;
        if (!level.isClientSide) {
            if (electrode != null) {
                compound.put("electrode", electrode.serializeNBT());
            }
        }
    }

    @Override
    public void writeSafe(CompoundTag tag) {
        super.writeSafe(tag);
        if (electrode != null) {
            tag.put("electrode", electrode.serializeNBT());
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (runningTicks >= 40) {
            running = false;
            runningTicks = 0;
            basinChecker.scheduleUpdate();
            return;
        }

        float speed = Math.abs(getSpeed());
        if (running && level != null) {
            if (level.isClientSide && runningTicks == 20)
                renderVisualEffects();

            if ((!level.isClientSide || isVirtual()) && runningTicks == 20) {
                if (processingTicks < 0) {
                    float recipeSpeed = 1;
                    if (currentRecipe instanceof ProcessingRecipe) {
                        int t = ((ProcessingRecipe<?>) currentRecipe).getProcessingDuration();
                        if (t != 0)
                            recipeSpeed = t / 100f;
                    }

                    processingTicks = Mth.clamp((Mth.log2((int) (512 / speed))) * Mth.ceil(recipeSpeed * 15) + 1, 1, 512);

                    Optional<BasinBlockEntity> basin = getBasin();
                    if (basin.isPresent()) {
                        Couple<SmartFluidTankBehaviour> tanks = basin.get()
                                .getTanks();
                        if (!tanks.getFirst()
                                .isEmpty()
                                || !tanks.getSecond()
                                .isEmpty())
                            level.playSound(null, worldPosition, SoundEvents.LIGHTNING_BOLT_THUNDER,
                                    SoundSource.BLOCKS, .25f, speed < 65 ? .75f : 1.5f);
                    }

                } else {
                    processingTicks--;
                    if (processingTicks == 0) {
                        runningTicks++;
                        processingTicks = -1;
                        applyBasinRecipe();
                        sendData();
                    }
                }
            }

            if (runningTicks != 20)
                runningTicks++;
        }
    }

    public void renderVisualEffects() {
    }

    @Override
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> r) {
        return r.getType() == RecipeInit.MECHANICAL_ELECTROLYSIS.getType();
    }

    @Override
    protected <C extends Container> boolean matchBasinRecipe(Recipe<C> recipe) {
        if (electrode == null) return false;
        return super.matchBasinRecipe(recipe);
    }

    @Override
    public void startProcessingBasin() {
        if (running && runningTicks <= 20)
            return;
        super.startProcessingBasin();

        running = true;
        runningTicks = 0;
    }

    @Override
    protected void applyBasinRecipe() {
        super.applyBasinRecipe();
        if (electrode != null) {
            electrode.setDamageValue(electrode.getDamageValue() + 1);
            System.out.println(electrode.serializeNBT());
        }
    }

    @Override
    public boolean continueWithPreviousRecipe() {
        runningTicks = 20;
        return true;
    }

    @Override
    protected void onBasinRemoved() {
        if (!running)
            return;
        runningTicks = 40;
        running = false;
    }

    @Override
    protected Object getRecipeCacheKey() {
        return shapelessOrMixingRecipesKey;
    }

    @Override
    protected boolean isRunning() {
        return running;
    }

    @Override
    protected Optional<CreateAdvancement> getProcessedRecipeTrigger() {
        return Optional.of(AllAdvancements.MIXER);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        // SoundEvents.BLOCK_STONE_BREAK
        boolean slow = Math.abs(getSpeed()) < 65;
        if (slow && AnimationTickHolder.getTicks() % 2 == 0)
            return;
        //replace with an electric sound
        /*if (runningTicks == 20)
            AllSoundEvents.SANDING_SHORT.playAt(level, worldPosition, .75f, 1, true);
    */
    }

    public ItemStack getElectrode() {
        return electrode;
    }

    public void setElectrode(@Nullable ItemStack held) {
        if (held == null) {
            electrode = null;
            notifyUpdate();
            return;
        }
        electrode = held.copy();
        notifyUpdate();
    }

}
