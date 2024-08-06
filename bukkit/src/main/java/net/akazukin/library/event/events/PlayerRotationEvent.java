package net.akazukin.library.event.events;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.akazukin.library.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@ToString
@Getter
public class PlayerRotationEvent extends Event implements Cancellable {
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
}
