package org.akazukin.library.event;

import org.akazukin.event.EventManager;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.manager.PlayerManager;
import org.akazukin.library.packetlistener.PacketManager;
import org.bukkit.event.Event;

public final class LibraryEventManager extends EventManager<Event> {
    LibraryPlugin plugin;

    public LibraryEventManager(final LibraryPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerListeners() {
        this.plugin.getLogManager().info("Registering event listeners");
        this.registerListeners(
                this.plugin.getGuiManager(),
                new PacketManager(),
                PlayerManager.SINGLETON
        );
        LibraryPlugin.getPlugin().getLogManager().info("Successfully Registered event listeners");
    }
}
