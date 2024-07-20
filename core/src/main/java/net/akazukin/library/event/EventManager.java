package net.akazukin.library.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.akazukin.library.LibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public abstract class EventManager {
    private final Map<Class<? extends Event>, List<EventHook>> registry = new HashMap<>();

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
        for (final Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventTarget.class) && method.getParameterTypes().length == 1) {
                if (!method.isAccessible()) method.setAccessible(true);

                final Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
                final EventTarget eventTarget = method.getAnnotation(EventTarget.class);
                final List<EventHook> invokableEventTargets = this.registry.getOrDefault(eventClass, new ArrayList<>());

                invokableEventTargets.add(new EventHook(listener, method, eventTarget.priority(),
                        eventTarget.bktPriority(), eventTarget.ignoreCondition()));
                invokableEventTargets.sort(Comparator.comparing(EventHook::getPriority));
                this.registry.put(eventClass, invokableEventTargets);
            }
        }
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
        for (final Map.Entry<Class<? extends Event>, List<EventHook>> targets : this.registry.entrySet()) {
            targets.getValue().removeIf(eventClass -> eventClass.getEventClass() == listenable);

            this.registry.put(targets.getKey(), targets.getValue());
        }
    }

    /**
     * Call event to listeners
     *
     * @param event to call
     */
    public void callEvent(final Event event, final EventPriority priority) {
        this.registry.entrySet().stream().filter(e -> e.getKey().isAssignableFrom(event.getClass()))
                .forEach(e -> {
                    e.getValue().stream()
                            .filter(e2 -> priority == e2.getBktPriority() && (e2.getEventClass().handleEvents() || e2.isIgnoreCondition()))
                            .forEach(eventHook -> {
                                try {
                                    eventHook.getMethod().invoke(eventHook.getEventClass(), event);
                                } catch (final Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
                });

        /*this.registry.forEach((key, value) -> {
            //if (!key.isAssignableFrom(event.getClass())) return;
            if (!key.equals(event.getClass())) return;

            //value.sort((a, b) -> a.getPriority() > b.getPriority());
            value.stream()
                    .filter(eventHook -> (eventHook.getEventClass().handleEvents() || eventHook.isIgnoreCondition()
                    || priority == eventHook.getBktPriority()))
                    .forEach(eventHook -> {
                        try {
                            eventHook.getMethod().invoke(eventHook.getEventClass(), event);
                        } catch (final Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        });*/
    }
}
