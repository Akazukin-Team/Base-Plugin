package net.akazukin.library.event;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.event.events.PacketReceiveEvent;
import net.akazukin.library.event.events.PacketSendEvent;
import net.akazukin.library.event.events.ServerTickEvent;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class Events implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerTick(final ServerTickEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.HIGHEST);
    }

    private void callEvent(final Event event, final net.akazukin.library.event.EventPriority priority) {
        LibraryPlugin.EVENT_MANAGER.callEvent(event, priority);
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.HIGH);
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerListPing(final ServerListPingEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.HIGH);
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onPacketReceive(final PacketReceiveEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.NORMAL);
        final Packet pkt = LibraryPlugin.COMPAT.getWrappedPacket(event.getPacket());
    }

    @EventHandler
    public void onPackeSend(final PacketSendEvent event) {
        this.callEvent(event, net.akazukin.library.event.EventPriority.NORMAL);
        final Packet pkt = LibraryPlugin.COMPAT.getWrappedPacket(event.getPacket());
    }
}
