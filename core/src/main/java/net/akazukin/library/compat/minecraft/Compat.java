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
import org.bukkit.inventory.ItemStack;

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


    boolean hasNBT(ItemStack itemStack);

    ItemStack setNBT(ItemStack itemStack, String id, String value);

    ItemStack setNBT(ItemStack itemStack, String id, long value);

    ItemStack setNBT(ItemStack itemStack, String id, boolean value);

    @Nullable
    String getNBTString(ItemStack itemStack, String id);

    @Nullable
    Long getNBTLong(ItemStack itemStack, String id);

    @Nullable
    Boolean getNBTBoolean(ItemStack itemStack, String id);

    boolean containsNBT(ItemStack itemStack, String id);

    ItemStack removeNBT(ItemStack itemStack, String key);

    WrappedPlayerProfile getGameProfile(Player player);

    BossBar createBossBar(String title, BarColor color, BarStyle style, BarFlag... flags);
}
