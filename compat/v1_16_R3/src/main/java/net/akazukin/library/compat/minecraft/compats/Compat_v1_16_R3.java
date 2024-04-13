package net.akazukin.library.compat.minecraft.compats;

import io.netty.channel.Channel;
import net.akazukin.library.compat.minecraft.Compat;
import net.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.compat.minecraft.v1_16_R3.PacketProcessor_v1_16_R3;
import net.akazukin.library.utils.ReflectionUtils;
import net.akazukin.library.utils.StringUtils;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.ContainerAnvil;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NetworkManager;
import net.minecraft.server.v1_16_R3.ServerConnection;
import net.minecraft.server.v1_16_R3.SharedConstants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class Compat_v1_16_R3 implements Compat {
    private final PacketProcessor_v1_16_R3 pktProcessor;

    public Compat_v1_16_R3() {
        pktProcessor = new PacketProcessor_v1_16_R3(this);
    }

    @Override
    public int getProtocolVersion() {
        return SharedConstants.getGameVersion().getProtocolVersion();
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
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).renameText,
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).levelCost.get(),
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).maximumRepairCost
        );
    }

    @Override
    public Inventory getBukkitAnvil(final WrappedAnvilInventory inventory) {
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).renameText = inventory.getRenameText();
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).levelCost.set(inventory.getRepairCost());
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).maximumRepairCost = inventory.getMaximumRepairCost();
        return inventory.getInventory();
    }

    @Override
    public net.minecraft.server.v1_16_R3.Packet<?> getNMSPacket(final Packet packet) {
        return pktProcessor.processWrapper(packet);
    }

    @Override
    public Packet getWrappedPacket(final Object packet) {
        return pktProcessor.processPacket((net.minecraft.server.v1_16_R3.Packet<?>) packet);
    }

    @Override
    public void sendPacket(final Player player, final Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(getNMSPacket(packet));
    }

    @Override
    public WrappedBlockPos getWrappedBlockPos(final Object pos) {
        if (!(pos instanceof BlockPosition))
            throw new IllegalArgumentException("Invalid argument position must be a block position");

        final BlockPosition pos2 = (BlockPosition) pos;
        return new WrappedBlockPos(pos2.getX(), pos2.getY(), pos2.getZ());
    }

    @Override
    public BlockPosition getNMSBlockPos(final WrappedBlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public Channel getPlayerChannel(final Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
    }

    @Override
    public List<Channel> getServerChannels() {
        final ServerConnection connection = ((CraftServer) Bukkit.getServer()).getServer().getServerConnection();
        try {
            final List<NetworkManager> networks = (List<NetworkManager>) ReflectionUtils.getField(connection, "connectedChannels", List.class);
            return networks.stream().map(network -> network.channel).collect(Collectors.toList());
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
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.hasTag();
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final String value) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        final NBTTagCompound nbt = nmsItemStack.getOrCreateTag();
        nbt.setString(id, value);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final long value) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        final NBTTagCompound nbt = nmsItemStack.getOrCreateTag();
        nbt.setLong(id, value);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final boolean value) {
        return setNBT(itemStack, id, String.valueOf(value));
    }

    @Override
    public String getNBTString(final ItemStack itemStack, final String id) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return containsNBT(itemStack, id) ? nmsItemStack.getTag().getString(id) : null;
    }

    @Override
    public Long getNBTLong(final ItemStack itemStack, final String id) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return containsNBT(itemStack, id) ? nmsItemStack.getTag().getLong(id) : null;
    }

    @Override
    public Boolean getNBTBoolean(final ItemStack itemStack, final String id) {
        return StringUtils.getBoolean(getNBTString(itemStack, id));
    }

    @Override
    public boolean containsNBT(final ItemStack itemStack, final String id) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.hasTag() && nmsItemStack.getTag().hasKey(id);
    }

    @Override
    public ItemStack removeNBT(final ItemStack itemStack, final String key) {
        if (!containsNBT(itemStack, key)) return itemStack;
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        nmsItemStack.getTag().remove(key);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }
}
