package net.akazukin.library.command.commands;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.command.Command;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.command.commands.akazukin.HelpSubCommand;
import net.akazukin.library.command.commands.akazukin.LanguageSubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.ArrayUtils;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "akazukin", description = "akazukin basically command")
public final class AkazukinCommand extends Command {

    public AkazukinCommand() {
        this.addSubCommands(
                HelpSubCommand.class,
                LanguageSubCommand.class
        );
    }

    @Override
    public void run(final CommandSender sender, final String[] args, final String[] args2) {
        final SubCommand subCmd = this.getSubCommand(ArrayUtils.getIndex(args, 0));
        if (!this.runSubCommand(sender, args, args2)) {
            LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.notFound"));
        }
    }
}
