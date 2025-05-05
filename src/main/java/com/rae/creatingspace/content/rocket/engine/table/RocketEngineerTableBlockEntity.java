package com.rae.creatingspace.content.rocket.engine.table;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.content.rocket.engine.design.ExhaustPackType;
import com.rae.creatingspace.content.rocket.engine.design.PowerPackType;
import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.init.MiscInit;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.rae.creatingspace.init.MiscInit.getSyncedExhaustPackRegistry;
import static com.rae.creatingspace.init.MiscInit.getSyncedPowerPackRegistry;

public class RocketEngineerTableBlockEntity extends SmartBlockEntity implements MenuProvider {
    public TableInventory inventory;
    public int size = 100;
    public int thrust = 100000;
    public int expansionRatio = 50;
    public ResourceLocation powerPackType;
    public ResourceLocation exhaustPackType;
    public ResourceLocation propellantType;

    @Override
    public Component getDisplayName() {
        return Component.literal("coucou");
    }


    public class TableInventory extends ItemStackHandler {
        public TableInventory() {
            super(10);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return super.getStackInSlot(slot);
        }

    }

    public RocketEngineerTableBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        readScreenData(SyncData.defaultData());
        inventory = new TableInventory();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return EngineerTableMenu.create(id, inv, this);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }


    public static CompoundTag fromIngredients(float size, PropellantType type, float totalEfficiency) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("isp", (int) (type.getMaxISP() * totalEfficiency));
        nbt.putInt("thrust", (int) size);

        Map<TagKey<Fluid>, Float> map = type.getPropellantRatio();
        nbt.putFloat("oxFuelRatio", map.values().stream().toList().get(0) / map.values().stream().toList().get(1));
        nbt.putString("fuelTag", map.keySet().stream().toList().get(1).location().toString());
        nbt.putString("oxidizerTag", map.keySet().stream().toList().get(0).location().toString());
        return nbt;
    }

    //should be replaced by craft blueprint
    public void craftEngine(ItemStack newEngine) {
        System.out.println(newEngine);
        if (inventory.isItemValid(0, newEngine)) {
            inventory.insertItem(0, newEngine, false);
        }
    }

    public CompoundTag saveScreenData() {
        CompoundTag screenInfo = new CompoundTag();
        screenInfo.putInt("thrust", thrust);
        screenInfo.putInt("size", size);
        screenInfo.putInt("expansionRatio", expansionRatio);
        screenInfo.put("exhaustPack", ResourceLocation.CODEC
                .encodeStart(NbtOps.INSTANCE, exhaustPackType)
                .result().orElse(new CompoundTag()));
        screenInfo.put("powerPack", ResourceLocation.CODEC
                .encodeStart(NbtOps.INSTANCE, powerPackType)
                .result().orElse(new CompoundTag()));
        screenInfo.put("propellantType", ResourceLocation.CODEC
                .encodeStart(NbtOps.INSTANCE, propellantType)
                .result().orElse(new CompoundTag()));
        return screenInfo;
    }

    public void readScreenData(CompoundTag screenInfo) {
        thrust = screenInfo.getInt("thrust");
        size = screenInfo.getInt("size");
        expansionRatio = screenInfo.getInt("expansionRatio");
        propellantType = ResourceLocation.CODEC
                .parse(NbtOps.INSTANCE, screenInfo.get("propellantType")).result().orElse(null);
        exhaustPackType = ResourceLocation.CODEC
                .parse(NbtOps.INSTANCE, screenInfo.get("exhaustPack")).result().orElse(null);
        powerPackType = ResourceLocation.CODEC
                .parse(NbtOps.INSTANCE, screenInfo.get("powerPack")).result().orElse(null);

    }

    public void readScreenData(SyncData screenInfo) {
        thrust = screenInfo.thrust;
        size = screenInfo.size;
        expansionRatio = screenInfo.expansionRatio;
        exhaustPackType = screenInfo.exhaustPackType;
        powerPackType = screenInfo.powerPackType;
        propellantType = screenInfo.propellantType;
    }

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        tag.put("inventory", inventory.serializeNBT());
        tag.put("screenInfo", saveScreenData());
        super.writeSafe(tag);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        inventory.deserializeNBT((CompoundTag) tag.get("inventory"));
        //seems to be buggy
        /*if (clientPacket) {
            if (inventory != null) inventory.setSize(
                    getSyncedExhaustPackRegistry().get(exhaustPackType).getSlots().size() +
                            getSyncedPowerPackRegistry().get(powerPackType).getSlots().size() + 1);
        } else {
            if (inventory != null)
                inventory.setSize(getSyncedExhaustPackRegistry().get(exhaustPackType).getSlots().size() +
                        getSyncedPowerPackRegistry().get(powerPackType).getSlots().size() + 1);
        }*/
        CompoundTag screenInfo = (CompoundTag) tag.get("screenInfo");
        assert screenInfo != null;
        readScreenData(screenInfo);
        notifyUpdate();
    }

    @Override
    public String toString() {
        return "RocketEngineerTableBlockEntity{" +
                "size=" + size +
                ", thrust=" + thrust +
                ", expansionRatio=" + expansionRatio +
                ", powerPackType=" + powerPackType +
                ", exhaustPackType=" + exhaustPackType +
                ", propellantType=" + propellantType +
                '}';
    }

    //use for sync packet bwn the screen and the BE (use resource location for ease of use) ->
    // use ResourceLocation to ensure it could be encoded properly
    public record SyncData(int thrust, int size, int expansionRatio, ResourceLocation exhaustPackType,
                           ResourceLocation powerPackType, ResourceLocation propellantType) {
        public static SyncData defaultData() {
            //CompoundTag syncData = new CompoundTag();
            return new SyncData(100000, 100, 50, MiscInit.BELL_NOZZLE.getId()
                    , MiscInit.OPEN_CYCLE.getId(),
                    PropellantTypeInit.METHALOX.getId());

        }
        //register a static RegistryAccessor only on server side ->
        // if it null then the server is distant and we use the sync

        public ExhaustPackType exhaustPackType(boolean client) {
            if (client) {
                return getSyncedExhaustPackRegistry().get(exhaustPackType);
            } else {
                //that won't work on a distant server
                return Minecraft.getInstance().getConnection().registryAccess().registry(MiscInit.Keys.EXHAUST_PACK_TYPE)
                        .orElseThrow().get(exhaustPackType);
            }
        }

        public PowerPackType powerPackType(boolean client) {
            if (client) {
                return getSyncedPowerPackRegistry().get(powerPackType);
            } else {
                //that won't work on a distant server
                return Minecraft.getInstance().getConnection().registryAccess().registry(MiscInit.Keys.POWER_PACK_TYPE)
                        .orElseThrow().get(powerPackType);
            }
        }

        public static Codec<SyncData> getCoded() {
            return RecordCodecBuilder.create(
                    instance ->
                            instance.group(
                                            Codec.INT.fieldOf("thrust").forGetter(i -> i.thrust),
                                            Codec.INT.fieldOf("size").forGetter(i -> i.size),
                                            Codec.INT.fieldOf("expansionRatio").forGetter(i -> i.expansionRatio),
                                            ResourceLocation.CODEC
                                                    .fieldOf("exhaustPack").forGetter(i -> i.exhaustPackType),
                                            ResourceLocation.CODEC
                                                    .fieldOf("powerPack").forGetter(i -> i.powerPackType),
                                            ResourceLocation.CODEC
                                                    .fieldOf("propellantType").forGetter(i -> i.propellantType)
                                    )
                                    .apply(instance, SyncData::new)
            );
        }
    }
}