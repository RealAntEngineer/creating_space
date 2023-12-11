package com.rae.creatingspace.server.blockentities;

import com.rae.creatingspace.utilities.CSUtil;
import com.rae.creatingspace.utilities.data.FlightDataHelper;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Locale;

public class FlightRecorderBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

    FlightDataHelper.RocketAssemblyData lastAssemblyData;
    public FlightRecorderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type,pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
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

    //oxygen


    public void setLastAssemblyData(FlightDataHelper.RocketAssemblyData lastAssemblyData) {
        this.lastAssemblyData = lastAssemblyData;
    }

    public void tick(Level level, BlockPos pos, BlockState state, FlightRecorderBlockEntity blockEntity) {
        super.tick();
        if (!level.isClientSide()) {
            if (syncCooldown > 0) {
                syncCooldown--;
                if (syncCooldown == 0 && queuedSync)
                    sendData();
            }
        }
    }
    @Override
    protected void read(CompoundTag nbt, boolean clientPacket) {
        lastAssemblyData = FlightDataHelper.RocketAssemblyData.fromNBT(nbt.getCompound("lastAssemblyData"));
        super.read(nbt, clientPacket);

    }

    @Override
    protected void write(CompoundTag nbt, boolean clientPacket) {
        nbt.put("lastAssemblyData",FlightDataHelper.RocketAssemblyData.toNBT(lastAssemblyData));
        super.write(nbt, clientPacket);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        String tradKey = "creatingspace.overlay.flight_recorder.";
        Lang.builder()
                .add(Component.translatable(tradKey+"title"))
                .forGoggles(tooltip, 1);

        if (lastAssemblyData!=null) {
            if (lastAssemblyData.hasFailed()) {
                if (lastAssemblyData.propellantStatusData().status().isFailReason) {
                    Lang.builder()
                            .add(Component.translatable(tradKey+"propellant_status."+
                                            String.valueOf(
                                                    lastAssemblyData.propellantStatusData()
                                                            .status()).toLowerCase(Locale.ROOT))

                            )
                            .forGoggles(tooltip, 1);
                    for (TagKey<Fluid> fluidTagKey : lastAssemblyData.propellantStatusData().consumedMassForEachPropellant().keySet()) {
                        Integer consumedMass = lastAssemblyData.propellantStatusData().consumedMassForEachPropellant().get(fluidTagKey);
                        Integer fluidMass = lastAssemblyData.propellantStatusData().massForEachPropellant().get(fluidTagKey);

                        if (fluidMass == null) {
                            fluidMass = 0;
                        }

                        Lang.builder()
                                .add(
                                        Component.translatable("fluid."+fluidTagKey.location().toLanguageKey())
                                                .append(" ")
                                                .append(Component.literal(CSUtil.scientificNbrFormatting((float)fluidMass/1000,5))
                                                        .append(Component.translatable(  "creatingspace.science.unit.metric_ton"))
                                                        .withStyle(consumedMass>=fluidMass?
                                                                ChatFormatting.DARK_RED:
                                                                ChatFormatting.DARK_GREEN))
                                                .append(Component.literal(" / " +
                                                                CSUtil.scientificNbrFormatting((float)consumedMass/1000,5))
                                                        .append(Component.translatable(  "creatingspace.science.unit.metric_ton"))
                                                        .withStyle(ChatFormatting.GOLD))
                                )
                                .forGoggles(tooltip, 2);
                    }
                }
                if (lastAssemblyData.thrust()<lastAssemblyData.weight()){
                    Lang.builder()
                            .add(Component.translatable(tradKey+"not_enough_thrust"))
                            .forGoggles(tooltip, 1);
                    Lang.builder()
                            .add(Component.translatable("creatingspace.overlay.flight_recorder.thrust1"))
                            .add(Component.literal(" : "+ CSUtil.scientificNbrFormatting(lastAssemblyData.thrust(),3))
                                    .append(Component.translatable("creatingspace.science.unit.newton")))
                            .forGoggles(tooltip,2);
                    Lang.builder()
                            .add(Component.translatable("creatingspace.overlay.flight_recorder.thrust2"))
                            .add(Component.literal(" : "+CSUtil.scientificNbrFormatting(lastAssemblyData.weight(),3))
                                    .append(Component.translatable("creatingspace.science.unit.newton")))
                            .forGoggles(tooltip,2);


                }

            }
            else {
                Lang.builder()
                        .add(Component.translatable(tradKey+"no_failure"))
                        .forGoggles(tooltip, 1);
            }
        } else {
            Lang.builder()
                    .add(Component.translatable(tradKey+"no_flight"))
                    .forGoggles(tooltip, 1);
        }
        return true;
    }
}
