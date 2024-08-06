package net.akazukin.library.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import lombok.Getter;
import net.akazukin.library.LibraryPluginProvider;

@Getter
public abstract class CommandManager {
    private final List<Command> commands = new ArrayList<>();

    public void registerCommands(final Class<? extends Command>... commands) {
        Arrays.stream(commands).forEach(this::registerCommand);
    }

    public void registerCommand(final Class<? extends Command> command) {
        try {
            this.registerCommand(command.newInstance());
        } catch (final InstantiationException | IllegalAccessException e) {
            LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void registerCommand(final Command command) {
        this.commands.add(command);
    }

    public Command getCommand(final Class<? extends Command> aClass) {
        return this.commands.stream().filter(cmd -> cmd.getClass().equals(aClass)).findFirst().orElse(null);
    }

    public Command getCommand(final String name) {
        return this.commands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
