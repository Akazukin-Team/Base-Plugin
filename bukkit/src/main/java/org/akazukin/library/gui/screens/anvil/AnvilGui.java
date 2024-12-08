package org.akazukin.library.gui.screens.anvil;

import lombok.Getter;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import org.akazukin.library.gui.screens.chest.ContainerGuiBase;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class AnvilGui extends ContainerGuiBase {
    private final Integer cost;
    @Getter
    protected String name;

    public AnvilGui(final String title, final Player player) {
        this(title, player, null);
    }

    public AnvilGui(final String title, final Player player, final GuiBase prevGui) {
        this(title, player, prevGui, false, null);
    }

    public AnvilGui(final String title, final Player player, final GuiBase prevGui, final boolean canPickup,
                    final Integer cost) {
        super(title, player, canPickup, prevGui);
        this.cost = cost;
        this.name = null;
    }

    @Override
    protected Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this.player, InventoryType.ANVIL, this.title);

        final WrappedAnvilInventory anvil = LibraryPlugin.COMPAT.getWrappedAnvil(inv);
        if (anvil != null) {
            this.name = anvil.getRenameText();
            anvil.setMaximumRepairCost(this.cost);
            inv = LibraryPlugin.COMPAT.getBukkitAnvil(anvil);
        }
        System.out.println("getInventory - anvil is null: " + (anvil == null));

        return inv;
    }

    @Override
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(this.title)) {
            return;
        }

        final WrappedAnvilInventory anvil = LibraryPlugin.COMPAT.getWrappedAnvil(event.getInventory());
        if (anvil != null) {
            this.name = anvil.getRenameText();
        }
        System.out.println("onInventoryClick - anvil is null: " + (anvil == null));

        super.onInventoryClick(event);
    }

    @Override
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (!event.getView().getTitle().equals(this.title)) {
            return;
        }

        final WrappedAnvilInventory anvil = LibraryPlugin.COMPAT.getWrappedAnvil(event.getInventory());
        if (anvil != null) {
            this.name = anvil.getRenameText();
        }
        System.out.println("onInventoryOpen - anvil is null: " + (anvil == null));

        super.onInventoryOpen(event);
    }

    @Override
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(this.title)) {
            return;
        }

        final WrappedAnvilInventory anvil = LibraryPlugin.COMPAT.getWrappedAnvil(event.getInventory());
        if (anvil != null) {
            this.name = anvil.getRenameText();
        }
        System.out.println("onInventoryClose - anvil is null: " + (anvil == null));

        super.onInventoryClose(event);
    }
}
