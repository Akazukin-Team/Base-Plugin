package org.akazukin.library.event;

public abstract class IEvents<E> {
    private final EventManager<E> eventManager;

    public IEvents(final EventManager<E> eventManager) {
        this.eventManager = eventManager;
    }

    protected <E2 extends E> void callEvent(final Class<? extends E2> clazz, final E2 event,
                                            final org.akazukin.library.event.EventPriority priority) {
        this.eventManager.callEvent(clazz, event, priority);
    }
}
