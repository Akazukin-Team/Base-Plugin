package org.akazukin.library.event;

import org.akazukin.event.EventManager;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.manager.PlayerManager;

public final class LibraryEventManager extends EventManager<Object> {
    LibraryPlugin plugin;

    public LibraryEventManager(final LibraryPlugin plugin) {
        super(Object.class);
        this.plugin = plugin;
    }

    public void registerListeners() {
        this.plugin.getLogManager().info("Registering event listeners");
        this.registerListeners(
                this.plugin.getGuiManager(),
                PlayerManager.SINGLETON
        );
        LibraryPlugin.getPlugin().getLogManager().info("Successfully Registered event listeners");
    }
}
