package org.akazukin.library.event;

public class IEventHelper {
    public static boolean callAndCheck(final IEvent event) {
        if (event instanceof ICancellableEvent) {
            return !((ICancellableEvent) event).isCancelled();
        }
        return true;
    }
}