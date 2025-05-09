package org.akazukin.library.command.commands.akazukin;

import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.command.CommandExecutor;
import org.akazukin.library.command.CommandInfo;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.SubCommand;

@CommandInfo(name = "", description = "Show information", executor = CommandExecutor.BOTH)
public class NoArgsSubCommand extends SubCommand<ICmdSender> {
    @Override
    public void run(final ICmdSender sender, final String[] args, final String[] args2) {
        sender.sendMessage("§6§l" + LibraryPlugin.getPlugin().getName());
        sender.sendMessage("");
        sender.sendMessage("§aDevelop: " + "Akazukin organizations");
        sender.sendMessage("§aDevelopper: " + "Currypan1229");
        sender.sendMessage("§aURL: " + "https://github.com/Akazukin-Team/Base-Plugin");

        sender.sendMessage("");
        sender.sendMessage("§e§lLicense");
        sender.sendMessage("§c再頒布不可");
        sender.sendMessage("§c各種権利の帰属");
        sender.sendMessage("§c自己責任");
        sender.sendMessage("§c商用利用の不可");
        sender.sendMessage("§c学習目的外利用の不可");
        sender.sendMessage("§cソースコードの開示");
        sender.sendMessage("§cソースコードを使用した場合、該当プロジェクトにも上記ライセンスを適応");
    }
}
