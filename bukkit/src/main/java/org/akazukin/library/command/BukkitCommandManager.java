package org.akazukin.library.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BukkitCommandManager extends CommandManager implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;

    public BukkitCommandManager(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerCommand(final Command command) {
        super.registerCommand(command);
        
        final PluginCommand cmd = this.plugin.getCommand(command.getName());
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);

            final PluginCommand cmd2 =
                    this.plugin.getCommand(this.plugin.getName().toLowerCase() + ":" + cmd.getName());
            if (cmd2 != null) {
                cmd2.setExecutor(this);
                cmd2.setTabCompleter(this);
            }
        }
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final org.bukkit.command.Command cmd,
                                      final String cmdName, final String[] args) {
        final Command cmD = this.getCommand(cmdName);
        final ICmdSender s = new BukkitPlayerCommandSender((Player) sender);
        if (cmD != null && cmD.hasPermission(s)) {
            final String[] comp = cmD.getCompletion(s, cmd.getName(), args, args);
            if (comp != null) return Arrays.asList(comp);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label
            , final String[] args) {
        new Thread(() -> {
            final Command cmd = this.getCommand(label);
            if (cmd == null || !cmd.handleEvents()) return;

            cmd.run(
                    sender instanceof Player ?
                            new BukkitPlayerCommandSender((Player) sender) :
                            new BukkitCommandSender(),
                    args, args);
        }, "Processing Command-Process").start();
        return true;
    }
}
