package net.akazukin.library.gui;

import lombok.Getter;

import java.util.UUID;
import java.util.function.Predicate;

@Getter
@Deprecated
public class ChatInputGui extends ChatGui {
    private final Predicate<String> isValid;

    public ChatInputGui(final UUID player, final GuiBase prevGui, final Predicate<String> isValid) {
        super(player, prevGui, true);
        this.isValid = isValid;
    }

    private void close(final String msg) {
        if (isValid.test(msg)) result = msg;
        GuiManager.singleton().setScreen(player, prevGui);
    }
}
