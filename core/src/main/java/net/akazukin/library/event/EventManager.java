package net.akazukin.library.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import net.akazukin.library.LibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public abstract class EventManager {
    private final Map<Class<? extends Event>, List<EventHook>> registry = new ConcurrentHashMap<>();

    /**
     * Call event at bukkit
     *
     * @param event to call
     */
    public static void callEventToBukkit(final Event event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public abstract void registerListeners();

    public void registerListeners(final Class<? extends Listenable>... listeners) {
        Arrays.stream(listeners).forEach(this::registerListener);
    }

    public void registerListener(final Class<? extends Listenable> command) {
        try {
            this.registerListener(command.newInstance());
        } catch (final InstantiationException | IllegalAccessException e) {
            LibraryPlugin.getLogManager().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Register [listener]
     */
    public void registerListener(final Listenable listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(EventTarget.class) && m.getParameterTypes().length == 1)
                .forEach(m -> {
                    if (!m.isAccessible()) m.setAccessible(true);

                    final Class<? extends Event> eventClass = (Class<? extends Event>) m.getParameterTypes()[0];
                    final EventTarget eventTarget = m.getAnnotation(EventTarget.class);
                    final List<EventHook> invokableEventTargets =
                            this.registry.getOrDefault(eventClass, new ArrayList<>());

                    invokableEventTargets.add(new EventHook(listener, m, eventTarget.priority(),
                            eventTarget.bktPriority(), eventTarget.ignoreCondition()));
                    invokableEventTargets.sort(Comparator.comparing(EventHook::getPriority));
                    this.registry.put(eventClass, invokableEventTargets);
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
    public void callEvent(final Event event, final EventPriority priority) {
        this.registry.entrySet().stream()
                .filter(e -> e.getKey().isAssignableFrom(event.getClass()))
                .forEach(e -> e.getValue().stream()
                        .filter(e2 -> priority == e2.getBktPriority() && (e2.getEventClass().handleEvents() || e2.isIgnoreCondition()))
                        .forEach(hook -> {
                            try {
                                hook.getMethod().invoke(hook.getEventClass(), event);
                            } catch (final Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }));
    }
}
