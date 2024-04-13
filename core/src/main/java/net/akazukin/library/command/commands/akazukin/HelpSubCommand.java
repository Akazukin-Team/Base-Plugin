package net.akazukin.library.command.commands.akazukin;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.i18n.I18n;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "help", description = "Show list of commands and descriptions")
public class HelpSubCommand extends SubCommand {
    @Override
    public void run(final CommandSender sender, final String... args) {
        LibraryPlugin.COMMAND_MANAGER.getCommands().forEach(cmd ->
                LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.help." + cmd.getName())));
    }
}
