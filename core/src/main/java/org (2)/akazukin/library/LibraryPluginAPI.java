package org.akazukin.library;

import java.util.logging.Logger;
import org.akazukin.library.event.EventManager;
import org.akazukin.library.packetlistener.InjectionManager;
import org.akazukin.library.utils.MessageHelper;

public interface LibraryPluginAPI {
    Logger getLogManager();

    Object getConfigUtils();

    InjectionManager getInjectionManager();

    EventManager<?> getEventManager();

    MessageHelper getMessageHelper();
}
