package org.akazukin.library;

import org.akazukin.event.EventManager;
import org.akazukin.library.utils.MessageHelper;

import java.util.logging.Logger;

public interface LibraryPluginAPI {
    Logger getLogManager();

    Object getConfigUtils();

    EventManager<?> getEventManager();

    MessageHelper getMessageHelper();
}
