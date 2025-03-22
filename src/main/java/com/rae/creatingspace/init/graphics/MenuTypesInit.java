package com.rae.creatingspace.init.graphics;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.engine.table.EngineerTableMenu;
import com.rae.creatingspace.content.rocket.RocketMenu;
import com.rae.creatingspace.content.life_support.spacesuit.UpgradableEquipmentMenu;
import com.rae.creatingspace.content.rocket.engine.table.EngineerTableScreen;
import com.rae.creatingspace.content.rocket.ScheduleMakingScreen;
import com.rae.creatingspace.content.life_support.spacesuit.UpgradableEquipementScreen;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MenuTypesInit {
    public static final MenuEntry<EngineerTableMenu> ENGINEER_TABLE = register("engineer_table", EngineerTableMenu::new, () -> EngineerTableScreen::new);
    public static final MenuEntry<RocketMenu> ROCKET_MENU = register("rocket_menu", RocketMenu::new, () -> ScheduleMakingScreen::new);
    public static final MenuEntry<UpgradableEquipmentMenu> UPGRADABLE_EQUIPMENT = register("upgradable_equipment", UpgradableEquipmentMenu::new, () -> UpgradableEquipementScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return CreatingSpace.REGISTRATE
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void register() {
    }

}
