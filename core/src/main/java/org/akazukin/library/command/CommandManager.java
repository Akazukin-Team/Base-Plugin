package org.akazukin.library.command;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class CommandManager<T extends Command<?>> {
    private final List<T> commands = new ArrayList<>();

    public void registerCommands(final T... commands) {
        Arrays.stream(commands).forEach(this::registerCommand);
    }

    public void registerCommand(final T command) {
        this.commands.add(command);
    }

    public <U extends T> U getCommand(final Class<U> aClass) {
        return (U) this.commands.stream()
                .filter(cmd -> cmd.getClass().equals(aClass))
                .findFirst()
                .orElse(null);
    }

    public T getCommand(final String name) {
        return this.commands.stream()
                .filter(cmd ->
                        cmd.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
