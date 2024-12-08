package org.akazukin.library.gui.screens.chat;

import lombok.Getter;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@Getter
@Deprecated
public class ChatGui extends GuiBase {
    protected boolean cancelChat;
    protected String result;

    public ChatGui(final Player player, final GuiBase prevGui, final boolean cancelChat) {
        super(player, prevGui);
        this.cancelChat = cancelChat;
    }

    public void onChat(final AsyncPlayerChatEvent event) {
        if (this.cancelChat) {
            event.setCancelled(true);
        }
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
