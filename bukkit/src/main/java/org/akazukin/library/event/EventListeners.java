package org.akazukin.library.event;

import org.akazukin.event.AEvents;
import org.akazukin.event.EventManager;
import org.akazukin.library.event.events.ServerTickEvent;
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

public class EventListeners extends AEvents<Object> implements Listener {
    public EventListeners(final EventManager<Object> eventManager) {
        super(eventManager);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerTick(final ServerTickEvent event) {
        this.callEvent(ServerTickEvent.class, event, EventPriority.HIGHEST.getSlot());
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        this.callEvent(EntityDamageEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        this.callEvent(InventoryOpenEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        this.callEvent(InventoryClickEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        this.callEvent(InventoryCloseEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.callEvent(PlayerQuitEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.callEvent(PlayerJoinEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerListPing(final ServerListPingEvent event) {
        this.callEvent(ServerListPingEvent.class, event, EventPriority.HIGH.getSlot());
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.callEvent(PlayerMoveEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        this.callEvent(PlayerInteractEvent.class, event, EventPriority.NORMAL.getSlot());
    }

    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        this.callEvent(AsyncPlayerChatEvent.class, event, EventPriority.NORMAL.getSlot());
    }
}
