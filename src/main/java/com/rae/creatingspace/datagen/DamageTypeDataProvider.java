package com.rae.creatingspace.datagen;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.DamageSourceInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DamageTypeDataProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, DamageSourceInit::bootstrap);

    public DamageTypeDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(CreatingSpace.MODID));
    }

    public static DataProvider.Factory<DamageTypeDataProvider> makeFactory(CompletableFuture<HolderLookup.Provider> registries) {
        return output -> new DamageTypeDataProvider(output, registries);
    }

    @Override
    @NotNull
    public String getName() {
        return "CreatingSpace's Damage Type Data";
    }
}
