package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    public enum CustomItemTags {
        OXYGEN_SOURCES(CustomNameSpace.FORGE);

        public final TagKey<Item> tag;
        public final boolean alwaysDatagen;

        CustomItemTags() {
            this(CustomNameSpace.MOD);
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

        private static void init() {}

    }
    public static void init() {
        CustomItemTags.init();
    }
}
