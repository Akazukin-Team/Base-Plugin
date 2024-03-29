package net.akazukin.library.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Predicate;

@Deprecated
public class GuiNameSelector extends AnvilGui {
    @Setter
    @Getter
    private String defaultName;
    private final Predicate<String> isValid;

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
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(defaultName);
        itemMeta.setLore(Collections.singletonList(
                "§6Undroppable"
        ));
        item.setItemMeta(itemMeta);
        inv.setItem(0, item);

        return inv;
    }

    @Override
    protected void onGuiClick(final InventoryClickEvent event) {
        if (event.getSlot() == 2 && isValid.test(name)) defaultName = name;

        GuiManager.singleton().setScreen(player, prevGui);
    }
}
