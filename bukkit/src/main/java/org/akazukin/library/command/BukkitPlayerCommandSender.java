package org.akazukin.library.command;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
public final class BukkitPlayerCommandSender implements IPlayerCmdSender {
    public final Player sender;

    @Override
    public void sendMessage(final String msg) {
        this.sender.sendMessage(msg);
    }

    @Override
    public CommandExecutor getExecutor() {
        return CommandExecutor.PLAYER;
    }

    @Override
    public String getDisplayName() {
        return this.sender.getDisplayName();
    }

    @Override
    public String getName() {
        return this.sender.getName();
    }

    @Override
    public UUID getUniqueId() {
        return this.sender.getUniqueId();
    }

    @Override
    public boolean hasPermission(final String perm) {
        return this.sender.hasPermission(perm);
    }
}
