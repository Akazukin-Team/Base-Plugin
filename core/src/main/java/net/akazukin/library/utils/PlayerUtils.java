package net.akazukin.library.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;

public class PlayerUtils {
    public static Player getPlayerFromAddress(final InetSocketAddress addr) {
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getAddress().equals(addr)) return player;
        }
        return null;
    }
}
