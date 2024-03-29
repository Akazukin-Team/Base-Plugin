package net.akazukin.library.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class EventHook {
    Listenable eventClass;
    Method method;
    int priority;
    boolean ignoreCondition;
}
