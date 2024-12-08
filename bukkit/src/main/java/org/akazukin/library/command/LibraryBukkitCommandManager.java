package org.akazukin.library.command;

import org.akazukin.library.command.commands.AkazukinCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class LibraryBukkitCommandManager extends BukkitCommandManager {
    public LibraryBukkitCommandManager(final JavaPlugin plugin) {
        super(plugin);
    }

    public void registerCommands() {
        this.registerCommands(
                AkazukinCommand.class
        );
    }
}
