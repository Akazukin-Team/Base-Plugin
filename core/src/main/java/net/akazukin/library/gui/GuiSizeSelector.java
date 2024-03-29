package net.akazukin.library.gui;

import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.InventoryUtils;
import net.akazukin.library.utils.MessageHelper;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

@Getter
public class GuiSizeSelector extends ChestGuiBase {
    private final int max;
    private final int min;

    @Setter
    private int size;

    public GuiSizeSelector(final String title, final UUID player, final int min, final int max, final int defaulT, final GuiBase prevGui) {
        super(title, 6, player, false, prevGui);
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);

        size = defaulT;
    }

    @Override
    public Inventory getInventory() {
        final Inventory inv = super.getInventory();
        InventoryUtils.fillEmptyItems(inv);
        InventoryUtils.fillCloseItem(inv);
        InventoryUtils.fillPrevGuiItem(inv);

        final ItemStack increaseItem = new ItemStack(Material.getMaterial("WATER_BUCKET"));
        final ItemMeta increaseItemMeta = increaseItem.getItemMeta();
        final ItemStack decreaseItem = new ItemStack(Material.getMaterial("LAVA_BUCKET"));
        final ItemMeta decreaseItemMeta = decreaseItem.getItemMeta();

        final ItemStack itemStack2 = new ItemStack(Material.getMaterial("DIAMOND_BLOCK"));
        final ItemMeta itemMeta2 = itemStack2.getItemMeta();
        itemMeta2.setDisplayName("§aAmount: " + size);
        itemMeta2.setLore(Arrays.asList(
                LibraryPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("gui.selector.size.max"), max),
                LibraryPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("gui.selector.size.min"), min)
        ));
        itemStack2.setItemMeta(itemMeta2);
        itemStack2.setAmount(size);
        inv.setItem(13, itemStack2);

        int index = 19;
        for (int i = 1; i <= max; i = i * 4) {
            if (min > i) continue;

            increaseItemMeta.setDisplayName("§a" + i);
            increaseItem.setItemMeta(increaseItemMeta);
            increaseItem.setAmount(i);
            inv.setItem(index, increaseItem);

            decreaseItemMeta.setDisplayName("§c" + i);
            decreaseItem.setItemMeta(decreaseItemMeta);
            decreaseItem.setAmount(i);
            inv.setItem(index + 9, decreaseItem);

            index++;
        }

        return inv;
    }

    @Override
    protected void onGuiClick(final InventoryClickEvent event) {
        if (event.getCurrentItem().getType() == Material.getMaterial("WATER_BUCKET") &&
                event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§a")) {
            final int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
            if (size >= max) {
                LibraryPlugin.MESSAGE_HELPER.sendMessage(event.getWhoClicked(), I18n.of("gui.selector.amount.cantIncrease"));
            } else {
                size += amount;
                if (size > max) size = max;

                if (event.getView().getItem(13).getType().equals(Material.getMaterial("DIAMOND_BLOCK")) &&
                        event.getView().getItem(13).getItemMeta().getDisplayName().startsWith("§a")) {
                    final ItemMeta itemMeta2 = event.getView().getItem(13).getItemMeta();
                    itemMeta2.setDisplayName("§aAmount: " + size);
                    event.getView().getItem(13).setItemMeta(itemMeta2);
                    event.getView().getItem(13).setAmount(size);
                }
            }
        } else if (event.getCurrentItem().getType() == Material.getMaterial("LAVA_BUCKET") &&
                event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§c")) {
            final int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
            if (size <= min) {
                LibraryPlugin.MESSAGE_HELPER.sendMessage(event.getWhoClicked(), I18n.of("gui.selector.size.cantDecrease"));
            } else {
                size -= amount;
                if (size < min) size = min;


                if (event.getView().getItem(13).getType().equals(Material.getMaterial("DIAMOND_BLOCK")) &&
                        event.getView().getItem(13).getItemMeta().getDisplayName().startsWith("§a")) {
                    final ItemMeta itemMeta2 = event.getView().getItem(13).getItemMeta();
                    itemMeta2.setDisplayName("§aAmount: " + size);
                    event.getView().getItem(13).setItemMeta(itemMeta2);
                    event.getView().getItem(13).setAmount(size);
                }
            }
        }
    }
}
