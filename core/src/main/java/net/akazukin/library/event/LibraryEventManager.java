package net.akazukin.library.event;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.grimac.GrimACEvents;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.packetlistener.PacketManager;

public final class LibraryEventManager extends EventManager {
    @Override
    public void registerListeners() {
        LibraryPlugin.getLogManager().info("Registering event listeners");
        registerListeners(
                GuiManager.singleton(),
                new PacketManager(),
                new GrimACEvents()
        );
        LibraryPlugin.getLogManager().info("Successfully Registered event listeners");
    }
}
