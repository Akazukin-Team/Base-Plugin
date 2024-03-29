package net.akazukin.library.command;

import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

@Getter
public abstract class CommandManager implements CommandExecutor {
    private final List<Command> commands = new ArrayList<>();

    public void registerCommands(final Class<? extends Command>... commands) {
        Arrays.stream(commands).forEach(this::registerCommand);
    }

    public void registerCommand(final Class<? extends Command> command) {
        try {
            registerCommand(command.newInstance());
        } catch (final InstantiationException | IllegalAccessException e) {
            LibraryPlugin.getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void registerCommand(final Command command) {
        commands.add(command);
    }

    public Command getCommand(final String name) {
        return commands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Command getCommand(final Class<? extends Command> aClass) {
        return commands.stream().filter(cmd -> cmd.getClass().equals(aClass)).findFirst().orElse(null);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label, final String[] args) {
        final Command cmd = getCommand(label);
        if (cmd == null || !cmd.handleEvents()) return false;

        return cmd.run(sender, args);
    }
}
