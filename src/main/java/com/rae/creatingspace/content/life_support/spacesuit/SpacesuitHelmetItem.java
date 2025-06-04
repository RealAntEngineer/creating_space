package com.rae.creatingspace.content.life_support.spacesuit;

import com.simibubi.create.foundation.advancement.AllAdvancements;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


import java.util.List;

@EventBusSubscriber
public class SpacesuitHelmetItem extends UpgradableEquipment {
	public static final Type TYPE = Type.HELMET;

	public SpacesuitHelmetItem(Holder<ArmorMaterial> material, Properties properties, ResourceLocation textureLoc) {
		super(material, TYPE, properties, textureLoc);
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		if (enchantment.is(Enchantments.AQUA_AFFINITY))
			return false;
		return super.supportsEnchantment(stack, enchantment);
	}

	@Override
	public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
		if (enchantment.is(Enchantments.AQUA_AFFINITY))
			return 1;
		return super.getEnchantmentLevel(stack, enchantment);
	}


	@Override
	public ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
		ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(super.getAllEnchantments(stack, lookup));
		enchants.set(lookup.getOrThrow(Enchantments.AQUA_AFFINITY), 1);
		return enchants.toImmutable();
	}

	public static boolean isWornBy(Entity entity) {
		return !getWornItem(entity).isEmpty();
	}

	public static ItemStack getWornItem(Entity entity) {
		if (!(entity instanceof LivingEntity livingEntity)) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = livingEntity.getItemBySlot(TYPE.getSlot());
		if (!(stack.getItem() instanceof SpacesuitHelmetItem)) {
			return ItemStack.EMPTY;
		}
		return stack;
	}

	@SubscribeEvent
	public static void breatheUnderwater(EntityTickEvent.Pre event) {
		if (event.getEntity() instanceof LivingEntity entity) {

			Level world = entity.level();
			boolean second = world.getGameTime() % 20 == 0;
			boolean drowning = entity.getAirSupply() == 0;

			if (world.isClientSide)
				entity.getPersistentData()
						.remove("VisualBacktankAir");

			ItemStack helmet = getWornItem(entity);
			if (helmet.isEmpty())
				return;

			boolean lavaDiving = entity.isInLava();
			if (!helmet
					.has(DataComponents.FIRE_RESISTANT) && lavaDiving)
				return;

			if (!entity.canDrownInFluidType(entity.getEyeInFluidType()) && !lavaDiving)
				return;
			if (entity instanceof Player && ((Player) entity).isCreative())
				return;

			List<ItemStack> O2Backtanks = OxygenBacktankUtil.getAllWithOxygen(entity);
			if (O2Backtanks.isEmpty())
				return;

			if (lavaDiving) {
				if (entity instanceof ServerPlayer sp)
					AllAdvancements.DIVING_SUIT_LAVA.awardTo(sp);
				if (O2Backtanks.stream()
						.noneMatch(backtank -> backtank.has(DataComponents.FIRE_RESISTANT)))
					return;
			}

			if (drowning)
				entity.setAirSupply(10);

			if (world.isClientSide)
				entity.getPersistentData()
						.putInt("VisualBacktankAir", Math.round(O2Backtanks.stream()
								.map(OxygenBacktankUtil::getOxygen)
								.reduce(0, Integer::sum)));

			if (!second)
				return;

			OxygenBacktankUtil.consumeOxygen(entity, O2Backtanks.get(0), 1);

			if (lavaDiving)
				return;

			if (entity instanceof ServerPlayer sp)
				AllAdvancements.DIVING_SUIT.awardTo(sp);

			entity.setAirSupply(Math.min(entity.getMaxAirSupply(), entity.getAirSupply() + 10));
			entity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 30, 0, true, false, true));
		}
	}
}
