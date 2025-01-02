package org.akazukin.library.event;

import org.akazukin.event.ICancellableEvent;
import org.akazukin.event.IEvent;

public class IEventHelper {
    public static boolean callAndCheck(final IEvent event) {
        if (event instanceof ICancellableEvent) {
            return !((ICancellableEvent) event).isCancelled();
        }
        return true;
    }
}