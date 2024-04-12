package net.akazukin.library.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.EventPriority;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class EventHook {
    Listenable eventClass;
    Method method;
    int priority;
    EventPriority bktPriority;
    boolean ignoreCondition;
}
