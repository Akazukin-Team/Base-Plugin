package net.akazukin.library.compat.minecraft;

import io.netty.channel.Channel;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface Compat {
    int getProtocolVersion();

    int getMinHeight(World world);

    @Deprecated
    WrappedAnvilInventory getWrappedAnvil(Inventory inventory);

    @Deprecated
    Inventory getBukkitAnvil(WrappedAnvilInventory inventory);

    Object getNMSPacket(Packet packet);

    Packet getWrappedPacket(Object packet);

    void sendPacket(Player player, Packet packet);

    WrappedBlockPos getWrappedBlockPos(Object pos);

    Object getNMSBlockPos(WrappedBlockPos pos);

    Channel getPlayerChannel(Player player);

    List<Channel> getServerChannels();

    void sendSignUpdate(Player player, Location location, String[] result);


    Boolean hasNBT(Object itemStack);

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

    Boolean containsPDCData(Object itemStack, String key);

    Integer getPDCDataInt(Object itemStack, String key);

    String getPDCDataString(Object itemStack, String key);

    Boolean getPDCDataBool(Object itemStack, String key);

    Long getPDCDataLong(Object itemStack, String key);

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
}
