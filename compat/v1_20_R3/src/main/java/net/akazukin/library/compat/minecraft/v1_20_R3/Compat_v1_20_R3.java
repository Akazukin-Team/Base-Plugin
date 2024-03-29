package net.akazukin.library.compat.minecraft.v1_20_R3;

import io.netty.channel.Channel;
import net.akazukin.library.compat.minecraft.Compat;
import net.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.utils.ReflectionUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConnection;
import net.minecraft.world.inventory.ContainerAnvil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.List;

public class Compat_v1_20_R3 implements Compat {
    public PacketProcessor_v1_20_R3 pktProcessor;

    public Compat_v1_20_R3() {
        pktProcessor = new PacketProcessor_v1_20_R3(this);
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
        for (final Method method : inventory.getClass().getMethods()) {
            System.out.println(method.getName());
        }
        System.out.println(((CraftInventory) inventory).getInventory());
        if (!(((CraftInventory) inventory).getInventory() instanceof ContainerAnvil)) return null;
        System.out.println("create Instance");
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
        ((CraftPlayer) player).getHandle().c.b(getNMSPacket(packet));
    }

    @Override
    public WrappedBlockPos getWrappedBlockPos(final Object pos) {
        if (!(pos instanceof final BlockPosition pos2))
            throw new IllegalArgumentException("Invalid argument position must be a block position");

        return new WrappedBlockPos(pos2.u(), pos2.v(), pos2.w());
    }

    @Override
    public BlockPosition getNMSBlockPos(final WrappedBlockPos position) {
        return new BlockPosition(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Channel getPlayerChannel(final Player player) {
        final PlayerConnection connection = ((CraftPlayer) player).getHandle().c;
        try {
            final NetworkManager network = ReflectionUtils.getField(connection, ServerCommonPacketListenerImpl.class, "c", NetworkManager.class);
            return network.n;
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Channel> getServerChannels() {
        final ServerConnection connection = ((CraftServer) Bukkit.getServer()).getServer().af();
        try {
            final List<NetworkManager> networks = (List<NetworkManager>) ReflectionUtils.getField(connection, "g", List.class);
            return networks.stream().map(network -> network.n).toList();
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    @Override
    public void sendSignUpdate(final Player player, final Location location, final String[] lines) {
        ((CraftPlayer) player).sendSignChange(location, lines);
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final String value) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        final NBTTagCompound nbt = nmsItemStack.w();
        nbt.a(id, value);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final long value) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        final NBTTagCompound nbt = nmsItemStack.w();
        nbt.a(id, value);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public String getNBTString(final ItemStack itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.u() && nmsItemStack.v().e(id) ? nmsItemStack.v().l(id) : null;
    }

    @Override
    public Long getNBTLong(final ItemStack itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.u() && nmsItemStack.v().e(id) ? nmsItemStack.v().i(id) : null;
    }

    @Override
    public boolean containsNBT(final ItemStack itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.u() && nmsItemStack.v().e(id);
    }
}
