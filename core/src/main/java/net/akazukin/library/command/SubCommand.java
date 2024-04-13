package net.akazukin.library.command;

import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.event.Listenable;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

@Getter
public abstract class SubCommand implements Listenable {
    private final String name;
    private final String description;
    private final SubCommand[] subCommands;

    @Setter
    private boolean toggle = true;

    public SubCommand() {
        final CommandInfo commandInfo = getClass().getAnnotation(CommandInfo.class);

        name = commandInfo.name();
        description = commandInfo.description();
        subCommands = getSubCommands();
    }

    public SubCommand getSubCommand(final String name) {
        return Arrays.stream(subCommands).filter(subCommand -> name == null ? subCommand.getName().equalsIgnoreCase("") : subCommand.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public abstract void run(CommandSender sender, String... args);

    @Override
    public boolean handleEvents() {
        return toggle;
    }
}
