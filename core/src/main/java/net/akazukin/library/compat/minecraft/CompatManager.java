package net.akazukin.library.compat.minecraft;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import lombok.Getter;
import net.akazukin.library.LibraryPlugin;

@Getter
public class CompatManager {
    public static Compat initCompat() {
        final String version = LibraryPlugin.getPlugin().getServer().getClass().getPackage().getName().split("\\.")[3];
        return getCompat("net.akazukin.library.compat.minecraft.compats.Compat_" + version);
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
