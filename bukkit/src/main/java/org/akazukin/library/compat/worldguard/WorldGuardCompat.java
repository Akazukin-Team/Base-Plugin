package org.akazukin.library.compat.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldGuardCompat {
    public static void createRegion(final String name, final Location loc, final Location loc2) {
        if (loc == null || loc2 == null || name == null)
            throw new IllegalArgumentException("location cannot be null");
        if (loc.getWorld() == null || loc2.getWorld() == null)
            throw new IllegalArgumentException("location of world cannot be null");

        if (!loc.getWorld().equals(loc2.getWorld()))
            throw new IllegalStateException("different worlds   " + loc.getWorld().getName() + "  " + loc2.getWorld().getName());


        final BlockVector3 min = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        final BlockVector3 max = BlockVector3.at(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
        final ProtectedRegion region = new ProtectedCuboidRegion(name, min, max);

        final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        rc.get(BukkitAdapter.adapt(loc.getWorld())).addRegion(region);
    }

    public static void addOwner(final World world, final String regionId, final UUID player) {
        final DefaultDomain owners = getRegion(world, regionId).getOwners();
        owners.addPlayer(player);
        getRegion(world, regionId).setOwners(owners);
    }

    public static ProtectedRegion getRegion(final World world,
                                            final String regionId) {
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(world)).getRegion(regionId);
    }

    public static void removeOwner(final World world, final String regionId, final UUID player) {
        final DefaultDomain owners = getRegion(world, regionId).getOwners();
        owners.removePlayer(player);
        getRegion(world, regionId).setOwners(owners);
    }

    public static void removeAllOwners(final World world, final String regionId) {
        final DefaultDomain owners = getRegion(world, regionId).getOwners();
        owners.removeAll();
        getRegion(world, regionId).setOwners(owners);
    }

    public static boolean isOwner(final World world, final String regionId, final UUID player) {
        final DefaultDomain owners = getRegion(world, regionId).getOwners();
        return owners.contains(player);
    }

    public static void addMember(final World world, final String regionId, final UUID player) {
        final DefaultDomain members = getRegion(world, regionId).getMembers();
        members.addPlayer(player);
        getRegion(world, regionId).setMembers(members);
    }

    public static void removeMember(final World world, final String regionId, final UUID player) {
        final DefaultDomain members = getRegion(world, regionId).getMembers();
        members.removePlayer(player);
        getRegion(world, regionId).setMembers(members);
    }

    public static void removeAllMembers(final World world, final String regionId) {
        final DefaultDomain members = getRegion(world, regionId).getMembers();
        members.removeAll();
        getRegion(world, regionId).setMembers(members);
    }

    public static boolean isMember(final World world, final String regionId, final UUID player) {
        final DefaultDomain members = getRegion(world, regionId).getMembers();
        return members.contains(player);
    }

    public static <K extends Flag<V>, V> void addFlag(final World world, final String regionId, final K key,
                                                      final V value) {
        getRegion(world, regionId).setFlag(key, value);
    }

    public static void addFlag(final World world, final String regionId, final int priority) {
        getRegion(world, regionId).setPriority(priority);
    }

    public static List<Set<ProtectedRegion>> removeRegion(final World world) {
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionManager rm = container.get(BukkitAdapter.adapt(world));
        return rm.getRegions().keySet().stream().map(id -> removeRegion(world, id)).collect(Collectors.toList());
    }

    public static Set<ProtectedRegion> removeRegion(final World world, final String regionId) {
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(world)).removeRegion(regionId);
    }

    public static boolean isInRegion(final Location loc, final String region) {
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionQuery query = container.createQuery();
        final ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
        for (final ProtectedRegion pr : set) {
            if (pr.getId().equals(region)) return true;
        }
        return false;
    }
}
