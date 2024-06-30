package net.akazukin.library.gui.screens.chat;

import java.util.UUID;
import java.util.function.Predicate;
import lombok.Getter;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.GuiBase;

@Getter
@Deprecated
public class ChatInputGui extends ChatGui {
    private final Predicate<String> isValid;

    public ChatInputGui(final UUID player, final GuiBase prevGui, final Predicate<String> isValid) {
        super(player, prevGui, true);
        this.isValid = isValid;
    }

    private void close(final String msg) {
        if (this.isValid.test(msg)) this.result = msg;
        GuiManager.singleton().setScreen(this.player, () -> this.prevGui);
    }
}
