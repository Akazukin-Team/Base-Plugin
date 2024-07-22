package net.akazukin.library.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import net.akazukin.library.event.events.PlayerLocationChangeEvent;
import net.akazukin.library.event.events.PlayerRotationEvent;
import net.akazukin.library.event.events.ServerTickEvent;
import net.akazukin.library.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@SuppressWarnings("unused")
public final class PlayerManager implements Listenable {
    public static final PlayerManager SINGLETON = new PlayerManager();
    public final Map<UUID, Location> prevRot = new HashMap<>();
    public final Map<UUID, Location> prevLoc = new HashMap<>();
    private final Map<UUID, Long> lastDmgTick = new HashMap<>();
    private final Map<UUID, Long> lastRotTick = new HashMap<>();
    private final Map<UUID, Long> lastMoveTick = new HashMap<>();
    private final Map<UUID, Long> lastLocTick = new HashMap<>();
    private final Map<UUID, Long> lastInteractTick = new HashMap<>();
    @Getter
    private long passedTime = 0L;

    public long getLastDamageTick(final Player player) {
        return getLastDamageTick(player.getUniqueId());
    }

    public long getLastDamageTick(final UUID player) {
        return lastDmgTick.containsKey(player) ? passedTime - lastDmgTick.get(player) : -1;
    }

    public long getLastRotatedTick(final Player player) {
        return getLastRotatedTick(player.getUniqueId());
    }

    public long getLastRotatedTick(final UUID player) {
        return lastRotTick.containsKey(player) ? passedTime - lastRotTick.get(player) : -1;
    }

    public long getLastPosTick(final Player player) {
        return getLastPosTick(player.getUniqueId());
    }

    public long getLastPosTick(final UUID player) {
        return lastLocTick.containsKey(player) ? passedTime - lastLocTick.get(player) : -1;
    }

    public long getLastMovedTick(final Player player) {
        return getLastMovedTick(player.getUniqueId());
    }

    public long getLastMovedTick(final UUID player) {
        return lastMoveTick.containsKey(player) ? passedTime - lastMoveTick.get(player) : -1;
    }

    public long getLastInteractTick(final Player player) {
        return getLastInteractTick(player.getUniqueId());
    }

    public long getLastInteractTick(final UUID player) {
        return lastInteractTick.containsKey(player) ? passedTime - lastInteractTick.get(player) : -1;
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
    }

    @EventTarget
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;

        lastDmgTick.put(event.getEntity().getUniqueId(), passedTime);
    }

    @EventTarget
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        final Location prevRot = this.prevRot.getOrDefault(event.getPlayer().getUniqueId(),
                event.getPlayer().getLocation());
        if (!this.prevRot.containsKey(event.getPlayer().getUniqueId()) ||
                event.getPlayer().getLocation().getYaw() != prevRot.getYaw() ||
                event.getPlayer().getLocation().getPitch() != prevRot.getPitch()) {
            final PlayerRotationEvent rotEvent = new PlayerRotationEvent(event.getPlayer(), prevRot.getYaw(),
                    prevRot.getPitch());
            Bukkit.getPluginManager().callEvent(rotEvent);
            if (rotEvent.isCancelled()) {
                event.getPlayer().getLocation().setYaw(prevRot.getYaw());
                event.getPlayer().getLocation().setPitch(prevRot.getPitch());
            } else {
                lastRotTick.put(event.getPlayer().getUniqueId(), passedTime);
                this.prevRot.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
            }
        }

        final Location prevLoc = this.prevLoc.getOrDefault(event.getPlayer().getUniqueId(),
                event.getPlayer().getLocation());
        if (!this.prevLoc.containsKey(event.getPlayer().getUniqueId()) ||
                event.getPlayer().getLocation().getX() != prevLoc.getX() ||
                event.getPlayer().getLocation().getY() != prevLoc.getY() ||
                event.getPlayer().getLocation().getZ() != prevLoc.getZ()) {
            final PlayerLocationChangeEvent rotEvent = new PlayerLocationChangeEvent(event.getPlayer(),
                    prevLoc.getX(), prevLoc.getY(), prevLoc.getZ());
            Bukkit.getPluginManager().callEvent(rotEvent);
            if (rotEvent.isCancelled()) {
                event.getPlayer().getLocation().setYaw(prevLoc.getYaw());
                event.getPlayer().getLocation().setPitch(prevLoc.getPitch());
            } else {
                lastLocTick.put(event.getPlayer().getUniqueId(), passedTime);
                this.prevLoc.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
            }
        }

        lastMoveTick.put(event.getPlayer().getUniqueId(), passedTime);
    }

    @EventTarget
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        lastInteractTick.put(event.getPlayer().getUniqueId(), passedTime);
    }

    @EventTarget
    public void onPlayerJoin(final PlayerJoinEvent event) {
        PlayerUtils.save(LibraryPlugin.COMPAT.getGameProfile(event.getPlayer()));
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
