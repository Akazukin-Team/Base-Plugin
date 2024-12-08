package org.akazukin.library.compat.minecraft.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

@AllArgsConstructor
@Getter
@Setter
public class WrappedAnvilInventory {
    private final Inventory inventory;
    private final String renameText;
    private int repairCost;
    private int maximumRepairCost;
}
