package net.akazukin.library.event;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.manager.PlayerManager;
import net.akazukin.library.packetlistener.PacketManager;
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
