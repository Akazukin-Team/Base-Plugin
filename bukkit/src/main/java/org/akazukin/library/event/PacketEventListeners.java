package org.akazukin.library.event;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import org.akazukin.event.EventManager;
import org.akazukin.library.event.events.ServerTickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PacketEventListeners extends PacketListenerAbstract implements Listener {
    private final EventManager<Object> eventManager;

    public PacketEventListeners(final EventManager<Object> eventManager) {
        this.eventManager = eventManager;
    }

    protected <E> void callEvent(final Class<E> clazz, final E event) {
        this.callEvent(clazz, event, 0);
    }

    protected <E> void callEvent(final Class<E> clazz, final E event, final int priority) {
        this.eventManager.callEvent(clazz, event, priority);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerTick(final ServerTickEvent event) {
        this.callEvent(ServerTickEvent.class, event, EventPriority.HIGHEST.getSlot());
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        this.callEvent(PacketReceiveEvent.class, event, 2);
    }
}
