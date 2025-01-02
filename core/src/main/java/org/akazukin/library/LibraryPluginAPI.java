package org.akazukin.library;

import org.akazukin.event.EventManager;
import org.akazukin.library.packetlistener.InjectionManager;
import org.akazukin.library.utils.MessageHelper;

import java.util.logging.Logger;

public interface LibraryPluginAPI {
    Logger getLogManager();

    Object getConfigUtils();

    InjectionManager getInjectionManager();

    EventManager<?> getEventManager();

    MessageHelper getMessageHelper();
}
