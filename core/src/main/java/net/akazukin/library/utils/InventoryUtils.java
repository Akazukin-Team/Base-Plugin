package net.akazukin.library.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    private static final ItemStack prevItem;
    private static final ItemStack blankItem;
    private static final ItemStack closeItem;

    static {
        final ItemStack prevItem_ = new ItemStack(Material.getMaterial("RED_WOOL"));
        ItemUtils.setDisplayName(prevItem_, "§c§lOpen Previous Menu");
        prevItem = ItemUtils.setGuiItem(prevItem_);

        final ItemStack blankItem_ = new ItemStack(Material.getMaterial("LIGHT_GRAY_STAINED_GLASS_PANE"));
        ItemUtils.setDisplayName(blankItem_, "§7Empty Slot");
        blankItem = ItemUtils.setGuiItem(blankItem_);

        final ItemStack closeItem_ = new ItemStack(Material.getMaterial("BARRIER"));
        ItemUtils.setDisplayName(closeItem_, "§c§lClose Menu");
        closeItem = ItemUtils.setGuiItem(closeItem_);
    }

    public static Inventory createInventory(final String name, final int rows) {
        return Bukkit.createInventory(null, 9 * rows, name);
    }

    public static void fillItems(final Inventory inventory, final ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, itemStack);
        }
    }

    public static void fillBlankItems(final Inventory inventory) {
        fillItems(inventory, blankItem);
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

    public static boolean isBlankItem(final ItemStack itemStack) {
        return blankItem.equals(itemStack);
    }
}
