package net.akazukin.library.command;

import net.akazukin.library.command.commands.AkazukinCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class LibraryCommandManager extends CommandManager {
    public LibraryCommandManager(final JavaPlugin plugin) {
        super(plugin);
    }

    public void registerCommands() {
        this.registerCommands(
                AkazukinCommand.class
        );
    }
}
