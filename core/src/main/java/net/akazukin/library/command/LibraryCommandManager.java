package net.akazukin.library.command;

import net.akazukin.library.command.commands.AkazukinCommand;

public final class LibraryCommandManager extends CommandManager {
    public void registerCommands() {
        registerCommands(
                AkazukinCommand.class
        );
    }
}
