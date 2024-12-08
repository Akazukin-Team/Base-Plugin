package org.akazukin.library.command;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.akazukin.library.exception.UnsupportedOperationYetException;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class BktCmdSender2 implements PlayerWrapper {
    private final Player player;

    @Override
    public String getDisplayName() {
        return this.player.getDisplayName();
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public void sendMessage(final String msg) {
        this.player.sendMessage(msg);
    }

    @Override
    public void kick(final String reason) {
        this.player.kickPlayer(reason);
    }

    @Override
    public void ban() {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public void banIPs() {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public String[] getIPs() {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public String getLastIP() {
        return this.player.getAddress().getHostName();
    }
}
