package net.akazukin.library.event.events;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.akazukin.library.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@ToString
@Getter
public class PlayerLocationChangeEvent extends Event implements Cancellable {
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
}
