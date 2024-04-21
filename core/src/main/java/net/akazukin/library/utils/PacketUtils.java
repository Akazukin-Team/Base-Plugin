package net.akazukin.library.utils;

import java.util.UUID;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketUtils {
    public static void sendPacket(final UUID player, final Packet packet) {
        sendPacket(Bukkit.getPlayer(player), packet);
    }

    public static void sendPacket(final Player player, final Packet packet) {
    }
}
