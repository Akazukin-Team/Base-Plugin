package org.akazukin.library.command.commands.akazukin;

import org.akazukin.i18n.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.command.Command;
import org.akazukin.library.command.CommandInfo;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.SubCommand;
import org.akazukin.library.utils.ArrayUtils;
import org.akazukin.util.utils.ListUtils;

import java.util.Arrays;

@CommandInfo(name = "help", description = "Show list of commands and descriptions")
public class HelpSubCommand extends SubCommand {
    @Override
    public String[] getCompletion(final ICmdSender sender, final String cmdName,
                                  final String[] args, final String[] args2) {

        if (args2.length <= 1) {
            return LibraryPlugin.getPlugin().commandManager.getCommands().stream()
                    .map(Command::getName)
                    .filter(s -> s.toLowerCase().startsWith(org.akazukin.library.utils.StringUtils.toStringOrEmpty(ArrayUtils.getIndex(args2,
                            0)).toLowerCase()))
                    .toArray(String[]::new);
        } else {
            Command cmD = LibraryPlugin.getPlugin().commandManager.getCommand(args2[0]);
            if (cmD == null) {
                return null;
            }

            int lastIndex = 0;
            for (int i = 1; i < Math.min(args2.length - 1, 10); i++) {
                cmD = cmD.getSubCommand(args2[i]);
                lastIndex = i;
                if (cmD == null) {
                    return null;
                }
            }

            return cmD.getCompletion(sender, cmdName, args,
                    ListUtils.copy(Arrays.asList(args2), 1, args2.length - 2 - lastIndex).toArray(new String[0]));
        }
    }

    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        if (args.length == 1) {
            LibraryPlugin.getPlugin().commandManager.getCommands().forEach(cmd ->
                    LibraryPlugin.getPlugin().getMessageHelper().sendMessage(sender,
                            I18n.of("library.command.help.commands." + cmd.getName())));
        } else {
            Command cmd = LibraryPlugin.getPlugin().commandManager.getCommand(args[1]);
            if (cmd == null) {
                LibraryPlugin.getPlugin().getMessageHelper().sendMessage(sender,
                        I18n.of("library.command.help.notFound"));
                return;
            }

            final StringBuilder id = new StringBuilder("library.command.help.commands." + cmd.getName());
            for (int i = 2; i < Math.min(args.length, 10); i++) {
                cmd = cmd.getSubCommand(args[i]);
                if (cmd == null) {
                    LibraryPlugin.getPlugin().getMessageHelper().sendMessage(sender,
                            I18n.of("library.command.help.notFound"));
                    return;
                }
                id.append(".").append(cmd.getName());
            }

            LibraryPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of(id.toString()));
            final SubCommand[] subCmds = cmd.getSubCommands();
            Arrays.stream(subCmds).forEach(cmd_ ->
                    LibraryPlugin.getPlugin().getMessageHelper().sendMessage(sender,
                            I18n.of((id + ((org.akazukin.util.utils.StringUtils.getLength(cmd_.getName()) > 0) ? "." + cmd_.getName() : "")))));
        }
    }
}
