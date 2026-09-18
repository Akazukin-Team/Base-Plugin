package org.akazukin.library.utils;

import org.akazukin.library.compat.worldguard.WorldGuardCompat;
import org.akazukin.library.world.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.function.Supplier;

public class WorldUtils {
    public static World getOrLoadWorld(@NotNull final WorldData worldData) {
        return getOrLoadWorld(worldData, () -> Bukkit.createWorld(WorldCreator.name(worldData.getName())));
    }

    public static World getOrLoadWorld(@NotNull final WorldData worldData, @NotNull final Supplier<World> creator) {
        final World w = getWorld(worldData);
        if (w != null) {
            return w;
        }

        return creator.get();
    }

    public static World getWorld(@NotNull final WorldData worldData) {
        return worldData.getUid() != null ? Bukkit.getWorld(worldData.getUid()) :
                (worldData.getName() != null ? Bukkit.getWorld(worldData.getName()) : null);
    }

    public static boolean isWorldLoaded(@NotNull final WorldData worldData) {
        return getWorld(worldData) != null;
    }

    public static boolean deleteWorld(@NotNull final World world) {
        return deleteWorld(new WorldData(world.getUID(), world.getName()));
    }

    public static boolean deleteWorld(@NotNull final WorldData worldData) {
        if (!worldData.isValid()) {
            throw new IllegalArgumentException("World data is invalid");
        }

        final World w = getWorld(worldData);
        final File dir;
        if (w != null) {
            Bukkit.unloadWorld(w, false);
            dir = w.getWorldFolder();
            if (PluginUtils.isEnabled("worldguard")) {
                WorldGuardCompat.removeRegion(w);
            }
        } else {
            dir = new File(Bukkit.getWorldContainer(), worldData.getName());
        }

        return FileUtils.delete(dir);
    }

    /**
     * Chunk 座標 (x, z) を 1 つの long にパッキングします。
     * [32bit: ChunkX] [32bit: ChunkZ]
     */
    public static long packChunkKey(final int chunkX, final int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    /**
     * Chunk Key (long) から ChunkX を抽出します。
     */
    public static int unpackChunkX(final long chunkKey) {
        return (int) (chunkKey >> 32);
    }

    /**
     * Chunk Key (long) から ChunkZ を抽出します。
     */
    public static int unpackChunkZ(final long chunkKey) {
        return (int) chunkKey;
    }


    // =========================================================================
    // 2. チャンクセクション内 ローカル 3D 座標 (0~15, 0~15, 0~15) ⇄ short (16-bit)
    // =========================================================================

    /**
     * ワールド絶対座標からセクション内ローカル座標 (0~15) を抽出して short にパッキングします。
     */
    public static short packLocalPosFromWorld(final int worldX, final int worldY, final int worldZ, final int minY) {
        final int relX = worldX & 15;
        final int relY = (worldY - minY) & 15;
        final int relZ = worldZ & 15;
        return packLocalPos(relX, relY, relZ);
    }

    /**
     * セクション内ローカル座標 (x, y, z) を short にパッキングします。
     * 各軸は 0 〜 15 (4bit) の範囲内である必要があります。
     * ビット配置: [4bit: 空き] [4bit: X] [4bit: Z] [4bit: Y]
     */
    public static short packLocalPos(final int relX, final int relY, final int relZ) {
        return (short) (((relX & 0xF) << 8) | ((relZ & 0xF) << 4) | (relY & 0xF));
    }

    /**
     * short から セクション内ローカル X (0~15) を抽出します。
     */
    public static int unpackLocalX(final short packed) {
        return (packed >> 8) & 0xF;
    }

    /**
     * short から セクション内ローカル Y (0~15) を抽出します。
     */
    public static int unpackLocalY(final short packed) {
        return packed & 0xF;
    }

    /**
     * short から セクション内ローカル Z (0~15) を抽出します。
     */
    public static int unpackLocalZ(final short packed) {
        return (packed >> 4) & 0xF;
    }


    // =========================================================================
    // 3. ワールド絶対座標 (x, y, z) ⇄ long (64-bit) [MC標準互換]
    // =========================================================================

    /**
     * ワールド絶対座標 (x, y, z) を long にパッキングします。
     * (Minecraft公式 BlockPos.asLong() と同等のフォーマット)
     */
    public static long packBlockPos(final int x, final int y, final int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }

    /**
     * long から ワールド絶対座標 X を抽出します。
     */
    public static int unpackBlockX(final long packed) {
        return (int) (packed >> 38);
    }

    /**
     * long から ワールド絶対座標 Y を抽出します（負の数にも対応）。
     */
    public static int unpackBlockY(final long packed) {
        return (int) (packed << 52 >> 52);
    }

    /**
     * long から ワールド絶対座標 Z を抽出します（負の数にも対応）。
     */
    public static int unpackBlockZ(final long packed) {
        return (int) (packed << 26 >> 38);
    }


    // =========================================================================
    // 4. complete() 内で使う補助計算
    // =========================================================================

    /**
     * ワールド Y 座標から対応するセクションインデックス (0, 1, 2...) を算出します。
     */
    public static int toSectionIndex(final int worldY, final int minY) {
        return (worldY - minY) >> 4;
    }

    /**
     * ブロック座標から所属する Chunk 座標 (X または Z) を算出します。
     */
    public static int toChunkCoord(final int blockCoord) {
        return blockCoord >> 4;
    }

    /**
     * Chunk 座標とセクション内ローカル座標から、ワールド絶対座標を計算します。
     */
    public static int toWorldCoord(final int chunkCoord, final int localCoord) {
        return (chunkCoord << 4) + localCoord;
    }

    /**
     * 相対セクションインデックス (0, 1, 2...) と World の最低高度 (minY) から、
     * パケット等で使用する実際のワールドセクション Y 座標を計算します。
     */
    public static int toWorldSectionY(final int sectionIndex, final int minY) {
        return sectionIndex + (minY >> 4);
    }
}
