package net.akazukin.library.gui.screens.anvil;

import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Predicate;

@Deprecated
public class GuiNameSelector extends AnvilGui {
    private final Predicate<String> isValid;
    @Setter
    @Getter
    private String defaultName;

    public GuiNameSelector(final String title, final UUID player, final String defaultName, final GuiBase prevGui, final Predicate<String> isValid) {
        super(title, player, prevGui, false, 0);
        this.isValid = isValid;
        if (defaultName == null || defaultName.isEmpty()) {
            this.defaultName = "None";
        } else {
            this.defaultName = defaultName;
        }
        name = defaultName;
    }

    @Override
    protected Inventory getInventory() {
        final Inventory inv = super.getInventory();

        final ItemStack item = new ItemStack(Material.PAPER);
        ItemUtils.setDisplayName(item, defaultName);
        inv.setItem(0, item);

        return inv;
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (event.getSlot() == 2 && isValid.test(name)) defaultName = name;

        GuiManager.singleton().setScreen(player, prevGui);
        return true;
    }
}
