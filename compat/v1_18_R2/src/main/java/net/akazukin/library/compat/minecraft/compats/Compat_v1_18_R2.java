package net.akazukin.library.compat.minecraft.compats;

import io.netty.channel.Channel;
import net.akazukin.library.compat.minecraft.Compat;
import net.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.compat.minecraft.v1_18_R2.PacketProcessor_v1_18_R2;
import net.akazukin.library.utils.ReflectionUtils;
import net.akazukin.library.utils.StringUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerConnection;
import net.minecraft.world.inventory.ContainerAnvil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Compat_v1_18_R2 implements Compat {
    private final PacketProcessor_v1_18_R2 pktProcessor;

    public Compat_v1_18_R2() {
        pktProcessor = new PacketProcessor_v1_18_R2(this);
    }

    @Override
    public int getProtocolVersion() {
        return SharedConstants.c();
    }

    @Override
    public int getMinHeight(final World world) {
        return world.getMinHeight();
    }

    @Override
    public WrappedAnvilInventory getWrappedAnvil(final Inventory inventory) {
        if (!(inventory instanceof CraftInventory)) return null;
        if (!(((CraftInventory) inventory).getInventory() instanceof ContainerAnvil)) return null;

        return new WrappedAnvilInventory(
                inventory,
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).v,
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).w.b(),
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).maximumRepairCost
        );
    }

    @Override
    public Inventory getBukkitAnvil(final WrappedAnvilInventory inventory) {
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).v = inventory.getRenameText();
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).w.a(inventory.getRepairCost());
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).maximumRepairCost = inventory.getMaximumRepairCost();
        return inventory.getInventory();
    }

    @Override
    public net.minecraft.network.protocol.Packet<?> getNMSPacket(final Packet packet) {
        return pktProcessor.processWrapper(packet);
    }

    @Override
    public Packet getWrappedPacket(final Object packet) {
        return pktProcessor.processPacket((net.minecraft.network.protocol.Packet<?>) packet);
    }

    @Override
    public void sendPacket(final Player player, final Packet packet) {
        ((CraftPlayer) player).getHandle().b.a(getNMSPacket(packet));
    }

    @Override
    public WrappedBlockPos getWrappedBlockPos(final Object pos) {
        if (!(pos instanceof final BlockPosition pos2))
            throw new IllegalArgumentException("Invalid argument position must be a block position");

        return new WrappedBlockPos(pos2.u(), pos2.v(), pos2.w());
    }

    @Override
    public BlockPosition getNMSBlockPos(final WrappedBlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public Channel getPlayerChannel(final Player player) {
        final PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
        try {
            final NetworkManager network = ReflectionUtils.getField(connection, "c", NetworkManager.class);
            return network.m;
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Channel> getServerChannels() {
        final ServerConnection connection = ((CraftServer) Bukkit.getServer()).getServer().ad();
        try {
            final List<NetworkManager> networks = (List<NetworkManager>) ReflectionUtils.getField(connection, "g", List.class);
            return networks.stream().map(network -> network.m).toList();
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    @Override
    public void sendSignUpdate(final Player player, final Location location, final String[] result) {
        player.sendSignChange(location, result);
    }

    @Override
    public boolean hasNBT(final ItemStack itemStack) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.s();
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final String value) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        final NBTTagCompound nbt = nmsItemStack.u();
        nbt.a(id, value);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final long value) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        final NBTTagCompound nbt = nmsItemStack.u();
        nbt.a(id, value);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final boolean value) {
        return setNBT(itemStack, id, String.valueOf(value));
    }

    @Override
    @SuppressWarnings("null")
    public String getNBTString(final ItemStack itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return containsNBT(itemStack, id) ? nmsItemStack.t().l(id) : null;
    }

    @Override
    @SuppressWarnings("null")
    public Long getNBTLong(final ItemStack itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return containsNBT(itemStack, id) ? nmsItemStack.t().i(id) : null;
    }

    @Override
    public Boolean getNBTBoolean(final ItemStack itemStack, final String id) {
        return StringUtils.getBoolean(getNBTString(itemStack, id));
    }

    @Override
    @SuppressWarnings("null")
    public boolean containsNBT(final ItemStack itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return hasNBT(itemStack) && nmsItemStack.t().e(id);
    }

    @Override
    @SuppressWarnings("null")
    public ItemStack removeNBT(final ItemStack itemStack, final String key) {
        if (!containsNBT(itemStack, key)) return itemStack;
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        nmsItemStack.t().r(key);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }
}
