package net.akazukin.library.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.utils.http.HttpUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

        if ((player instanceof Player)) {
            skullItemMeta.setOwningPlayer(player);
        } else {
            try {
                final GameProfile profile = ReflectionUtils.getField(skullItemMeta, "profile", GameProfile.class);

                if (profile.getProperties().get("textures").isEmpty()) {
                    final JsonObject prop = new JsonParser()
                            .parse(new String(
                                    HttpUtils.requestGet("https://api.mineskin.org/generate/user/" + player.getUniqueId()),
                                    StandardCharsets.UTF_8))
                            .getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonObject("texture");
                    final Property textures = new Property("textures", prop.get("value").getAsString(), null);
                    profile.getProperties().put("textures", textures);

                    try {
                        final Method m = skullItemMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                        m.setAccessible(true);
                        m.invoke(skullItemMeta, profile);
                    } catch (final NoSuchMethodException ignored) {
                        ReflectionUtils.setField(skullItemMeta, "profile", profile);
                    }
                }
            } catch (final NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

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

    @Nonnull
    public static List<String> getLore(@Nonnull final ItemStack itemStack) {
        if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) return new ArrayList<>();
        return itemStack.getItemMeta().getLore();
    }
}
