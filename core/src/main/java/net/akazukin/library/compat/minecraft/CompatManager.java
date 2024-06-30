package net.akazukin.library.compat.minecraft;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import org.bukkit.Bukkit;

@Getter
public class CompatManager {
    public static Compat initCompat() {
        return getCompat("net.akazukin.library.compat.minecraft.compats.Compat_" + getMappingVersion());
    }

    public static Compat getCompat(final String clazzName) {
        try {
            final Class<?> clazz = Class.forName(clazzName);
            if (Compat.class.isAssignableFrom(clazz)) {
                return getCompat((Class<? extends Compat>) clazz);
            } else {
                throw new IllegalArgumentException("The class was not extends ComaptClass");
            }
        } catch (final IllegalArgumentException | ClassNotFoundException e) {
            LibraryPlugin.getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public static String getMappingVersion() {
        switch (Bukkit.getServer().getBukkitVersion().split("-")[0]) {
            case "1.21": {
                return "v_1_21_R1";
            }

            case "1.20.5":
            case "1.20.6": {
                return "v_1_20_R4";
            }

            case "1.20.3":
            case "1.20.4": {
                return "v_1_20_R3";
            }

            case "1.20.2": {
                return "v_1_20_R2";
            }

            case "1.20":
            case "1.20.1": {
                return "v_1_20_R1";
            }

            case "1.19.4": {
                return "v_1_19_R3";
            }

            case "1.19.3": {
                return "v_1_19_R2";
            }

            case "1.19":
            case "1.19.1":
            case "1.19.2": {
                return "v_1_19_R1";
            }

            case "1.18":
            case "1.18.1":
            case "1.18.2": {
                return "v_1_18_R1";
            }


            default: {
                return null;
            }
        }
    }

    public static Compat getCompat(final Class<? extends Compat> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (final IllegalArgumentException | InvocationTargetException |
                       NoSuchMethodException |
                       IllegalAccessException | InstantiationException e) {
            LibraryPlugin.getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }
}
