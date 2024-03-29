package net.akazukin.library.compat.minecraft;

import io.netty.channel.Channel;
import net.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

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


    ItemStack setNBT(ItemStack itemStack, String id, String value);

    ItemStack setNBT(ItemStack itemStack, String id, long value);

    @Nullable
    String getNBTString(ItemStack itemStack, String id);

    @Nullable
    Long getNBTLong(ItemStack itemStack, String id);

    boolean containsNBT(ItemStack itemStack, String id);
}
