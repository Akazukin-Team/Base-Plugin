package org.akazukin.library.command.commands.akazukin;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.akazukin.i18n.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.command.CommandExecutor;
import org.akazukin.library.command.CommandInfo;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.IPlayerCmdSender;
import org.akazukin.library.command.SubCommand;
import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.entity.MUserEntity;
import org.akazukin.library.doma.repo.MUserRepo;
import org.akazukin.library.utils.ArrayUtils;

@CommandInfo(name = "language", description = "Change user's language", executor = CommandExecutor.PLAYER)
public class LanguageSubCommand extends SubCommand {
    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        if (args.length <= 1) {
            LibraryPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("library.command.language.enterLanguage"));
        } else if (LibraryPlugin.getPlugin().getConfigUtils().getConfig("config.yaml").getStringList("locales").stream().map(String::toLowerCase).noneMatch(locale -> locale.equals(ArrayUtils.getIndex(Arrays.stream(args).map(String::toLowerCase).collect(Collectors.toList()), 1)))) {
            LibraryPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("library.command.language.notFound"));
        } else {
            LibrarySQLConfig.singleton().getTransactionManager().required(() -> {
                MUserEntity entity = MUserRepo.selectById(((IPlayerCmdSender) sender).getUniqueId());
                if (entity == null) {
                    entity = new MUserEntity();
                    entity.setPlayerUuid(((IPlayerCmdSender) sender).getUniqueId());
                }
                entity.setLocale(ArrayUtils.getIndex(args, 1));
                MUserRepo.save(entity);
            });
            LibraryPlugin.getPlugin().getMessageHelper().sendMessage(sender, I18n.of("library.command.language.changed"));
        }
    }
}
