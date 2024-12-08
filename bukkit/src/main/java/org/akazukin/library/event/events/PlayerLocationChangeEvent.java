package org.akazukin.library.event.events;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@ToString
@Getter
public class PlayerLocationChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final double prevX;
    private final double prevY;
    private final double prevZ;
    @Setter
    private boolean cancelled = false;

    public PlayerLocationChangeEvent(final Player player, final double prevX, final double prevY, final double prevZ) {
        super(false);
        this.player = player;
        this.prevX = prevX;
        this.prevY = prevY;
        this.prevZ = prevZ;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
