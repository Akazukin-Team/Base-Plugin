package org.akazukin.library.gui.screens.chest;

import lombok.Getter;
import lombok.Setter;
import org.akazukin.event.EventTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

@Getter
public abstract class ContainerGuiBase extends GuiBase {
    protected final String title;
    @Setter
    protected boolean canPickup;

    public ContainerGuiBase(@Nonnull final String title, @Nonnull final Player player, final boolean canPickup,
                            final GuiBase prevGui) {
        super(player, prevGui);
        this.title = title;
        this.canPickup = canPickup;
    }

    protected abstract Inventory getInventory();

    @EventTarget(libraryPriority = 3)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(this.title)) {
            return;
        }
        if (!this.canPickup) {
            event.setCancelled(true);
        }
        this.onGuiClick(event);
    }

    protected boolean onGuiClick(final InventoryClickEvent event) {
        return false;
    }

    @EventTarget(libraryPriority = 2)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (!event.getView().getTitle().equals(this.title)) {
            return;
        }
        this.onGuiOpen(event);
    }

    protected void onGuiOpen(final InventoryOpenEvent event) {
    }

    @EventTarget(libraryPriority = 2)
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(this.title)) {
            return;
        }
        this.onGuiClose(event);
    }

    protected void onGuiClose(final InventoryCloseEvent event) {
    }
}
