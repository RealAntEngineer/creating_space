package com.rae.creatingspace.content.life_support.sealer;

import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.ingameobject.EntityInit;
import com.rae.creatingspace.legacy.server.blocks.atmosphere.SealerBlock;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class RoomPressuriserBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {
    public RoomPressuriserBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public FluidTank OXYGEN_TANK = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            notifyUpdate();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return TagsInit.CustomFluidTags.LIQUID_OXYGEN.matches(stack.getFluid());

        }
    };
    public LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> this.OXYGEN_TANK);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            Direction localDir = this.getBlockState().getValue(SealerBlock.FACING);

            if (side == localDir.getOpposite()) {
                return this.fluidOptional.cast();
            }
        }
        return super.getCapability(cap, side);
    }
    public void tryRoom() {
        assert level != null;
        if (!level.isClientSide) {
            boolean initialized = false;
            for (RoomAtmosphere room : level.getEntitiesOfClass(RoomAtmosphere.class,
                    new AABB(getBlockPos().relative(getBlockState()
                            .getValue(RoomPressuriserBlock.FACING))))) {
                if (room != null) {
                    initialized = true;
                    room.regenerateRoom(getBlockPos().relative(getBlockState().getValue(RoomPressuriserBlock.FACING)));
                }
            }
            if (!initialized) {
                RoomAtmosphere room = new RoomAtmosphere(EntityInit.ATMOSPHERE_ENTITY.get(), getLevel());
                room.setPos(Vec3.atCenterOf(getBlockPos().relative(getBlockState().getValue(RoomPressuriserBlock.FACING))));
                if (level != null) {
                    level.addFreshEntity(room);
                    room.regenerateRoom(getBlockPos().relative(getBlockState().getValue(RoomPressuriserBlock.FACING)));
                }
            }
        }
    }

    @Override
    public void remove() {
        if (level != null) {
            for (RoomAtmosphere room : level.getEntitiesOfClass(RoomAtmosphere.class,
                    new AABB(getBlockPos().relative(getBlockState().getValue(RoomPressuriserBlock.FACING))))) {
                if (room != null)
                    room.kill();
            }
        }
        super.remove();
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
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        assert level != null;
        if (!level.isClientSide) {
            if (getSpeed() != 0) {
                List<RoomAtmosphere> rooms = level.getEntitiesOfClass(RoomAtmosphere.class,
                        new AABB(getBlockPos().relative(getBlockState()
                                .getValue(RoomPressuriserBlock.FACING))));
                if (rooms.isEmpty()){
                    tryRoom();
                }
                for (RoomAtmosphere room : rooms) {
                    if (room != null) {
                        room.addO2(OXYGEN_TANK.drain((int)Math.abs(getSpeed()), IFluidHandler.FluidAction.EXECUTE).getAmount() * 10);
                    }
                }
            }
        }
    }
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip,isPlayerSneaking,fluidOptional.cast());
        assert level != null;
        List<RoomAtmosphere> rooms = level.getEntitiesOfClass(RoomAtmosphere.class,
                new AABB(getBlockPos().relative(getBlockState()
                        .getValue(RoomPressuriserBlock.FACING))));
        for (RoomAtmosphere room : rooms) {
            tooltip.add(Component.translatable(room.getShape().isClosed()? "creatingspace.entity.room_atmosphere.sealed":"creatingspace.entity.room_atmosphere.open"));
            tooltip.add(Component.literal(String.format("02 concentration : %.1f mb/m3",room.getO2concentration())));
            //TODO make a server/client sync for the code bellow to work.
            /*
            if (isPlayerSneaking){
                tooltip.add(Component.literal("autonomous production : ").withStyle(ChatFormatting.GOLD));
                for (Map.Entry<ResourceLocation, RoomAtmosphere.AtmosphereFilterData> entry: room.passiveFilters.entrySet()) {
                    tooltip.add(Component.literal("  ").withStyle(ChatFormatting.GRAY)
                                    .append(Component.translatable(entry.getKey().toLanguageKey("block")))
                                    .append(" : ")
                                    .append(String.valueOf(entry.getValue().globalImpact()))
                                    .append(" mb/tick")
                            );

                }
            }*/
        }
        return true;
    }


    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        OXYGEN_TANK.writeToNBT(compound);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        OXYGEN_TANK.readFromNBT(compound);
    }
}