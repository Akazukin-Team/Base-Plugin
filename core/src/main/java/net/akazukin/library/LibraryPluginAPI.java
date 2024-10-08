package net.akazukin.library;

import java.util.logging.Logger;
import net.akazukin.library.event.EventManager;
import net.akazukin.library.packetlistener.InjectionManager;
import net.akazukin.library.utils.MessageHelper;

public interface LibraryPluginAPI {
    Logger getLogManager();

    Object getConfigUtils();

    InjectionManager getInjectionManager();

    EventManager<?> getEventManager();

    MessageHelper getMessageHelper();
}
