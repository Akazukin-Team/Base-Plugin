package net.akazukin.library.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventTarget {

    boolean ignoreCondition() default false;

    EventPriority bktPriority() default EventPriority.NORMAL;

    int priority() default 0;
}
