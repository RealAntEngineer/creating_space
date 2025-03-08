package com.rae.creatingspace.content.rocket.engine.design;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.rae.creatingspace.init.MiscInit.DEFERRED_EXHAUST_PACK_TYPE;
import static com.rae.creatingspace.init.MiscInit.DEFERRED_POWER_PACK_TYPE;

public class DesignBlueprintItem extends Item {
    public DesignBlueprintItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        ResourceLocation registry = ResourceLocation.CODEC.parse(NbtOps.INSTANCE,
                nbt.get("design_type")).result().orElse(null);
        ResourceLocation location = ResourceLocation.CODEC.parse(NbtOps.INSTANCE,
                nbt.get("design")).result().orElse(null);
        if (registry != null) {
            components.add(Component.translatable(registry.toLanguageKey())
                    .append(" : ")
                    .append(Component.translatable(
                    registry.getPath() + "." +
                            location.getNamespace() + "." + location.getPath())));
        }
        super.appendHoverText(itemStack, level, components, flag);
    }

    /*@Override
    public void fillItemCategory(CreativeModeTab modeTab, NonNullList<ItemStack> itemStacks) {
        if (category == modeTab) {
            DEFERRED_POWER_PACK_TYPE.getEntries().forEach((ro) -> {
                ItemStack stack = getDefaultInstance();
                CompoundTag nbt = stack.getOrCreateTag();
                nbt.put("design_type", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE,
                        ro.getKey().registry()).result().orElse(new CompoundTag()));
                nbt.put("design", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE,
                        ro.getId()).result().orElse(new CompoundTag()));
                stack.setTag(nbt);
                itemStacks.add(stack);
            });
            DEFERRED_EXHAUST_PACK_TYPE.getEntries().forEach((ro) -> {
                ItemStack stack = getDefaultInstance();
                CompoundTag nbt = stack.getOrCreateTag();
                nbt.put("design_type", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE,
                        ro.getKey().registry()).result().orElse(new CompoundTag()));
                nbt.put("design", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE,
                        ro.getId()).result().orElse(new CompoundTag()));
                stack.setTag(nbt);
                itemStacks.add(stack);
            });
        }
        super.fillItemCategory(modeTab, itemStacks);
    }*/

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        CompoundTag nbt = stack.getOrCreateTag();
        ResourceLocation registry = ResourceLocation.CODEC.parse(NbtOps.INSTANCE,
                nbt.get("design_type")).result().orElse(null);
        ResourceLocation location = ResourceLocation.CODEC.parse(NbtOps.INSTANCE,
                nbt.get("design")).result().orElse(null);
        if (registry != null) {
                save(stack, player);
                player.displayClientMessage(Component.translatable("item.creatingspace.design_blueprint.message")
                                .append(" ")
                                .append(Component.translatable(
                                registry.getPath() + "." +
                                        location.getNamespace() + "." + location.getPath()))
                                .append(" ")
                                .append(Component.translatable(registry.toLanguageKey())),
                        true);

        }
        return super.use(level, player, interactionHand);
    }

    private static void save(ItemStack stack, Player player) {
        CompoundTag nbt = stack.getOrCreateTag();
        ResourceLocation registry = ResourceLocation.CODEC.parse(NbtOps.INSTANCE,
                nbt.get("design_type")).result().orElse(null);
        ResourceLocation location = ResourceLocation.CODEC.parse(NbtOps.INSTANCE,
                nbt.get("design")).result().orElse(null);
        if (registry.getPath().equals(DEFERRED_EXHAUST_PACK_TYPE.getRegistryName().getPath())) {
            CreatingSpace.DESIGN_SAVED_DATA.addExhaustForPlayer(player, location);

        }
        if (registry.getPath().equals(DEFERRED_POWER_PACK_TYPE.getRegistryName().getPath())) {
            CreatingSpace.DESIGN_SAVED_DATA.addPowerPackForPlayer(player, location);
        }
    }
}
