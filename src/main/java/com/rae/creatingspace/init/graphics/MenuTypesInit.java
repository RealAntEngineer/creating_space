package com.rae.creatingspace.init.graphics;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.client.gui.menu.EngineerTableMenu;
import com.rae.creatingspace.client.gui.menu.RocketMenu;
import com.rae.creatingspace.client.gui.menu.UpgradableEquipmentMenu;
import com.rae.creatingspace.client.gui.screen.EngineerTableScreen;
import com.rae.creatingspace.client.gui.screen.NewDestinationScreen;
import com.rae.creatingspace.client.gui.screen.UpgradableEquipementScreen;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MenuTypesInit {
    public static final MenuEntry<EngineerTableMenu> ENGINEER_TABLE = register("engineer_table", EngineerTableMenu::new, () -> EngineerTableScreen::new);
    public static final MenuEntry<RocketMenu> ROCKET_MENU = register("rocket_menu", RocketMenu::new, () -> NewDestinationScreen::new);
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
