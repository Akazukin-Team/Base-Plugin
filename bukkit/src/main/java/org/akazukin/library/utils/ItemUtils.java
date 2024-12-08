package org.akazukin.library.utils;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Nonnull;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
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

    public static String getDisplayName(@Nonnull final ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return null;
        return itemStack.getItemMeta().getDisplayName();
    }

    public static void setDisplayName(@Nonnull final ItemStack itemStack, final String displayName) {
        if (!itemStack.hasItemMeta()) return;
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);
    }

    public static void setLore(@Nonnull final ItemStack itemStack, final List<String> lores) {
        if (!itemStack.hasItemMeta()) return;
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
    }

    public static List<String> getLore(@Nonnull final ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return null;
        return itemStack.getItemMeta().getLore();
    }

    public static ToolType getToolType(final ItemStack itemStack) {
        if (itemStack.getType().name().endsWith("_SWORD")) {
            return ToolType.SWORD;
        } else if (itemStack.getType().name().endsWith("_PICKAXE")) {
            return ToolType.PICKAXE;
        } else if (itemStack.getType().name().endsWith("_AXE")) {
            return ToolType.AXE;
        } else if (itemStack.getType().name().endsWith("_SHOVEL")) {
            return ToolType.SHOVEL;
        } else if (itemStack.getType().name().endsWith("_HOE")) {
            return ToolType.HOE;

        } else if (itemStack.getType().name().equals("SHEARS")) {
            return ToolType.SHEARS;
        } else if (itemStack.getType().name().equals("SHIELD")) {
            return ToolType.SHIELD;

        } else if (itemStack.getType().name().equals("BOW")) {
            return ToolType.BOW;
        } else if (itemStack.getType().name().equals("FISHING_ROD")) {
            return ToolType.FISHING_ROD;

        } else if (itemStack.getType().name().endsWith("_HELMET")) {
            return ToolType.HELMET;
        } else if (itemStack.getType().name().endsWith("_CHESTPLATE")) {
            return ToolType.CHESTPLATE;
        } else if (itemStack.getType().name().endsWith("_LEGGINGS")) {
            return ToolType.LEGGINGS;
        } else if (itemStack.getType().name().endsWith("_BOOTS")) {
            return ToolType.BOOTS;
        }
        return null;
    }

    public static ToolType getBestToolType(final BlockState blockState) {
        final float sword = LibraryPlugin.COMPAT.getDestroySpeed(new ItemStack(Material.STONE_SWORD), blockState);
        final float pickaxe = LibraryPlugin.COMPAT.getDestroySpeed(new ItemStack(Material.STONE_PICKAXE), blockState);
        final float axe = LibraryPlugin.COMPAT.getDestroySpeed(new ItemStack(Material.STONE_AXE), blockState);
        final float shovel = LibraryPlugin.COMPAT.getDestroySpeed(new ItemStack(Material.STONE_SHOVEL), blockState);
        final float hoe = LibraryPlugin.COMPAT.getDestroySpeed(new ItemStack(Material.STONE_HOE), blockState);
        final float shears = LibraryPlugin.COMPAT.getDestroySpeed(new ItemStack(Material.SHEARS), blockState);

        final float speed = MathUtils.max(sword, pickaxe, axe, shovel, hoe, shears);

        if (sword == pickaxe && pickaxe == axe && axe == shovel && shovel == hoe && hoe == shears) {
            return null;
        } else if (sword == speed) {
            return ToolType.SWORD;
        } else if (pickaxe == speed) {
            return ToolType.PICKAXE;
        } else if (axe == speed) {
            return ToolType.AXE;
        } else if (shovel == speed) {
            return ToolType.SHOVEL;
        } else if (hoe == speed) {
            return ToolType.HOE;
        } else if (shears == speed) {
            return ToolType.SHEARS;
        }
        return null;
    }

    public enum ToolType {
        SWORD,
        PICKAXE,
        AXE,
        SHOVEL,
        HOE,

        BOW,
        FISHING_ROD,

        SHEARS,
        SHIELD,

        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }
}
