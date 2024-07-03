package net.akazukin.library.compat.minecraft;

import io.netty.channel.Channel;
import java.util.List;
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

    <T> T setNBT(T itemStack, String id, String value);

    <T> T setNBT(T itemStack, String id, long value);

    <T> T setNBT(T itemStack, String id, boolean value);

    @Nullable
    String getNBTString(Object itemStack, String id);

    @Nullable
    Long getNBTLong(Object itemStack, String id);

    @Nullable
    Boolean getNBTBoolean(Object itemStack, String id);

    Boolean containsNBT(Object itemStack, String id);

    <T> T removeNBT(T itemStack, String key);

    WrappedPlayerProfile getGameProfile(Player player);

    BossBar createBossBar(String title, BarColor color, BarStyle style, BarFlag... flags);

    <I> I setPDCData(I itemStack, String id, String value);

    <I> I setPDCData(I itemStack, String id, Integer value);

    Integer getIntPDCData(Object itemStack, String id);

    String getStringPDCData(Object itemStack, String id);

    <I> I setPlData(I itemStack, String key, String value);

    <I> I setPlData(I itemStack, String key, Integer value);

    String getStringPlData(Object itemStack, String key);

    Integer getIntPlData(Object itemStack, String key);
}
