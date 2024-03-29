package net.akazukin.library.gui;

import lombok.Getter;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

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
        if (cancelChat) event.setCancelled(true);
        close(event.getMessage());
    }

    private void close(final String msg) {
        result = msg;
        GuiManager.singleton().setScreen(player, prevGui);
    }

    public void onPlayerMove(final PlayerMoveEvent event) {
        close(null);
    }
}
