package org.akazukin.library.command;

import lombok.Getter;
import org.akazukin.library.LibraryPluginProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

@Getter
public abstract class CommandManager<T extends ICmdSender> {
    private final List<Command<T>> commands = new ArrayList<>();

    public void registerCommands(final Class<? extends Command<T>>... commands) {
        Arrays.stream(commands).forEach(this::registerCommand);
    }

    public void registerCommand(final Class<? extends Command<T>> command) {
        try {
            this.registerCommand(command.newInstance());
        } catch (final InstantiationException | IllegalAccessException e) {
            LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void registerCommand(final Command<T> command) {
        this.commands.add(command);
    }

    public Command<T> getCommand(final Class<? extends Command<T>> aClass) {
        return this.commands.stream()
                .filter(cmd -> cmd.getClass().equals(aClass))
                .findFirst()
                .orElse(null);
    }

    public Command<T> getCommand(final String name) {
        return this.commands.stream()
                .filter(cmd ->
                        cmd.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
