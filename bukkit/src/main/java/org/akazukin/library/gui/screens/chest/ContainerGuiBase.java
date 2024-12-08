package org.akazukin.library.gui.screens.chest;

import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import org.akazukin.library.event.EventPriority;
import org.akazukin.library.event.EventTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

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

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(this.title)) return;
        if (!this.canPickup) event.setCancelled(true);
        this.onGuiClick(event);
    }

    protected boolean onGuiClick(final InventoryClickEvent event) {
        return false;
    }

    @EventTarget
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (!event.getView().getTitle().equals(this.title)) return;
        this.onGuiOpen(event);
    }

    protected void onGuiOpen(final InventoryOpenEvent event) {
    }

    @EventTarget
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(this.title)) return;
        this.onGuiClose(event);
    }

    protected void onGuiClose(final InventoryCloseEvent event) {
    }
}
