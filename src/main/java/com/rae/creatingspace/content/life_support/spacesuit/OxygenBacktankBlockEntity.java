package com.rae.creatingspace.content.life_support.spacesuit;

import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.ingameobject.FluidInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OxygenBacktankBlockEntity extends SmartBlockEntity implements Nameable {

	public int oxygenLevel;
	public int oxygenLevelTimer;
	private int prevOxygenLevel;
	private final Component defaultName;
	private Component customName;
	private int capacityEnchantLevel;
	private ListTag enchantmentTag;

	private final FluidTank OXYGEN_TANK = new FluidTank(1000){
		@Override
		protected void onContentsChanged() {

			super.onContentsChanged();
		}

		@Override
		public boolean isFluidValid(FluidStack stack) {
			return TagsInit.CustomFluidTags.LIQUID_OXYGEN.matches(stack.getFluid());
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			//make filling harder the more you go
			return super.fill(resource, action);
		}
	};
	public LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(()-> this.OXYGEN_TANK);


	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			if (side == Direction.DOWN ){
				return this.fluidOptional.cast();
			}
		}
		return super.getCapability(cap, side);
	}
	//replace O2 level by a tank

	public OxygenBacktankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		defaultName = getDefaultName();
		enchantmentTag = new ListTag();
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	public static Component getDefaultName() {


		return ItemInit.COPPER_OXYGEN_BACKTANK.get().getDescription();
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide()) {
			prevOxygenLevel = oxygenLevel;
			oxygenLevel = OXYGEN_TANK.getFluidAmount();
			setChanged();
			sendData();
		}
		BlockState state = getBlockState();

		int prevComparatorLevel = getComparatorOutput();
		if (getComparatorOutput() != prevComparatorLevel && !level.isClientSide)
			level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
		if (OXYGEN_TANK.getSpace() == 0)
			sendData();
	}

	public int getComparatorOutput() {
		int max = OxygenBacktankUtil.maxOxygen(capacityEnchantLevel);
		return ComparatorUtil.fractionToRedstoneLevel(oxygenLevel / (float) max);
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putInt("Oxygen", oxygenLevel);
		compound.putInt("prevOxygen",prevOxygenLevel);
		compound.putInt("Timer", oxygenLevelTimer);
		compound.putInt("CapacityEnchantment", capacityEnchantLevel);
		if (this.customName != null)
			compound.putString("CustomName", Component.Serializer.toJson(this.customName));
		compound.put("Enchantments", enchantmentTag);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		int prev = oxygenLevel;
		capacityEnchantLevel = compound.getInt("CapacityEnchantment");
		OXYGEN_TANK.setCapacity(OxygenBacktankUtil.maxOxygen(capacityEnchantLevel));
		oxygenLevel = compound.getInt("Oxygen");
		prevOxygenLevel = compound.getInt("prevOxygen");
		oxygenLevelTimer = compound.getInt("Timer");
		enchantmentTag = compound.getList("Enchantments", Tag.TAG_COMPOUND);
		if (compound.contains("CustomName", 8))
			this.customName = Component.Serializer.fromJson(compound.getString("CustomName"));
		if (prev != 0 && prev != oxygenLevel && oxygenLevel == OxygenBacktankUtil.maxOxygen(capacityEnchantLevel) && clientPacket)
			playFilledEffect();
	}

	protected void playFilledEffect() {
		AllSoundEvents.CONFIRM.playAt(level, worldPosition, 0.4f, 1, true);
		Vec3 baseMotion = new Vec3(.25, 0.1, 0);
		Vec3 baseVec = VecHelper.getCenterOf(worldPosition);
		for (int i = 0; i < 360; i += 10) {
			Vec3 m = VecHelper.rotate(baseMotion, i, Axis.Y);
			Vec3 v = baseVec.add(m.normalize()
				.scale(.25f));

			level.addParticle(ParticleTypes.SPIT, v.x, v.y, v.z, m.x, m.y, m.z);
		}
	}

	@Override
	public Component getName() {
		return this.customName != null ? this.customName
			: defaultName;
	}

	public int getOxygenLevel() {
		return oxygenLevel;
	}

	public void setOxygenLevel(int oxygenLevel) {
		this.oxygenLevel = oxygenLevel;
		OXYGEN_TANK.setFluid(new FluidStack(FluidInit.LIQUID_OXYGEN.get(),oxygenLevel));
		setChanged();
		sendData();
	}

	public void setCustomName(Component customName) {
		this.customName = customName;
	}

	public Component getCustomName() {
		return customName;
	}

	public ListTag getEnchantmentTag() {
		return enchantmentTag;
	}

	public void setEnchantmentTag(ListTag enchantmentTag) {
		this.enchantmentTag = enchantmentTag;
	}

	public void setCapacityEnchantLevel(int capacityEnchantLevel) {
		this.capacityEnchantLevel = capacityEnchantLevel;
		this.OXYGEN_TANK.setCapacity(OxygenBacktankUtil.maxOxygen(capacityEnchantLevel));
	}

}
