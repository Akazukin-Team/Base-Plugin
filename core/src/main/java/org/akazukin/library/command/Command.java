package org.akazukin.library.command;

import lombok.Getter;
import lombok.Setter;
import org.akazukin.event.Listenable;
import org.akazukin.i18n.I18n;
import org.akazukin.library.LibraryPluginProvider;
import org.akazukin.library.utils.ArrayUtils;
import org.akazukin.library.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command<T extends ICmdSender> implements Listenable {
    @Getter
    private final String name;
    @Getter
    private final String description;
    private final List<SubCommand<? super T>> subCommands = new ArrayList<>();
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

    public SubCommand<? super T>[] getSubCommands() {
        return this.subCommands.toArray(new SubCommand[0]);
    }

    public void addSubCommands(final SubCommand<? super T>... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
    }

    public void removeSubCommands(final SubCommand<? super T>... subCommands) {
        this.subCommands.removeAll(Arrays.asList(subCommands));
    }

    @Override
    public boolean handleEvents() {
        return this.toggle;
    }

    public String[] getCompletion(final T sender, final String cmdName,
                                  final String[] args, final String[] args2) {
        if (args2.length > 1) {
            return this.subCommands.parallelStream()
                    .filter(s -> s.getName().toLowerCase().startsWith(args2[0].toLowerCase()) && s.hasPermission(sender))
                    .findFirst()
                    .map(subCommand -> subCommand.getCompletion(sender, cmdName, args,
                            org.akazukin.util.utils.ArrayUtils.copyOfRange(args2, 1, args2.length - 1)))
                    .orElse(null);
        }

        final String arg = StringUtils.toStringOrEmpty(ArrayUtils.getIndex(args2, 0)).toLowerCase();
        return this.subCommands.parallelStream()
                .filter(cmD -> cmD.getName().toLowerCase().startsWith(arg) && cmD.hasPermission(sender))
                .map(SubCommand::getName)
                .sorted()
                .toArray(String[]::new);
    }

    public boolean runSubCommand(final T sender, final String[] args, final String[] args2) {
        final SubCommand<? super T> cmd = this.getSubCommand(StringUtils.toStringOrEmpty(ArrayUtils.getIndex(args2, 0)));
        if (cmd == null) {
            return false;
        } else if (!cmd.validExecutor(sender)) {
            LibraryPluginProvider.getApi().getMessageHelper().sendMessage(sender,
                    I18n.of("library.command.execute.mustBeBy" +
                            (sender instanceof IPlayerCmdSender ? "Console" : "Player")));
        } else if (!cmd.hasPermission(sender)) {
            LibraryPluginProvider.getApi().getMessageHelper().sendMessage(sender,
                    I18n.of("library.message.requirePerm"));
        } else {
            cmd.run(sender, args,
                    args2.length > 1
                            ? org.akazukin.util.utils.ArrayUtils.copyOfRange(args2, 1, args2.length - 1)
                            : new String[0]);
        }
        return true;
    }

    public SubCommand<? super T> getSubCommand(final String name) {
        return this.subCommands.stream()
                .filter(cmd -> name == null ? cmd.getName().equalsIgnoreCase("") :
                        cmd.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public abstract void run(T sender, String[] args, String[] args2);

    public final boolean hasPermission(final T sender) {
        return this.permission.isEmpty() || !(sender instanceof IPlayerCmdSender) || ((IPlayerCmdSender) sender).hasPermission(this.permission);
    }

    public final boolean validExecutor(final T sender) {
        if (this.executor == CommandExecutor.CONSOLE && !(sender instanceof IPlayerCmdSender)) {
            return true;
        } else if (this.executor == CommandExecutor.PLAYER && sender instanceof IPlayerCmdSender) {
            return true;
        }
        return this.executor == CommandExecutor.BOTH;
    }
}
