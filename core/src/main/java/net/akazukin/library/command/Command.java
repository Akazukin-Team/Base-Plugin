package net.akazukin.library.command;

import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.event.Listenable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
public abstract class Command implements Listenable {
    private final String name;
    private final String description;
    private final SubCommand[] subCommands;
    private final String permission;
    private final CommandExecutor executor;

    @Setter
    private boolean toggle = true;

    public Command() {
        final CommandInfo commandInfo = getClass().getAnnotation(CommandInfo.class);

        name = commandInfo.name();
        description = commandInfo.description();
        permission = commandInfo.permission();
        executor = commandInfo.executor();
        subCommands = getSubCommands();
    }

    public SubCommand[] getSubCommands() {
        return new SubCommand[]{};
    }

    public SubCommand getSubCommand(final String name) {
        return Arrays.stream(subCommands).filter(cmd -> name == null ? cmd.getName().equalsIgnoreCase("") : cmd.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public abstract void run(CommandSender sender, String... args);

    @Override
    public boolean handleEvents() {
        return toggle;
    }

    public final boolean hasPermission(final CommandSender sender) {
        return permission.isEmpty() || !(sender instanceof Player) || sender.hasPermission(permission);
    }

    public final boolean validExecutor(final CommandSender sender) {
        if (executor == CommandExecutor.CONSOLE && !(sender instanceof Player)) {
            return true;
        } else if (executor == CommandExecutor.PLAYER && sender instanceof Player) {
            return true;
        }
        return executor == CommandExecutor.BOTH;
    }
}
