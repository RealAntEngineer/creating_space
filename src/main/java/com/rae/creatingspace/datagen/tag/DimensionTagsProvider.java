package com.rae.creatingspace.datagen.tag;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class DimensionTagsProvider extends TagsProvider {
    protected DimensionTagsProvider(DataGenerator generator, Registry registry, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, registry, CreatingSpace.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(DimensionInit.IS_ORBIT).add(DimensionInit.EARTH_ORBIT_TYPE,DimensionInit.MOON_ORBIT_TYPE);
    }
}
