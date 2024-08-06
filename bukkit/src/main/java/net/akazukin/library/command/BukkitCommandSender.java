package net.akazukin.library.command;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;

@AllArgsConstructor
public final class BukkitCommandSender implements ICmdSender {
    @Override
    public void sendMessage(final String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    @Override
    public CommandExecutor getExecutor() {
        return CommandExecutor.CONSOLE;
    }
}
