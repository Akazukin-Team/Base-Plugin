package net.akazukin.library.utils;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUtils {
    public static ItemStack setGuiItemType(@Nonnull final ItemStack itemStack, @Nonnull final String type) {
        return LibraryPlugin.COMPAT.setPlData(itemStack, "AKZ_GUI_ITEM_TYPE", type);
    }

    public static String getGuiItemType(@Nonnull final ItemStack itemStack) {
        return LibraryPlugin.COMPAT.getPlDataString(itemStack, "AKZ_GUI_ITEM_TYPE");
    }

    public static ItemStack setGuiItem(@Nonnull final ItemStack itemStack) {
        return LibraryPlugin.COMPAT.setPlData(itemStack, "AKZ_GUI_ITEM", true);
    }

    public static boolean isGuiItem(@Nonnull final ItemStack itemStack) {
        return Boolean.TRUE.equals(LibraryPlugin.COMPAT.getPlDataBool(itemStack, "AKZ_GUI_ITEM"));
    }

    public static ItemStack getSkullItem(@Nonnull final OfflinePlayer player) {
        final ItemStack skullItem = new ItemStack(Material.getMaterial("PLAYER_HEAD"));
        final SkullMeta skullItemMeta = (SkullMeta) skullItem.getItemMeta();

        if (player instanceof Player) {
            skullItemMeta.setOwningPlayer(player);
        } else {
            try {
                final WrappedPlayerProfile profile = PlayerUtils.get(player.getUniqueId());
                if (profile != null) {
                    final GameProfile profile_ = PlayerUtils.getProfile(profile);

                    try {
                        final Method m = skullItemMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                        m.setAccessible(true);
                        m.invoke(skullItemMeta, profile_);
                    } catch (final NoSuchMethodException ignored) {
                        ignored.printStackTrace();
                        ReflectionUtils.setField(skullItemMeta, "profile", profile_);
                    }
                }
            } catch (final NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        skullItem.setItemMeta(skullItemMeta);
        return LibraryPlugin.COMPAT.setPlData(skullItem, "HEAD_UUID", String.valueOf(player.getUniqueId()));
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

    @Nonnull
    public static List<String> getLore(@Nonnull final ItemStack itemStack) {
        if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) return new ArrayList<>();
        return itemStack.getItemMeta().getLore();
    }
}
