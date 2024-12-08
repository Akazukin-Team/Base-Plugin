package org.akazukin.library.gui.screens.chest;

import lombok.Getter;
import org.akazukin.library.event.EventTarget;
import org.akazukin.library.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

@Getter
public abstract class ChestGuiBase extends ContainerGuiBase {
    protected int rows;

    public ChestGuiBase(final String title, final int rows, final Player player, final boolean canPickup,
                        final GuiBase prevGui) {
        super(title, player, canPickup, prevGui);
        this.rows = rows;
    }

    @Override
    public boolean forceOpen() {
        if (this.player == null) return false;
        final Inventory inv = this.getInventory();
        if (inv == null) return false;
        final InventoryView inv2 = this.player.openInventory(this.getInventory());
        return inv2 != null;
    }

    @Override
    protected Inventory getInventory() {
        return InventoryUtils.createInventory(this.title, this.rows);
    }

    @Override
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(this.title)) return;
        if (!this.canPickup) event.setCancelled(true);

        this.onGuiClick(event);
    }

    @Override
    @EventTarget
    public final void onInventoryClose(final InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(this.title)) return;
        this.onGuiClose(event);
    }
}
