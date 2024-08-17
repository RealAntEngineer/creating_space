package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.ForgeRegistries;

public class TagsInit extends AllTags {

    public enum CustomNameSpace {

        MOD(CreatingSpace.MODID, false, true),
        FORGE("forge"),
        TIC("tconstruct"),
        QUARK("quark");

        public final String id;
        public final boolean optionalDefault;
        public final boolean alwaysDatagenDefault;

        CustomNameSpace(String id) {
            this(id, true, false);
        }

        CustomNameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
            this.id = id;
            this.optionalDefault = optionalDefault;
            this.alwaysDatagenDefault = alwaysDatagenDefault;
        }
    }
    public enum CustomBlockTags {
    MOON_CARVER_REPLACEABLES(),MOON_STONE_ORE_REPLACEABLES();

    public final TagKey<Block> tag;
    public final boolean alwaysDatagen;

    CustomBlockTags() {
        this(CustomNameSpace.MOD);
    }

    CustomBlockTags(CustomNameSpace namespace) {
        this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
    }

    CustomBlockTags(CustomNameSpace namespace, String path) {
        this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
    }

    CustomBlockTags(CustomNameSpace namespace, boolean optional, boolean alwaysDatagen) {
        this(namespace, null, optional, alwaysDatagen);
    }

    CustomBlockTags(CustomNameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
        ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
        if (optional) {
            tag = optionalTag(ForgeRegistries.BLOCKS, id);
        } else {
            tag = BlockTags.create(id);
        }
        this.alwaysDatagen = alwaysDatagen;
    }

    @SuppressWarnings("deprecation")
    public boolean matches(Block block) {
        return block.builtInRegistryHolder()
                .is(tag);
    }

    public boolean matches(ItemStack stack) {
        return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
    }

    public boolean matches(BlockState state) {
        return state.is(tag);
    }

    private static void init() {
    }
}
    public enum CustomItemTags {
        OXYGEN_SOURCES(),
        SPACESUIT();

        public final TagKey<Item> tag;
        public final boolean alwaysDatagen;

        CustomItemTags() {
            this(CustomNameSpace.MOD);
        }
        CustomItemTags(String path) {
            this(CustomNameSpace.MOD,path);
        }
        CustomItemTags(CustomNameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        CustomItemTags(CustomNameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        CustomItemTags(CustomNameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        CustomItemTags(CustomNameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = optionalTag(ForgeRegistries.ITEMS, id);
            } else {
                tag = ItemTags.create(id);
            }
            this.alwaysDatagen = alwaysDatagen;
        }

        @SuppressWarnings("deprecation")
        public boolean matches(Item item) {
            return item.builtInRegistryHolder()
                    .is(tag);
        }

        public boolean matches(ItemStack stack) {
            return stack.is(tag);
        }

        private static void init() {
        }

    }
    public enum CustomEntityTag {
        SPACE_CREATURES();

        public final TagKey<EntityType<?>> tag;
        public final boolean alwaysDatagen;

        CustomEntityTag() {
            this(CustomNameSpace.MOD);
        }

        CustomEntityTag(CustomNameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        CustomEntityTag(CustomNameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        CustomEntityTag(CustomNameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        CustomEntityTag(CustomNameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = optionalTag(ForgeRegistries.ENTITY_TYPES, id);
            } else {
                tag = TagKey.create(Registries.ENTITY_TYPE, id);
            }
            this.alwaysDatagen = alwaysDatagen;
        }

        public boolean matches(Entity entity) {
            return entity.getType()
                    .is(tag);
        }

        private static void init() {}

    }
    public enum CustomFluidTags {

        LIQUID_METHANE(),
        LIQUID_HYDROGEN(),
        LIQUID_OXYGEN(),
        METALIC_HYDROGEN(),
        DISSIPATE_IN_SPACE(), LIQUID_CO2();

        public final TagKey<Fluid> tag;
        public final boolean alwaysDatagen;

        CustomFluidTags() {
            this(CustomNameSpace.MOD);
        }

        CustomFluidTags(CustomNameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        CustomFluidTags(CustomNameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        CustomFluidTags(CustomNameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        CustomFluidTags(CustomNameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = optionalTag(ForgeRegistries.FLUIDS, id);
            } else {
                tag = FluidTags.create(id);
            }
            this.alwaysDatagen = alwaysDatagen;
        }

        @SuppressWarnings("deprecation")
        public boolean matches(Fluid fluid) {
            return fluid.is(tag);
        }

        public boolean matches(FluidState state) {
            return state.is(tag);
        }

        private static void init() {
        }

    }

    public enum CustomBiomeTags {
        NO_OXYGEN();

        public final TagKey<Biome> tag;
        public final boolean alwaysDatagen;

        CustomBiomeTags() {
            this(CustomNameSpace.MOD);
        }

        CustomBiomeTags(CustomNameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        CustomBiomeTags(CustomNameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        CustomBiomeTags(CustomNameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        CustomBiomeTags(CustomNameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = optionalTag(ForgeRegistries.BIOMES, id);
            } else {
                tag = TagKey.create(Registries.BIOME, id);
            }
            this.alwaysDatagen = alwaysDatagen;
        }

        public boolean matches(Biome biome) {
            return matches(ForgeRegistries.BIOMES.getHolder(biome).orElse(null));
        }

        public boolean matches(ResourceLocation biome) {
            return matches(ForgeRegistries.BIOMES.getHolder(biome).orElse(null));
        }

        public boolean matches(Holder<Biome> biome) {
            if (biome != null) {
                return biome.is(tag);
            }
            return false;
        }

        private static void init() {
        }
    }


    public static void init() {
        CustomBlockTags.init();
        CustomItemTags.init();
        CustomEntityTag.init();
        CustomFluidTags.init();
    }
}
