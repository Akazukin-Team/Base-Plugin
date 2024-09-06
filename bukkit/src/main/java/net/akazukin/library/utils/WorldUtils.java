package net.akazukin.library.utils;

import java.io.File;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.akazukin.library.compat.worldguard.WorldGuardCompat;
import net.akazukin.library.world.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class WorldUtils {
    public static World getOrLoadWorld(@Nonnull final WorldData worldData) {
        return getOrLoadWorld(worldData, () -> Bukkit.createWorld(WorldCreator.name(worldData.getName())));
    }

    public static World getOrLoadWorld(@Nonnull final WorldData worldData, @Nonnull final Supplier<World> creator) {
        final World w = getWorld(worldData);
        if (w != null) return w;

        return creator.get();
    }

    public static World getWorld(@Nonnull final WorldData worldData) {
        return worldData.getUid() != null ? Bukkit.getWorld(worldData.getUid()) :
                (worldData.getName() != null ? Bukkit.getWorld(worldData.getName()) : null);
    }

    public static boolean isWorldLoaded(@Nonnull final WorldData worldData) {
        return getWorld(worldData) != null;
    }

    public static boolean deleteWorld(@Nonnull final World world) {
        return deleteWorld(new WorldData(world.getUID(), world.getName()));
    }

    public static boolean deleteWorld(@Nonnull final WorldData worldData) {
        if (!worldData.isValid())
            throw new IllegalArgumentException("World data is invalid");

        final World w = getWorld(worldData);
        final File dir;
        if (w != null) {
            Bukkit.unloadWorld(w, false);
            dir = w.getWorldFolder();
            if (PluginUtils.isEnabled("worldguard")) WorldGuardCompat.removeRegion(w);
        } else {
            dir = new File(Bukkit.getWorldContainer(), worldData.getName());
        }

        return FileUtils.delete(dir);
    }
}
