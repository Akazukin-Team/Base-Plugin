package net.akazukin.library.command.commands.akazukin;

import java.util.Arrays;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.command.Command;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.StringUtils;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "help", description = "Show list of commands and descriptions")
public class HelpSubCommand extends SubCommand {
    @Override
    public void run(final CommandSender sender, final String... args) {
        if (args.length == 1) {
            LibraryPlugin.COMMAND_MANAGER.getCommands().forEach(cmd ->
                    LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.help.commands." + cmd.getName())));
        } else {
            Command cmd = LibraryPlugin.COMMAND_MANAGER.getCommand(args[1]);
            if (cmd == null) {
                LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.help.notFound"));
                return;
            }

            final StringBuilder id = new StringBuilder("library.command.help.commands." + cmd.getName());
            for (int i = 2; i < Math.min(args.length, 10); i++) {
                cmd = cmd.getSubCommand(args[i]);
                if (cmd == null) {
                    LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.help.notFound"));
                    return;
                }
                id.append(".").append(cmd.getName());
            }

            LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of(id.toString()));
            final SubCommand[] subCmds = cmd.getSubCommands();
            Arrays.stream(subCmds).forEach(cmd_ ->
                    LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of((id + ((StringUtils.getLength(cmd_.getName()) > 0) ? "." + cmd_.getName() : "")))));
        }
    }
}
