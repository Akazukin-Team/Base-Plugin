package org.akazukin.library.gui.screens.chat;

import lombok.Getter;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

@Getter
@Deprecated
public class ChatInputGui extends ChatGui {
    private final Predicate<String> isValid;

    public ChatInputGui(final Player player, final GuiBase prevGui, final Predicate<String> isValid) {
        super(player, prevGui, true);
        this.isValid = isValid;
    }

    private void close(final String msg) {
        if (this.isValid.test(msg)) {
            this.result = msg;
        }
        GuiManager.singleton().setScreen(this.player, () -> this.prevGui);
    }
}
