package org.akazukin.library.compat.minecraft;

import io.netty.channel.Channel;
import org.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import org.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import org.akazukin.library.compat.minecraft.data.packets.Packet;
import org.akazukin.library.world.WrappedBlockData;
import org.akazukin.library.worldedit.Vec2;
import org.akazukin.library.worldedit.Vec3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface ICompat {
    float getAttackCooldown(Player player);

    int getProtocolVersion();

    int getMinHeight(World world);

    @Deprecated
    WrappedAnvilInventory getWrappedAnvil(Inventory inventory);

    @Deprecated
    Inventory getBukkitAnvil(WrappedAnvilInventory inventory);

    Object getNMSPacket(Packet packet);

    Packet getWrappedPacket(Object packet);

    void sendPacket(@Nonnull Player player, @Nonnull Packet packet);

    Vec3<Integer> getWrappedBlockPos(Object pos);

    Object getNMSBlockPos(Vec3<Integer> pos);

    Channel getPlayerChannel(Player player);

    Channel[] getServerChannels();

    void sendSignUpdate(Player player, Location location, String[] result);

    boolean hasNBT(Object itemStack);

    <T> T setNBT(T itemStack, String key, String value);

    <T> T setNBT(T itemStack, String key, long value);

    <T> T setNBT(T itemStack, String key, boolean value);

    <T> T setNBT(T itemStack, String key, byte value);

    <T> T setNBT(T itemStack, String key, short value);

    <T> T setNBT(T itemStack, String key, UUID value);

    <T> T setNBT(T itemStack, String key, double value);

    @Nullable
    String getNBTString(Object itemStack, String key);

    @Nullable
    Long getNBTLong(Object itemStack, String key);

    @Nullable
    Boolean getNBTBoolean(Object itemStack, String key);

    Boolean containsNBT(Object itemStack, String key);

    <T> T removeNBT(T itemStack, String key);

    WrappedPlayerProfile getGameProfile(Player player);

    BossBar createBossBar(String title, BarColor color, BarStyle style, BarFlag... flags);

    <I> I setPDCData(I itemStack, String key, String value);

    <I> I setPDCData(I itemStack, String key, Integer value);

    <I> I setPDCData(I itemStack, String key, Long value);

    <I> I setPDCData(I itemStack, String key, Boolean value);

    boolean containsPDCData(Object itemStack, String key);

    Integer getPDCDataAsInt(Object itemStack, String key);

    String getPDCDataAsString(Object itemStack, String key);

    Boolean getPDCDataAsBool(Object itemStack, String key);

    Long getPDCDataAsLong(Object itemStack, String key);

    <I> I setPlData(I itemStack, String key, String value);

    <I> I setPlData(I itemStack, String key, Integer value);

    <I> I setPlData(I itemStack, String key, Long value);

    String getPlDataString(Object itemStack, String key);

    Integer getPlDataInt(Object itemStack, String key);

    Long getPlDataLong(Object itemStack, String key);

    Boolean getPlDataBool(Object itemStack, String key);

    <I> I setPlData(I itemStack, String key, Boolean value);

    boolean containsPlData(Object itemStack, String key);

    <I> I removePlData(I item, String key);

    <I> I removePDCData(I item, String key);

    Object getNMSChunk(Object world, Vec2<Integer> chunkLoc);

    Object getNMSChunk(Object world, Vec3<Integer> vec3i);

    Object setBlockData(Object chunk, Vec3<Integer> vec3i, WrappedBlockData blockData, boolean applyPhysics);

    WrappedBlockData getNMSNewBlockData(Material material, byte data);

    Object getNMSChunk(Object chunk);

    Object getNMSWorld(Object world);

    Object getNMSChunkSection(Object chunk, int y);

    Object getNMSChunkSection(Object world, Vec3<Integer> chunkLoc);

    int getHeight(World world);

    void updateLightsAtChunk(final Object chunk);

    void updateChunk(Object world, Vec2<Integer> chunkLoc);

    WrappedBlockData setWrappedBlockData(Object chunkSection, Vec3<Integer> vec3i, WrappedBlockData blockData,
                                         boolean applyPhysics);

    WrappedBlockData getWrappedBlockData(Object chunkSection, Vec3<Integer> vec3i);

    Object getBlockData(Object blockData);

    Object getBlockData(Object chunkSection, Vec3<Integer> vec3i);

    Object getBlockData2(Object chunkSection, Vec3<Integer> fixedVec3i);

    Object getNMSChunkSection(Object chunkSection);

    Object getNMSChunkSection2(Object chunk, int fixedY);

    void updateLightsAtBlock(Object world, Vec3<Integer> pos);

    void unloadChunk(Object chunk, boolean save);

    Object loadChunk(Object world, Vec2<Integer> chunkLoc, boolean generate);

    float getDestroySpeed(ItemStack itemStack, BlockState blockState);

    String getBlockName(Object block);

    boolean isChunkViewing(Object player, Object world, Vec2<Integer> chunkPos);

    Object getNMSPlayer(Object player);
}
