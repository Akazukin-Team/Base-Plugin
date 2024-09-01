package net.akazukin.library.event.events;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@ToString
@Getter
public class PlayerRotationEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final float prevYaw;
    private final float prevPitch;
    @Setter
    private boolean cancelled = false;

    public PlayerRotationEvent(final Player player, final float prevYaw, final float prevPitch) {
        super(false);
        this.player = player;
        this.prevYaw = prevYaw;
        this.prevPitch = prevPitch;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
