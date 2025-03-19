package com.rae.creatingspace.legacy.saved;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerUnlockedDesignProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerUnlockedDesign>  PLAYER_UNLOCKED_DESIGN = CapabilityManager.get(
            new CapabilityToken<PlayerUnlockedDesign>() {
            }
    );
    private PlayerUnlockedDesign unlockedDesign = null;
    private final LazyOptional<PlayerUnlockedDesign> optional = LazyOptional.of(this::createUnlockedDesign);

    private PlayerUnlockedDesign createUnlockedDesign() {
        if (unlockedDesign == null){
            unlockedDesign = new PlayerUnlockedDesign();
        }
        return unlockedDesign;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_UNLOCKED_DESIGN){
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createUnlockedDesign().save(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createUnlockedDesign().load(nbt);
    }

}
