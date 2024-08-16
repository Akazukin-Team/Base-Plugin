package net.akazukin.library.event;

import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventHook {
    Listenable eventClass;
    Method method;
    int priority;
    EventPriority bktPriority;
    boolean ignoreCondition;
    boolean ignoreSuperClasses;
}
