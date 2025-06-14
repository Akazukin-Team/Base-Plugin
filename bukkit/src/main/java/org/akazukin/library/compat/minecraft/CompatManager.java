package org.akazukin.library.compat.minecraft;

import lombok.Getter;
import org.akazukin.library.LibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

@Getter
public class CompatManager {
    public static ICompat initCompat(final JavaPlugin plugin) {
        return getCompat("org.akazukin.library.compat.minecraft.compats." + getMappingVersion() + ".Compat", plugin);
    }

    @SuppressWarnings("unchecked")
    public static ICompat getCompat(final String clazzName, final JavaPlugin plugin) {
        try {
            final Class<?> clazz = Class.forName(clazzName);
            if (ICompat.class.isAssignableFrom(clazz)) {
                return getCompat((Class<? extends ICompat>) clazz, plugin);
            } else {
                throw new IllegalArgumentException("The class was not extends ComaptClass");
            }
        } catch (final IllegalArgumentException | ClassNotFoundException e) {
            LibraryPlugin.getPlugin().getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public static ICompat getCompat(final Class<? extends ICompat> clazz, final JavaPlugin plugin) {
        try {
            return clazz.getDeclaredConstructor(JavaPlugin.class).newInstance(plugin);
        } catch (final IllegalArgumentException | InvocationTargetException |
                       NoSuchMethodException |
                       IllegalAccessException | InstantiationException e) {
            LibraryPlugin.getPlugin().getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public static String getMappingVersion() {
        final String clazz = LibraryPlugin.getPlugin().getServer().getClass().getName();
        if (clazz.matches("org\\.bukkit\\.craftbukkit\\.v_1_[0-9]+_R[0-4]\\.CraftServer")) {
            return clazz.replaceFirst("org\\.bukkit\\.craftbukkit\\.(v_1_[0-9]+_R[0-4])\\.CraftServer", "$1");
        }

        switch (Bukkit.getServer().getBukkitVersion().split("-")[0]) {
            case "1.21.5": {
                return "v1_21_R4";
            }

            case "1.21.4": {
                return "v1_21_R3";
            }

            case "1.21.3": {
                return "v1_21_R2";
            }

            case "1.21":
            case "1.21.1":
            case "1.21.2": {
                return "v1_21_R1";
            }

            case "1.20.5":
            case "1.20.6": {
                return "v1_20_R4";
            }

            case "1.20.3":
            case "1.20.4": {
                return "v1_20_R3";
            }

            case "1.20.2": {
                return "v1_20_R2";
            }

            case "1.20":
            case "1.20.1": {
                return "v1_20_R1";
            }

            case "1.19.4": {
                return "v1_19_R3";
            }

            case "1.19.3": {
                return "v1_19_R2";
            }

            case "1.19":
            case "1.19.1":
            case "1.19.2": {
                return "v1_19_R1";
            }

            case "1.18":
            case "1.18.1":
            case "1.18.2": {
                return "v1_18_R1";
            }


            default: {
                return null;
            }
        }
    }
}
