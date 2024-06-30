package net.akazukin.library.gui.screens.chat;

import java.util.UUID;
import lombok.Getter;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.GuiBase;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@Getter
@Deprecated
public class ChatGui extends GuiBase {
    protected boolean cancelChat;
    protected String result;

    public ChatGui(final UUID player, final GuiBase prevGui, final boolean cancelChat) {
        super(player, prevGui);
        this.cancelChat = cancelChat;
    }

    public void onChat(final AsyncPlayerChatEvent event) {
        if (this.cancelChat) event.setCancelled(true);
        this.close(event.getMessage());
    }

    private void close(final String msg) {
        this.result = msg;
        GuiManager.singleton().setScreen(this.player, () -> this.prevGui);
    }

    public void onPlayerMove(final PlayerMoveEvent event) {
        this.close(null);
    }
}
