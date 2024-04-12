package net.akazukin.library.utils;

import net.akazukin.library.LibraryPlugin;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemUtils {
    public static ItemStack setGuiItemType(@Nonnull final ItemStack itemStack, @Nonnull final String type) {
        return LibraryPlugin.COMPAT.setNBT(itemStack, "AKZ_GUI_ITEM_TYPE", type);
    }

    public static String getGuiItemType(@Nonnull final ItemStack itemStack) {
        return LibraryPlugin.COMPAT.getNBTString(itemStack, "AKZ_GUI_ITEM_TYPE");
    }

    public static ItemStack setGuiItem(@Nonnull final ItemStack itemStack) {
        return LibraryPlugin.COMPAT.setNBT(itemStack, "AKZ_GUI_ITEM", true);
    }

    public static boolean isGuiItem(@Nonnull final ItemStack itemStack) {
        return Boolean.TRUE.equals(LibraryPlugin.COMPAT.getNBTBoolean(itemStack, "AKZ_GUI_ITEM"));
    }

    public static ItemStack getSkullItem(@Nonnull final OfflinePlayer player) {
        final ItemStack skullItem = new ItemStack(Material.getMaterial("PLAYER_HEAD"));
        final SkullMeta skullItemMeta = (SkullMeta) skullItem.getItemMeta();
        skullItemMeta.setOwningPlayer(player);
        skullItem.setItemMeta(skullItemMeta);
        return LibraryPlugin.COMPAT.setNBT(skullItem, "HEAD_UUID", String.valueOf(player.getUniqueId()));
    }

    public static void setDisplayName(@Nonnull final ItemStack itemStack, final String displayName) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);
    }

    public static void setLore(@Nonnull final ItemStack itemStack, final List<String> lores) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
    }

    @Nullable
    public static List<String> getLore(@Nonnull final ItemStack itemStack) {
        return itemStack.getItemMeta().getLore();
    }
}
