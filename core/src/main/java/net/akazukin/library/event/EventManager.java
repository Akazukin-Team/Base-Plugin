package net.akazukin.library.event;

import net.akazukin.library.LibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class EventManager {
    private final Map<Class<? extends Event>, List<EventHook>> registry = new HashMap<>();

    public abstract void registerListeners();

    public void registerListeners(final Class<? extends Listenable>... listeners) {
        Arrays.stream(listeners).forEach(this::registerListener);
    }

    public void registerListeners(final Listenable... listeners) {
        Arrays.stream(listeners).forEach(this::registerListener);
    }

    public void registerListener(final Class<? extends Listenable> command) {
        try {
            registerListener(command.newInstance());
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
                if (!method.isAccessible())
                    method.setAccessible(true);

                final Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
                final EventTarget eventTarget = method.getAnnotation(EventTarget.class);
                final ArrayList<EventHook> invokableEventTargets = (ArrayList<EventHook>) registry.getOrDefault(eventClass, new ArrayList<>());

                invokableEventTargets.add(new EventHook(listener, method, eventTarget.priority(), eventTarget.bktPriority(), eventTarget.ignoreCondition()));
                invokableEventTargets.sort((obj1, obj2) -> obj2.getPriority() - obj1.getPriority());
                registry.put(eventClass, invokableEventTargets);
            }
        }
    }

    /**
     * Unregister listener
     *
     * @param listenable for unregister
     */
    public void unregisterListener(final Listenable listenable) {
        for (final Map.Entry<Class<? extends Event>, List<EventHook>> targets : registry.entrySet()) {
            targets.getValue().removeIf(eventClass -> eventClass.getEventClass() == listenable);

            registry.put(targets.getKey(), targets.getValue());
        }
    }

    /**
     * Call event to listeners
     *
     * @param event to call
     */
    public void callEvent(final Event event, final EventPriority priority) {
        registry.forEach((key, value) -> {
            //if (!key.isAssignableFrom(event.getClass())) return;
            if (!key.equals(event.getClass())) return;

            //value.sort((a, b) -> a.getPriority() > b.getPriority());
            value.stream().filter(eventHook -> (eventHook.getEventClass().handleEvents() || eventHook.isIgnoreCondition() || priority == eventHook.getBktPriority())).forEach(eventHook -> {
                try {
                    eventHook.getMethod().invoke(eventHook.getEventClass(), event);
                } catch (final Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        });
    }

    /**
     * Call event at bukkit
     *
     * @param event to call
     */
    public static void callEventToBukkit(final Event event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
    }
}
