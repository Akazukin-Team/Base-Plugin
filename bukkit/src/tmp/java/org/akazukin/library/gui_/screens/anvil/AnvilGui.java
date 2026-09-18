package org.akazukin.library.gui_.screens.anvil;

import lombok.Getter;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import org.akazukin.library.gui.IGui;
import org.akazukin.library.gui.OpenStatus;
import org.akazukin.library.gui.screens.IPacketGui;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class AnvilGui implements IPacketGui {
    private final Integer cost;
    @Getter
    protected String name;

    public AnvilGui(final String title, final II18nLang locale) {
        this(title, locale, null);
    }

    public AnvilGui(final String title, final II18nLang locale, final IGui prevGui) {
        this(title, locale, prevGui, false, null);
    }

    public AnvilGui(final String title, final II18nLang locale, final IGui prevGui, final boolean canPickup,
                    final Integer cost) {
        super(title, locale, canPickup, prevGui);
        this.cost = cost;
        this.name = null;
    }

    @Override
    protected Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this.player, InventoryType.ANVIL, this.title);
        final WrappedAnvilInventory anvil = LibraryPlugin.getPlugin().getCompat().getWrappedAnvil(inventory);
        if (anvil != null) {
            this.name = anvil.getRenameText();
            anvil.setMaximumRepairCost(this.cost);
            inventory = LibraryPlugin.getPlugin().getCompat().getBukkitAnvil(anvil);
        }
        return inventory;
    }

    @Override
    public OpenStatus open(final UUID player) {
        throw new UnsupportedOperationException("AnvilGui does not support opening");
    }
}


