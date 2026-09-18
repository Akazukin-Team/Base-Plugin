package org.akazukin.library.gui.screens.container.physhic;

import org.akazukin.library.gui.IGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface IPhysicalContainerGui extends IGui {
    void onClick(InventoryClickEvent event);

    Inventory getInv();
}
