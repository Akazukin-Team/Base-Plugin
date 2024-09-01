package net.akazukin.library.event;

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

public class Events extends IEvents<Event> implements Listener {
    public Events(final EventManager<Event> eventManager) {
        super(eventManager);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerTick(final ServerTickEvent event) {
        this.callEvent(ServerTickEvent.class, event, net.akazukin.library.event.EventPriority.HIGHEST);
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        this.callEvent(EntityDamageEvent.class, event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        this.callEvent(InventoryOpenEvent.class, event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        this.callEvent(InventoryOpenEvent.class, event, net.akazukin.library.event.EventPriority.HIGH);
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        this.callEvent(InventoryCloseEvent.class, event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.callEvent(PlayerQuitEvent.class, event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.callEvent(PlayerJoinEvent.class, event, net.akazukin.library.event.EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerListPing(final ServerListPingEvent event) {
        this.callEvent(ServerListPingEvent.class, event, net.akazukin.library.event.EventPriority.HIGH);
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.callEvent(PlayerMoveEvent.class, event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        this.callEvent(PlayerInteractEvent.class, event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        this.callEvent(AsyncPlayerChatEvent.class, event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onPacketReceive(final PacketReceiveEvent event) {
        this.callEvent(PacketReceiveEvent.class, event, net.akazukin.library.event.EventPriority.NORMAL);
    }

    @EventHandler
    public void onPackeSend(final PacketSendEvent event) {
        this.callEvent(PacketSendEvent.class, event, net.akazukin.library.event.EventPriority.NORMAL);
    }
}
