package net.akazukin.library.command.commands.akazukin;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.command.CommandExecutor;
import net.akazukin.library.command.CommandInfo;
import net.akazukin.library.command.SubCommand;
import net.akazukin.library.doma.LibrarySQLConfig;
import net.akazukin.library.doma.entity.MUserEntity;
import net.akazukin.library.doma.repo.MUserRepo;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

@CommandInfo(name = "language", description = "Change user's language", executor = CommandExecutor.PLAYER)
public class LanguageSubCommand extends SubCommand {
    @Override
    public void run(final CommandSender sender, final String... args) {
        if (args.length <= 1) {
            LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.language.enterLanguage"));
        } else if (LibraryPlugin.CONFIG_UTILS.getConfig("config.yaml").getStringList("locales").stream().map(String::toLowerCase).noneMatch(locale -> locale.equals(StringUtils.getIndex(Arrays.stream(args).map(String::toLowerCase).collect(Collectors.toList()), 1)))) {
            LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.language.notFound"));
        } else {
            LibrarySQLConfig.singleton().getTransactionManager().required(() -> {
                MUserEntity entity = MUserRepo.selectById(((Player) sender).getUniqueId());
                if (entity == null) {
                    entity = new MUserEntity();
                    entity.setPlayerUuid(((Player) sender).getUniqueId());
                }
                entity.setLocale(StringUtils.getIndex(args, 1));
                MUserRepo.save(entity);
            });
            LibraryPlugin.MESSAGE_HELPER.sendMessage(sender, I18n.of("library.command.language.changed"));
        }
    }
}
