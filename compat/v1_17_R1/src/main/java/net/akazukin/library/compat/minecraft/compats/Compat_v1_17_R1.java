package net.akazukin.library.compat.minecraft.compats;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.List;
import net.akazukin.library.compat.minecraft.Compat;
import net.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.compat.minecraft.v1_17_R1.PacketProcessor_v1_17_R1;
import net.akazukin.library.utils.ReflectionUtils;
import net.akazukin.library.utils.StringUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.ServerConnection;
import net.minecraft.world.inventory.ContainerAnvil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Compat_v1_17_R1 implements Compat {
    private final PacketProcessor_v1_17_R1 pktProcessor;

    public Compat_v1_17_R1() {
        this.pktProcessor = new PacketProcessor_v1_17_R1(this);
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
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).w.get(),
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).maximumRepairCost
        );
    }

    @Override
    public Inventory getBukkitAnvil(final WrappedAnvilInventory inventory) {
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).v = inventory.getRenameText();
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).w.set(inventory.getRepairCost());
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).maximumRepairCost = inventory.getMaximumRepairCost();
        return inventory.getInventory();
    }

    @Override
    public net.minecraft.network.protocol.Packet<?> getNMSPacket(final Packet packet) {
        return this.pktProcessor.processWrapper(packet);
    }

    @Override
    public Packet getWrappedPacket(final Object packet) {
        return this.pktProcessor.processPacket((net.minecraft.network.protocol.Packet<?>) packet);
    }

    @Override
    public void sendPacket(final Player player, final Packet packet) {
        ((CraftPlayer) player).getHandle().b.sendPacket(this.getNMSPacket(packet));
    }

    @Override
    public WrappedBlockPos getWrappedBlockPos(final Object pos) {
        if (!(pos instanceof final BlockPosition pos2))
            throw new IllegalArgumentException("Invalid argument position must be a block position");

        return new WrappedBlockPos(pos2.getX(), pos2.getY(), pos2.getZ());
    }

    @Override
    public BlockPosition getNMSBlockPos(final WrappedBlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public Channel getPlayerChannel(final Player player) {
        return ((CraftPlayer) player).getHandle().b.a.k;
    }

    @Override
    public List<Channel> getServerChannels() {
        final ServerConnection connection = ((CraftServer) Bukkit.getServer()).getServer().getServerConnection();
        try {
            final List<NetworkManager> networks = (List<NetworkManager>) ReflectionUtils.getField(connection, "g", List.class);
            return networks.stream().map(network -> network.k).toList();
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
        return nmsItemStack.hasTag();
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final String value) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        final NBTTagCompound nbt = nmsItemStack.getOrCreateTag();
        nbt.setString(id, value);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final long value) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        final NBTTagCompound nbt = nmsItemStack.getOrCreateTag();
        nbt.setLong(id, value);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack setNBT(final ItemStack itemStack, final String id, final boolean value) {
        return this.setNBT(itemStack, id, String.valueOf(value));
    }

    @Override
    public String getNBTString(final ItemStack itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return this.containsNBT(itemStack, id) ? nmsItemStack.getTag().getString(id) : null;
    }

    @Override
    public Long getNBTLong(final ItemStack itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return this.containsNBT(itemStack, id) ? nmsItemStack.getTag().getLong(id) : null;
    }

    @Override
    public Boolean getNBTBoolean(final ItemStack itemStack, final String id) {
        return StringUtils.getBoolean(this.getNBTString(itemStack, id));
    }

    @Override
    public boolean containsNBT(final ItemStack itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return this.hasNBT(itemStack) && nmsItemStack.getTag().hasKey(id);
    }

    @Override
    public ItemStack removeNBT(final ItemStack itemStack, final String key) {
        if (!this.containsNBT(itemStack, key)) return itemStack;
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        nmsItemStack.getTag().remove(key);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public WrappedPlayerProfile getGameProfile(final Player player) {
        if (player instanceof CraftPlayer) {
            final GameProfile profile = ((CraftPlayer) player).getProfile();
            final Property textures = new ArrayList<>(profile.getProperties().get("textures")).get(0);
            final JsonObject textures__ = new JsonParser()
                    .parse(textures.getValue())
                    .getAsJsonObject();
            final JsonObject textures_ = textures__.getAsJsonObject("textures");
            final WrappedPlayerProfile profile_ = new WrappedPlayerProfile();
            profile_.setUniqueId(profile.getId());
            profile_.setName(profile.getName());
            if (textures_.has("SKIN"))
                profile_.setSkin(textures_
                        .getAsJsonObject("SKIN")
                        .get("url")
                        .getAsString());
            if (textures_.has("SKIN") &&
                    textures_.getAsJsonObject("SKIN").has("metadata"))
                profile_.setSkinModel(textures_
                        .getAsJsonObject("SKIN")
                        .getAsJsonObject("metadata")
                        .getAsJsonObject("model")
                        .getAsString()
                        .equals("slim") ? "SLIM" : "CLASSIC");
            if (textures_.has("CAPE"))
                profile_.setCape(textures_
                        .getAsJsonObject("CAPE")
                        .get("url")
                        .getAsString());
            profile_.setTimestamp(textures__.get("timestamp").getAsLong());
            return profile_;
        }
        /*try {
            ReflectionUtils.getField(((CraftPlayer) player).getHandle(), EntityHuman.class, "cs", GameProfile.class);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    @Override
    public BossBar createBossBar(final String title, final BarColor color, final BarStyle style, final BarFlag... flags) {
        return Bukkit.createBossBar(title, color, style, flags);
    }
}
