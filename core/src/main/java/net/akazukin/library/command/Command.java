package net.akazukin.library.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.event.Listenable;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.ArrayUtils;
import net.akazukin.library.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Command implements Listenable {
    @Getter
    private final String name;
    @Getter
    private final String description;
    private final List<SubCommand> subCommands = new ArrayList<>();
    @Getter
    private final String permission;
    @Getter
    private final CommandExecutor executor;

    @Getter
    @Setter
    private boolean toggle = true;

    public Command() {
        final CommandInfo commandInfo = this.getClass().getAnnotation(CommandInfo.class);

        this.name = commandInfo.name();
        this.description = commandInfo.description();
        this.permission = commandInfo.permission();
        this.executor = commandInfo.executor();
    }

    public SubCommand[] getSubCommands() {
        return this.subCommands.toArray(new SubCommand[0]);
    }

    public void addSubCommands(final Class<? extends SubCommand>... subCommands) {
        this.addSubCommands(Arrays.stream(subCommands).map(c -> {
            try {
                return c.getConstructor().newInstance();
            } catch (final InstantiationException | IllegalAccessException | InvocationTargetException |
                           NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).toArray(SubCommand[]::new));
    }

    public void addSubCommands(final SubCommand... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
    }

    public void removeSubCommands(final SubCommand... subCommands) {
        this.subCommands.removeAll(Arrays.asList(subCommands));
    }

    @Override
    public boolean handleEvents() {
        return this.toggle;
    }

    public String[] getCompletion(final CommandSender sender, final org.bukkit.command.Command cmd,
                                  final String[] args, final String[] args2) {
        if (args2.length > 1) {
            return this.subCommands.parallelStream()
                    .filter(s -> s.getName().toLowerCase().startsWith(args2[0].toLowerCase()) && s.hasPermission(sender))
                    .findFirst()
                    .map(subCommand -> subCommand.getCompletion(sender, cmd, args,
                            ArrayUtils.copy(Arrays.asList(args2), 1, args2.length - 2)
                                    .toArray(new String[0])))
                    .orElse(null);
        }

        final String arg = StringUtils.toStringOrEmpty(ArrayUtils.getIndex(args2, 0)).toLowerCase();
        return this.subCommands.parallelStream()
                .filter(cmD -> cmD.getName().toLowerCase().startsWith(arg) &&
                        (cmD.getPermission().isEmpty() || sender.hasPermission(cmD.getPermission())))
                .map(SubCommand::getName)
                .sorted()
                .toArray(String[]::new);
    }

    public boolean runSubCommand(final CommandSender sender, final String[] args, final String[] args2) {
        final SubCommand cmd = this.getSubCommand(StringUtils.toStringOrEmpty(ArrayUtils.getIndex(args2, 0)));
        if (cmd == null) return false;
        else if (!cmd.validExecutor(sender)) {
            LibraryPlugin.MESSAGE_HELPER.consoleMessage(I18n.of("library.command.execute.mustBeBy" + (sender instanceof Player ? "Console" : "Player")));
        } else if (!cmd.hasPermission(sender)) {
            LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.message.requirePerm"));
        } else {
            cmd.run(sender, args, ArrayUtils.copy(Arrays.asList(args2), 1, args2.length - 2).toArray(new String[0]));
        }
        return true;
    }

    public SubCommand getSubCommand(final String name) {
        return this.subCommands.stream().filter(cmd -> name == null ? cmd.getName().equalsIgnoreCase("") :
                cmd.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public abstract void run(CommandSender sender, String[] args, String[] args2);

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
}
