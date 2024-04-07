package net.akazukin.library.event;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.event.events.PacketReceiveEvent;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class Events implements Listener {
    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onServerListPing(final ServerListPingEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onSignChange(final SignChangeEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onPacketReceive(final PacketReceiveEvent event) {
        callEvent(event);
        final Packet pkt = LibraryPlugin.COMPAT.getWrappedPacket(event.getPacket());
    }

    private void callEvent(final Event event) {
        LibraryPlugin.EVENT_MANAGER.callEvent(event);
    }
}
