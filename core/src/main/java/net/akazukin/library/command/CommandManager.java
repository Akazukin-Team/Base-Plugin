package net.akazukin.library.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public abstract class CommandManager implements CommandExecutor, TabCompleter {
    private final List<Command> commands = new ArrayList<>();
    private final JavaPlugin plugin;

    protected CommandManager(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommands(final Class<? extends Command>... commands) {
        Arrays.stream(commands).forEach(this::registerCommand);
    }

    public void registerCommand(final Class<? extends Command> command) {
        try {
            this.registerCommand(command.newInstance());
        } catch (final InstantiationException | IllegalAccessException e) {
            LibraryPlugin.getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void registerCommand(final Command command) {
        final PluginCommand commanD = this.plugin.getCommand(command.getName());
        if (commanD == null) return;
        final PluginCommand command2 =
                this.plugin.getCommand(this.plugin.getName().toLowerCase() + ":" + commanD.getName());
        if (command2 == null) return;

        commanD.setExecutor(this);
        commanD.setTabCompleter(this);
        command2.setExecutor(this);
        command2.setTabCompleter(this);

        this.commands.add(command);
    }

    public Command getCommand(final Class<? extends Command> aClass) {
        return this.commands.stream().filter(cmd -> cmd.getClass().equals(aClass)).findFirst().orElse(null);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label
            , final String[] args) {
        new Thread(() -> {
            final Command cmd = this.getCommand(label);
            if (cmd == null || !cmd.handleEvents()) return;

            cmd.run(sender, args, args);
        }).start();
        return true;
    }

    public Command getCommand(final String name) {
        return this.commands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final org.bukkit.command.Command cmd,
                                      final String cmdName, final String[] args) {
        final Command cmD = this.getCommand(cmdName);
        if (cmD != null && cmD.hasPermission(sender)) {
            final String[] comp = cmD.getCompletion(sender, cmd, args, args);
            if (comp != null) return Arrays.asList(comp);
        }
        return Collections.emptyList();
    }
}
