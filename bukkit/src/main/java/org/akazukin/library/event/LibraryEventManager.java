package org.akazukin.library.event;

import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.manager.PlayerManager;
import org.akazukin.library.packetlistener.PacketManager;
import org.bukkit.event.Event;

public final class LibraryEventManager extends EventManager<Event> {
    @Override
    public void registerListeners() {
        LibraryPlugin.getPlugin().getLogManager().info("Registering event listeners");
        this.registerListeners(
                GuiManager.singleton(),
                new PacketManager(),
                PlayerManager.SINGLETON
        );
        LibraryPlugin.getPlugin().getLogManager().info("Successfully Registered event listeners");
    }
}
