package org.akazukin.library.gui.screens.anvil;

import java.util.function.Predicate;
import lombok.Getter;
import lombok.Setter;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.akazukin.library.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class GuiNameSelector extends AnvilGui {
    private final Predicate<String> isValid;
    @Setter
    @Getter
    private String defaultName;

    public GuiNameSelector(final String title, final Player player, final String defaultName, final GuiBase prevGui,
                           final Predicate<String> isValid) {
        super(title, player, prevGui, false, 0);
        this.isValid = isValid;
        if (defaultName == null || defaultName.isEmpty()) {
            this.defaultName = "None";
        } else {
            this.defaultName = defaultName;
        }
        this.name = defaultName;
    }

    @Override
    protected Inventory getInventory() {
        final Inventory inv = super.getInventory();

        final ItemStack item = new ItemStack(Material.PAPER);
        ItemUtils.setDisplayName(item, this.defaultName);
        inv.setItem(0, item);

        return inv;
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (event.getSlot() == 2 && this.isValid.test(this.name)) this.defaultName = this.name;

        GuiManager.singleton().setScreen(this.player, () -> this.prevGui);
        return true;
    }
}
