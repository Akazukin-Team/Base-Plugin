package org.akazukin.library.utils;

import org.akazukin.i18n.I18n;
import org.akazukin.library.LibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class InventoryUtils {
    public static boolean isCloseItem(final ItemStack itemStack) {
        return Objects.equals(ItemUtils.getGuiItemType(itemStack), "CLOSE");
    }

    public static boolean isBackItem(final ItemStack itemStack) {
        return Objects.equals(ItemUtils.getGuiItemType(itemStack), "BACK");
    }

    public static boolean isBlankItem(final ItemStack itemStack) {
        return Objects.equals(ItemUtils.getGuiItemType(itemStack), "BLANK");
    }

    public static Inventory createInventory(final String name, final int rows) {
        return Bukkit.createInventory(null, 9 * rows, name);
    }

    public static void fillBlankItems(final Inventory inventory, final String locale) {
        fillItems(inventory, getBlankItem(locale));
    }

    public static void fillItems(final Inventory inventory, final ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, itemStack);
        }
    }

    public static ItemStack getBlankItem(final String locale) {
        ItemStack blankItem = new ItemStack(Material.getMaterial("LIGHT_GRAY_STAINED_GLASS_PANE"));
        ItemUtils.setDisplayName(blankItem, LibraryPlugin.getPlugin().getMessageHelper().get(locale, I18n.of("library" +
                ".gui.item.blank")));
        blankItem = ItemUtils.setGuiItem(blankItem);
        return ItemUtils.setGuiItemType(blankItem, "BLANK");
    }

    public static void fillCloseItem(final Inventory inventory, final String locale) {
        inventory.setItem(inventory.getSize() - 9, getCloseItem(locale));
    }

    public static ItemStack getCloseItem(final String locale) {
        ItemStack closeItem = new ItemStack(Material.getMaterial("BARRIER"));
        ItemUtils.setDisplayName(closeItem, LibraryPlugin.getPlugin().getMessageHelper().get(locale, I18n.of("library" +
                ".gui.item.close")));
        closeItem = ItemUtils.setGuiItem(closeItem);
        return ItemUtils.setGuiItemType(closeItem, "CLOSE");
    }

    public static void fillBackItem(final Inventory inventory, final String locale) {
        inventory.setItem(inventory.getSize() - 8, getBackItem(locale));
    }

    public static ItemStack getBackItem(final String locale) {
        ItemStack backItem = new ItemStack(Material.getMaterial("IRON_DOOR"));
        ItemUtils.setDisplayName(backItem, LibraryPlugin.getPlugin().getMessageHelper().get(locale, I18n.of("library" +
                ".gui.item.back")));
        backItem = ItemUtils.setGuiItem(backItem);
        return ItemUtils.setGuiItemType(backItem, "BACK");
    }
}
