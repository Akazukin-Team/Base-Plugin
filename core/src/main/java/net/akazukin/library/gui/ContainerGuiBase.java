package net.akazukin.library.gui;

import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.event.EventTarget;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

@Getter
public abstract class ContainerGuiBase extends GuiBase {
    protected final String title;
    @Setter
    protected boolean canPickup;

    public ContainerGuiBase(final String title, final UUID player, final boolean canPickup, final GuiBase prevGui) {
        super(player, prevGui);
        this.title = title;
        this.canPickup = canPickup;
    }

    protected abstract Inventory getInventory();

    @EventTarget
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(title)) return;
        if (!canPickup) event.setCancelled(true);
        onGuiClick(event);
    }

    protected void onGuiClick(final InventoryClickEvent event) {
    }

    @EventTarget
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (!event.getView().getTitle().equals(title)) return;
        onGuiOpen(event);
    }

    @EventTarget
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(title)) return;
        onGuiClose(event);
    }

    protected void onGuiOpen(final InventoryOpenEvent event) {
    }

    protected void onGuiClose(final InventoryCloseEvent event) {
    }
}
