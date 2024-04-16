package net.akazukin.library.manager;

import lombok.Getter;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import net.akazukin.library.event.events.PlayerRotationEvent;
import net.akazukin.library.event.events.ServerTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerManager implements Listenable {
    public static final PlayerManager SINGLETON = new PlayerManager();
    public final Map<UUID, Location> prevLoc = new HashMap<>();
    private final Map<UUID, Long> lastDmgTick = new HashMap<>();
    private final Map<UUID, Long> lastRotTick = new HashMap<>();
    private final Map<UUID, Long> lastMoveTick = new HashMap<>();
    private final Map<UUID, Long> lastInteractTick = new HashMap<>();
    @Getter
    private long passedTime = 0L;

    public long getLastDamageTick(final UUID player) {
        return passedTime - lastDmgTick.getOrDefault(player, -1L);
    }

    public long getLastRotatedTick(final UUID player) {
        return passedTime - lastRotTick.getOrDefault(player, -1L);
    }

    public long getLastMovedTick(final UUID player) {
        return passedTime - lastMoveTick.getOrDefault(player, -1L);
    }

    public long getLastInteractTick(final UUID player) {
        return passedTime - lastInteractTick.getOrDefault(player, -1L);
    }

    public void reset() {
        passedTime = 0L;
        lastDmgTick.clear();
        lastRotTick.clear();
        lastMoveTick.clear();
        lastInteractTick.clear();
    }

    @EventTarget(bktPriority = EventPriority.HIGHEST)
    public void onServerTick(final ServerTickEvent event) {
        passedTime++;

        for (final Player p : Bukkit.getOnlinePlayers()) {
            final Location prevLoc = this.prevLoc.get(p.getUniqueId());
            if (p.getLocation().getYaw() != prevLoc.getYaw() ||
                    p.getLocation().getPitch() != prevLoc.getPitch()) {
                final PlayerRotationEvent rotEvent = new PlayerRotationEvent(p, prevLoc.getYaw(), prevLoc.getPitch());
                Bukkit.getPluginManager().callEvent(rotEvent);
                if (rotEvent.isCancelled()) {
                    p.getLocation().setYaw(prevLoc.getYaw());
                    p.getLocation().setPitch(prevLoc.getPitch());
                }
            }
            this.prevLoc.put(p.getUniqueId(), p.getLocation());
        }
    }

    @EventTarget
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;

        lastDmgTick.put(event.getEntity().getUniqueId(), passedTime);
    }

    @EventTarget
    public void onPlayerRotation(final PlayerRotationEvent event) {
        if (event.isCancelled()) return;
        lastRotTick.put(event.getPlayer().getUniqueId(), passedTime);
    }

    @EventTarget
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        lastMoveTick.put(event.getPlayer().getUniqueId(), passedTime);
    }

    @EventTarget
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        lastInteractTick.put(event.getPlayer().getUniqueId(), passedTime);
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
