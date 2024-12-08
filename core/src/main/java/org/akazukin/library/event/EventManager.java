package org.akazukin.library.event;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.akazukin.library.LibraryPluginProvider;

public abstract class EventManager<E> {
    private final Map<Class<? extends E>, List<EventHook>> registry = new ConcurrentHashMap<>();

    public abstract void registerListeners();

    public void registerListeners(final Class<? extends Listenable>... listeners) {
        Arrays.stream(listeners).forEach(this::registerListener);
    }

    public void registerListener(final Class<? extends Listenable> command) {
        try {
            this.registerListener(command.newInstance());
        } catch (final InstantiationException | IllegalAccessException e) {
            LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Register [listener]
     */
    public void registerListener(final Listenable listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(EventTarget.class) && m.getParameterTypes().length == 1)
                .peek(m -> {
                    if (!m.isAccessible()) {
                        m.setAccessible(true);
                    }
                })
                .forEach(m -> {
                    final Class<? extends E> eventClass = (Class<? extends E>) m.getParameterTypes()[0];
                    final EventTarget eventTarget = m.getAnnotation(EventTarget.class);
                    if (!this.registry.containsKey(eventClass)) {
                        this.registry.put(eventClass, new CopyOnWriteArrayList<>());
                    }
                    final List<EventHook> invokableEventTargets =
                            this.registry.get(eventClass);

                    invokableEventTargets.add(new EventHook(listener, m, eventTarget.priority(),
                            eventTarget.bktPriority(), eventTarget.ignoreCondition(),
                            eventTarget.ignoreSuperClasses()));
                    invokableEventTargets.sort(Comparator.comparing(EventHook::getPriority));
                });
    }

    public void registerListeners(final Listenable... listeners) {
        Arrays.stream(listeners).forEach(this::registerListener);
    }

    /**
     * Unregister listener
     *
     * @param listenable for unregister
     */
    public void unregisterListener(final Listenable listenable) {
        this.registry.forEach((key, value) -> {
            value.removeIf(eventClass -> eventClass.getEventClass() == listenable);

            this.registry.put(key, value);
        });
    }

    /**
     * Call event to listeners
     *
     * @param event to call
     */
    public void callEvent(final Class<? extends E> clazz, final E event, final EventPriority priority) {
        this.registry.entrySet().stream()
                .filter(e -> e.getKey().isAssignableFrom(event.getClass()))
                .forEach(e -> e.getValue().stream()
                        .filter(e2 -> priority == e2.getBktPriority() && (e2.getEventClass().handleEvents() || e2.isIgnoreCondition()) && (!e2.isIgnoreSuperClasses() || clazz.equals(e.getKey())))
                        .forEach(hook -> {
                            try {
                                hook.getMethod().invoke(hook.getEventClass(), event);
                            } catch (final Throwable t) {
                                LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE,
                                        "An error occurred while processing the EventFlag, " + event.getClass().getSimpleName() + ":" + priority.name(), t);
                            }
                        }));
    }
}
