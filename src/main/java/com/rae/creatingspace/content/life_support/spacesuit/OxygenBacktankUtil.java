package com.rae.creatingspace.content.life_support.spacesuit;

import com.rae.creatingspace.init.TagsInit;
import com.simibubi.create.AllEnchantments;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OxygenBacktankUtil {

	//mostly copied from BacktankUtil
	private static final List<Function<LivingEntity, List<ItemStack>>> OXYGEN_BACKTANK_SUPPLIERS = new ArrayList<>();
	
	static {
		addOxygenBacktankSupplier(entity -> {
			List<ItemStack> stacks = new ArrayList<>();
			for (ItemStack itemStack : entity.getArmorSlots())
				if (TagsInit.CustomItemTags.OXYGEN_SOURCES.matches(itemStack)) {
					stacks.add(itemStack);
				}

			return stacks;
		});
	}

	public static List<ItemStack> getAllWithOxygen(LivingEntity entity) {
		List<ItemStack> all = new ArrayList<>();

		for (Function<LivingEntity, List<ItemStack>> supplier : OXYGEN_BACKTANK_SUPPLIERS) {
			List<ItemStack> result = supplier.apply(entity);

			for (ItemStack stack : result)
				if (hasOxygenRemaining(stack))
					all.add(stack);
		}

		// Sort with ascending order (we want to prioritize the most empty so things actually run out)
		all.sort((a, b) -> Float.compare(getOxygen(a), getOxygen(b)));

		return all;
	}

	public static boolean hasOxygenRemaining(ItemStack backtank) {
		return getOxygen(backtank) > 0;
	}

	public static float getOxygen(ItemStack backtank) {
		CompoundTag tag = backtank.getOrCreateTag();
		return Math.min(tag.getFloat("Oxygen"), maxOxygen(backtank));
	}

	public static void consumeOxygen(LivingEntity entity, ItemStack backtank, int i) {
		CompoundTag tag = backtank.getOrCreateTag();
		int maxOxygen = maxOxygen(backtank);
		float oxygen = getOxygen(backtank);
		float newOxygen = Math.max(oxygen - i, 0);
		tag.putFloat("Oxygen", Math.min(newOxygen, maxOxygen));
		tag.putBoolean("toUpdate",true);
		backtank.setTag(tag);

		if (!(entity instanceof ServerPlayer player))
			return;
		
		sendWarning(player, oxygen, newOxygen, maxOxygen / 10f);
		sendWarning(player, oxygen, newOxygen, 1);
	}

	private static void sendWarning(ServerPlayer player, float oxygen, float newOxygen, float threshold) {
		if (newOxygen > threshold)
			return;
		if (oxygen <= threshold)
			return;

		boolean depleted = threshold == 1;
		MutableComponent component = Lang.translateDirect(depleted ? "oxygenbacktank.depleted" : "backtank.low");

		AllSoundEvents.DENY.play(player.level(), null, player.blockPosition(), 1, 1.25f);
		AllSoundEvents.STEAM.play(player.level(), null, player.blockPosition(), .5f, .5f);

		player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 40, 10));
		player.connection.send(new ClientboundSetSubtitleTextPacket(
			Components.literal("\u26A0 ").withStyle(depleted ? ChatFormatting.RED : ChatFormatting.GOLD)
				.append(component.withStyle(ChatFormatting.GRAY))));
		player.connection.send(new ClientboundSetTitleTextPacket(Components.immutableEmpty()));
	}

	public static int maxOxygen(ItemStack backtank) {
		return maxOxygen(backtank.getEnchantmentLevel(AllEnchantments.CAPACITY.get()));
	}

	public static int maxOxygen(int enchantLevel) {
		return AllConfigs.server().equipment.airInBacktank.get()
			+ AllConfigs.server().equipment.enchantedBacktankCapacity.get() * enchantLevel;
	}

	public static int maxOxygenWithoutEnchants() {
		return AllConfigs.server().equipment.airInBacktank.get();
	}


	/**
	 * Use this method to add custom entry points to the backtank item stack supplier, e.g. getting them from custom
	 * slots or items.
	 */
	public static void addOxygenBacktankSupplier(Function<LivingEntity, List<ItemStack>> supplier) {
		OXYGEN_BACKTANK_SUPPLIERS.add(supplier);
	}
}
