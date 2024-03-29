package net.akazukin.library.gui;

import lombok.Getter;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.UUID;

@Getter
public abstract class ChestGuiBase extends ContainerGuiBase {
    protected final int rows;

    public ChestGuiBase(final String title, final int rows, final UUID player, final boolean canPickup, final GuiBase prevGui) {
        super(title, player, canPickup, prevGui);
        this.rows = rows;
    }

    @Override
    protected Inventory getInventory() {
        return InventoryUtils.createInventory(title, rows);
    }

    @Override
    public boolean forceOpen() {
        final Player player_ = Bukkit.getPlayer(player);
        if (player_ == null) return false;
        final Inventory inv = getInventory();
        if (inv == null) return false;
        final InventoryView inv2 = player_.openInventory(getInventory());
        return inv2 != null;
    }

    @Override
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(title)) return;
        if (!canPickup) event.setCancelled(true);

        if (InventoryUtils.isCloseItem(event.getCurrentItem())) {
            event.getWhoClicked().closeInventory();
        } else if (InventoryUtils.isPrevItem(event.getCurrentItem())) {
            GuiManager.singleton().setScreen(event.getWhoClicked().getUniqueId(), prevGui);
        } else {
            onGuiClick(event);
        }
    }

    @Override
    @EventTarget
    public final void onInventoryClose(final InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(title)) return;
        onGuiClose(event);
    }
}
