package net.akazukin.library.command.commands;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.command.Command;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.command.commands.akazukin.HelpSubCommand;
import net.akazukin.library.command.commands.akazukin.LanguageSubCommand;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.StringUtils;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "akazukin", description = "akazukin basically command")
public final class AkazukinCommand extends Command {

    @Override
    public void run(final CommandSender sender, final String... args) {
        final SubCommand subCmd = getSubCommand(StringUtils.getIndex(args, 0));
        if (subCmd == null) {
            LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.notFound"));
            return;
        }
        subCmd.run(sender, args);
    }

    @Override
    public SubCommand[] getSubCommands() {
        return new SubCommand[]{
                new HelpSubCommand(),
                new LanguageSubCommand()
        };
    }
}
