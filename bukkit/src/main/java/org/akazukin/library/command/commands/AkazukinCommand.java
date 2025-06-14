package org.akazukin.library.command.commands;

import org.akazukin.i18n.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.command.Command;
import org.akazukin.library.command.CommandInfo;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.SubCommand;
import org.akazukin.library.command.commands.akazukin.HelpSubCommand;
import org.akazukin.library.command.commands.akazukin.LanguageSubCommand;
import org.akazukin.library.utils.ArrayUtils;

@CommandInfo(name = "akazukin", description = "akazukin basically command")
public final class AkazukinCommand extends Command<ICmdSender> {

    public AkazukinCommand() {
        this.addSubCommands(
                new HelpSubCommand(),
                new LanguageSubCommand()
        );
    }

    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        final SubCommand<? super ICmdSender> subCmd = this.getSubCommand(ArrayUtils.getIndex(args, 0));
        if (!this.runSubCommand(sender, args, args2)) {
            LibraryPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("library.command.notFound"));
        }
    }
}
