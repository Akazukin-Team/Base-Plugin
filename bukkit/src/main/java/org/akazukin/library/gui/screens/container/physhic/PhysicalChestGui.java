package org.akazukin.library.gui.screens.container.physhic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.gui.IGui;
import org.akazukin.library.gui.OpenStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class PhysicalChestGui implements IPhysicalContainerGui {
    @Getter
    Inventory inv;
    @Getter
    IGui prevGui;

    public PhysicalChestGui(final int size, @Nullable final IGui prevGui) {
        this.inv = Bukkit.createInventory(null, size);
        this.prevGui = prevGui;
    }

    public PhysicalChestGui(final String title, final int size, @Nullable final IGui prevGui) {
        this.inv = Bukkit.createInventory(null, size, title);
        this.prevGui = prevGui;
    }

    public ItemStack[] getContents() {
        return this.inv.getContents();
    }

    public void setContents(final ItemStack[] contents) {
        this.inv.setContents(contents);
    }

    @Override
    public OpenStatus open(final UUID player) {
        final Player bktPlayer = Bukkit.getPlayer(player);
        if (bktPlayer == null) {
            return OpenStatus.FAILURE;
        }

        this.refreshContainer();

        bktPlayer.openInventory(this.inv);

        return OpenStatus.SUCCESS_ALWAYS_OPNE;
    }

    @Override
    public void close(final UUID player) {
        final HumanEntity p = this.inv.getViewers()
                .stream()
                .filter(v -> v.getUniqueId().equals(player))
                .findAny()
                .orElse(null);
        if (p == null) {
            return;
        }

        p.closeInventory();
    }

    @Override
    public void onOpen(final UUID player) {
    }

    @Override
    public void onClose(final UUID player) {
    }

    @Override
    public void destroy() {
    }

    protected void refreshContainer() {
    }

    @Override
    public void onClick(final InventoryClickEvent event) {
    }
}
