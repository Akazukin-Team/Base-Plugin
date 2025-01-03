package org.akazukin.library.manager;

import lombok.Getter;
import org.akazukin.event.EventTarget;
import org.akazukin.event.Listenable;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.event.events.PlayerLocationChangeEvent;
import org.akazukin.library.event.events.PlayerRotationEvent;
import org.akazukin.library.event.events.ServerTickEvent;
import org.akazukin.library.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        return this.getLastDamageTick(player.getUniqueId());
    }

    public long getLastDamageTick(final UUID player) {
        return this.lastDmgTick.containsKey(player) ? this.passedTime - this.lastDmgTick.get(player) : -1;
    }

    public long getLastRotatedTick(final Player player) {
        return this.getLastRotatedTick(player.getUniqueId());
    }

    public long getLastRotatedTick(final UUID player) {
        return this.lastRotTick.containsKey(player) ? this.passedTime - this.lastRotTick.get(player) : -1;
    }

    public long getLastPosTick(final Player player) {
        return this.getLastPosTick(player.getUniqueId());
    }

    public long getLastPosTick(final UUID player) {
        return this.lastLocTick.containsKey(player) ? this.passedTime - this.lastLocTick.get(player) : -1;
    }

    public long getLastMovedTick(final Player player) {
        return this.getLastMovedTick(player.getUniqueId());
    }

    public long getLastMovedTick(final UUID player) {
        return this.lastMoveTick.containsKey(player) ? this.passedTime - this.lastMoveTick.get(player) : -1;
    }

    public long getLastInteractTick(final Player player) {
        return this.getLastInteractTick(player.getUniqueId());
    }

    public long getLastInteractTick(final UUID player) {
        return this.lastInteractTick.containsKey(player) ? this.passedTime - this.lastInteractTick.get(player) : -1;
    }

    public void reset() {
        this.passedTime = 0L;
        this.lastDmgTick.clear();
        this.lastRotTick.clear();
        this.lastMoveTick.clear();
        this.lastInteractTick.clear();
    }

    @EventTarget(libraryPriority = 4)
    public void onServerTick(final ServerTickEvent event) {
        this.passedTime++;
    }

    @EventTarget(libraryPriority = 2)
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        this.lastDmgTick.put(event.getEntity().getUniqueId(), this.passedTime);
    }

    @EventTarget(libraryPriority = 2)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

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
                this.lastRotTick.put(event.getPlayer().getUniqueId(), this.passedTime);
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
                this.lastLocTick.put(event.getPlayer().getUniqueId(), this.passedTime);
                this.prevLoc.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
            }
        }

        this.lastMoveTick.put(event.getPlayer().getUniqueId(), this.passedTime);
    }

    @EventTarget(libraryPriority = 2)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        this.lastInteractTick.put(event.getPlayer().getUniqueId(), this.passedTime);
    }

    @EventTarget(libraryPriority = 2)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        PlayerUtils.save(LibraryPlugin.getPlugin().getCompat().getGameProfile(event.getPlayer()));
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
