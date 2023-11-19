package com.rae.creatingspace.server.blockentities;

import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import com.rae.creatingspace.server.blocks.AirLiquefierBlock;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

import static java.lang.Math.abs;

public class AirLiquefierBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

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

    float residualFloatO2Amount = 0;

    //oxygen
    private final LazyOptional<IFluidHandler> oxygenFluidOptional = LazyOptional.of(()-> this.OXYGEN_TANK);
    private final FluidTank OXYGEN_TANK = new FluidTank(4000){
        @Override
        protected void onContentsChanged() {
            sendData();
            super.onContentsChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return TagsInit.CustomFluidTags.LIQUID_OXYGEN.matches(stack.getFluid());
        }
    };

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            Direction localDir = this.getBlockState().getValue(AirLiquefierBlock.FACING);

            if (side ==  localDir.getOpposite()){
                return this.oxygenFluidOptional.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    public void tick(Level level, BlockPos pos, BlockState state, AirLiquefierBlockEntity blockEntity) {
        super.tick();
        if (!level.isClientSide()) {
            if (syncCooldown > 0) {
                syncCooldown--;
                if (syncCooldown == 0 && queuedSync)
                    sendData();
            }
            //setChanged();
            if (hasRecipe(blockEntity)) {
                float rot_speed = this.getSpeed();
                float O2Amount = (oxygenProduction(rot_speed) /FluidInit.LIQUID_OXYGEN.getType().getDensity()*1000f);

                residualFloatO2Amount += O2Amount - (int) O2Amount;

                blockEntity.OXYGEN_TANK.fill(new FluidStack(FluidInit.LIQUID_OXYGEN.get(),
                        (int) O2Amount + (int) residualFloatO2Amount), IFluidHandler.FluidAction.EXECUTE);
                residualFloatO2Amount -= (int) residualFloatO2Amount;
            }
        }
    }
    private boolean hasRecipe(AirLiquefierBlockEntity blockEntity) {
        float rot_speed = this.getSpeed();
        boolean isRunning = !blockEntity.isOverStressed();
        boolean enoughSpaceInOTank = blockEntity.OXYGEN_TANK.getSpace()>oxygenProduction(rot_speed)/FluidInit.LIQUID_OXYGEN.getType().getDensity()*1000f;
        boolean isInO2 = DimensionInit.hasO2Atmosphere(blockEntity.level.dimension());
        return enoughSpaceInOTank && isRunning && isInO2;
    }

    @Override
    protected void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        OXYGEN_TANK.setFluid(new FluidStack(FluidInit.LIQUID_OXYGEN.get(), nbt.getInt("oxygenAmount")));

    }

    @Override
    protected void write(CompoundTag nbt, boolean clientPacket) {
        nbt.putInt("oxygenAmount",OXYGEN_TANK.getFluidAmount());

        super.write(nbt, clientPacket);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = Lang.translate("generic.unit.millibuckets");
        LangBuilder mbs = Lang.translate("generic.unit.fluidflow");
        Lang.translate("gui.goggles.fluid_container")
                .forGoggles(tooltip);

            FluidTank tank = OXYGEN_TANK;
            String fluidName = FluidInit.LIQUID_OXYGEN.getType().getDescriptionId();

            FluidStack fluidStack = tank.getFluidInTank(0);

            Lang.builder().add(Component.translatable(fluidName))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);

            Lang.builder()
                    .add(Lang.number(fluidStack.getAmount())
                            .add(mb)
                            .style(ChatFormatting.GOLD))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(Lang.number(tank.getTankCapacity(0))
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    private float oxygenProduction(float speed){
        return (abs(speed));
    }


}
