package net.akazukin.library.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class InventoryUtils {
    private static final ItemStack prevItem;
    private static final ItemStack emptyItem;
    private static final ItemStack closeItem;

    static {
        prevItem = new ItemStack(Material.getMaterial("RED_WOOL"));
        final ItemMeta prevItemMeta = prevItem.getItemMeta();
        prevItemMeta.setDisplayName("§c§lOpen Previous Menu");
        prevItemMeta.setLore(Arrays.asList(
                "§fWhen u click this, open previous gui",
                "§6§lUndroppable§r§7§l"
        ));
        prevItem.setItemMeta(prevItemMeta);


        emptyItem = new ItemStack(Material.getMaterial("LIGHT_GRAY_STAINED_GLASS_PANE"));
        final ItemMeta emptyItemMeta = emptyItem.getItemMeta();
        emptyItemMeta.setDisplayName("§7Empty Slot");
        emptyItemMeta.setLore(Collections.singletonList("§6§lUndroppable§r§7§l"));
        emptyItem.setItemMeta(emptyItemMeta);


        closeItem = new ItemStack(Material.getMaterial("BARRIER"));
        final ItemMeta closeItemMeta = closeItem.getItemMeta();
        closeItemMeta.setDisplayName("§c§lClose Menu");
        closeItemMeta.setLore(Arrays.asList(
                "§fWhen u click this, close your opened gui",
                "§6§lUndroppable§r§7§l"
        ));
        closeItem.setItemMeta(closeItemMeta);
    }

    public static Inventory createInventory(final String name, final int rows) {
        return Bukkit.createInventory(null, 9 * rows, name);
    }

    public static void fillItems(final Inventory inventory, final ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, itemStack);
        }
    }

    public static void fillEmptyItems(final Inventory inventory) {
        fillItems(inventory, emptyItem);
    }

    public static void fillCloseItem(final Inventory inventory) {
        inventory.setItem(inventory.getSize() - 9, closeItem);
    }

    public static void fillPrevGuiItem(final Inventory inventory) {
        inventory.setItem(inventory.getSize() - 8, prevItem);
    }

    public static boolean isCloseItem(final ItemStack itemStack) {
        return closeItem.equals(itemStack);
    }

    public static boolean isPrevItem(final ItemStack itemStack) {
        return prevItem.equals(itemStack);
    }
}
