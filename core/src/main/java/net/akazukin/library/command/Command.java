package net.akazukin.library.command;

import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.event.Listenable;
import net.akazukin.library.utils.ArrayUtils;
import net.akazukin.library.utils.StringUtils;
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
        final CommandInfo commandInfo = this.getClass().getAnnotation(CommandInfo.class);

        this.name = commandInfo.name();
        this.description = commandInfo.description();
        this.permission = commandInfo.permission();
        this.executor = commandInfo.executor();
        this.subCommands = this.getSubCommands();
    }

    public SubCommand[] getSubCommands() {
        return new SubCommand[]{};
    }

    public SubCommand getSubCommand(final String name) {
        return Arrays.stream(this.subCommands).filter(cmd -> name == null ? cmd.getName().equalsIgnoreCase("") :
                cmd.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public abstract void run(CommandSender sender, String... args);

    @Override
    public boolean handleEvents() {
        return this.toggle;
    }

    public final boolean hasPermission(final CommandSender sender) {
        return this.permission.isEmpty() || !(sender instanceof Player) || sender.hasPermission(this.permission);
    }

    public final boolean validExecutor(final CommandSender sender) {
        if (this.executor == CommandExecutor.CONSOLE && !(sender instanceof Player)) {
            return true;
        } else if (this.executor == CommandExecutor.PLAYER && sender instanceof Player) {
            return true;
        }
        return this.executor == CommandExecutor.BOTH;
    }

    public String[] getCompletion(final CommandSender sender, final org.bukkit.command.Command cmd,
                                  final String[] args, final String[] args2) {
        if (args2.length > 1) {
            if (args2[0] == null) return null;

            return Arrays.stream(this.subCommands)
                    .filter(s -> s.getName().toLowerCase().startsWith(args2[0].toLowerCase()))
                    .findFirst().get()
                    .getCompletion(sender, cmd, args,
                            ArrayUtils.copy(Arrays.asList(args2), 1, args2.length - 2).toArray(new String[0]));
        }
        return Arrays.stream(this.subCommands)
                .map(SubCommand::getName)
                .filter(s -> s.toLowerCase().startsWith(StringUtils.toStringOrEmpty(StringUtils.getIndex(args2, 0))))
                .sorted().toArray(String[]::new);
    }
}
