package net.akazukin.library.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CommandInfo {
    String name();

    String description();

    String permission() default "";

    CommandExecutor executor() default CommandExecutor.BOTH;
}
