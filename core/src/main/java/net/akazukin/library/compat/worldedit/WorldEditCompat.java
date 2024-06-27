package net.akazukin.library.compat.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public class WorldEditCompat {
    public static void fill(final Location loc, final Location loc2, final BlockData blockData) {
        fill(loc, loc2, new ChancePattern(blockData, 1));
    }

    public static void fill(final Location loc, final Location loc2, final ChancePattern... chancePattern) {
        if (loc == null || loc2 == null)
            throw new IllegalArgumentException("location cannot be null");
        if (loc.getWorld() == null || loc2.getWorld() == null)
            throw new IllegalArgumentException("location of world cannot be null");

        if (!loc.getWorld().equals(loc2.getWorld()))
            throw new IllegalStateException("different worlds   " + loc.getWorld().getName() + "  " + loc2.getWorld().getName());

        final World world_ = BukkitAdapter.adapt(loc.getWorld());
        final BlockVector3 vec = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        final BlockVector3 vec2 = BlockVector3.at(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());

        final CuboidRegion selection = new CuboidRegion(world_, vec, vec2);
        try (final EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world_,
                -1)) {
            final RandomPattern pat = new RandomPattern();
            for (final ChancePattern pattern : chancePattern) {
                pat.add(BukkitAdapter.adapt(pattern.getBlockData()), pattern.getChance());
            }
            editSession.setBlocks(selection, pat);
            editSession.commit();
        } catch (final MaxChangedBlocksException ex) {
            ex.printStackTrace();
        }
    }

    public static Clipboard load(final File file) {
        final ClipboardFormat format = ClipboardFormats.findByFile(file);

        try (final FileInputStream fis = new FileInputStream(file)) {
            try (final ClipboardReader reader = format.getReader(fis)) {
                return reader.read();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void paste(final Clipboard clipboard, final Location loc) {
        try (final EditSession editSession =
                     WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(loc.getWorld()), -1)) {
            final Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).ignoreAirBlocks(true).build();
            try {
                Operations.complete(operation);
            } catch (final WorldEditException e) {
                e.printStackTrace();
            }
        }
    }


    public static BlockArrayClipboard copy(final Location loc, final Location loc2) {
        if (loc == null || loc2 == null)
            throw new IllegalArgumentException("location cannot be null");
        if (loc.getWorld() == null || loc2.getWorld() == null)
            throw new IllegalArgumentException("location of world cannot be null");

        if (!loc.getWorld().equals(loc2.getWorld()))
            throw new IllegalStateException("different worlds   " + loc.getWorld().getName() + "  " + loc2.getWorld().getName());


        final World world = BukkitAdapter.adapt(loc.getWorld());
        final BlockVector3 vec = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        final BlockVector3 vec2 = BlockVector3.at(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());

        final CuboidRegion region = new CuboidRegion(vec, vec2);
        final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        final ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(world, region, clipboard,
                region.getMinimumPoint());

        try {
            Operations.complete(forwardExtentCopy);
        } catch (final WorldEditException e) {
            e.printStackTrace();
        }

        return clipboard;
    }

    public static void save(final File file, final Clipboard clipboard) {
        try (final FileOutputStream fos = new FileOutputStream(file)) {
            try (final ClipboardWriter writer = BuiltInClipboardFormat.MCEDIT_SCHEMATIC.getWriter(fos)) {
                writer.write(clipboard);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean regenerateChunk(final Location loc, final Location loc2) {
        final World world_ = BukkitAdapter.adapt(loc.getWorld());
        try (final EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world_,
                -1)) {
            final BlockVector3 vec = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            final BlockVector3 vec2 = BlockVector3.at(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
            final CuboidRegion region = new CuboidRegion(vec, vec2);

            return world_.regenerate(region, editSession);
        }
    }
}
