package net.akazukin.library.compat.minecraft.compats;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.akazukin.library.compat.minecraft.Compat;
import net.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.compat.minecraft.v1_17_R1.PacketProcessor_v1_17_R1;
import net.akazukin.library.utils.ObjectUtils;
import net.akazukin.library.utils.ReflectionUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.ServerConnection;
import net.minecraft.world.inventory.ContainerAnvil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class Compat_v1_17_R1 implements Compat {
    private final PacketProcessor_v1_17_R1 pktProcessor;
    private final JavaPlugin plugin;

    public Compat_v1_17_R1(final JavaPlugin plugin) {
        this.pktProcessor = new PacketProcessor_v1_17_R1(this);
        this.plugin = plugin;
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
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).maximumRepairCost =
                inventory.getMaximumRepairCost();
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
            final List<NetworkManager> networks = (List<NetworkManager>) ReflectionUtils.getField(connection, "g",
                    List.class);
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
    public Boolean hasNBT(final Object itemStack) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        return nmsItemStack.hasTag();
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final String value) {
        return this.setNBT(itemStack, key, (Object) value);
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final long value) {
        return this.setNBT(itemStack, key, (Object) value);
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final boolean value) {
        return this.setNBT(itemStack, key, (Object) value);
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final byte value) {
        return this.setNBT(itemStack, key, (Object) value);
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final short value) {
        return this.setNBT(itemStack, key, (Object) value);
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final UUID value) {
        return this.setNBT(itemStack, key, (Object) value);
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final double value) {
        return this.setNBT(itemStack, key, (Object) value);
    }

    @Override
    public String getNBTString(final Object itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        return this.containsNBT(itemStack, key) ? nmsItemStack.getTag().getString(key) : null;
    }

    @Override
    public Long getNBTLong(final Object itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        return this.containsNBT(nmsItemStack, key) ? nmsItemStack.getTag().getLong(key) : null;
    }

    @Override
    public Boolean getNBTBoolean(final Object itemStack, final String key) {
        return ObjectUtils.getBoolean(this.getNBTString(itemStack, key));
    }

    @Override
    public Boolean containsNBT(final Object itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        return this.hasNBT(nmsItemStack) && nmsItemStack.getTag().hasKey(key);
    }

    @Override
    public <T> T removeNBT(final T itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        if (!this.containsNBT(itemStack, key)) return itemStack;

        nmsItemStack.getTag().remove(key);

        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            return (T) nmsItemStack;
        else if (itemStack instanceof ItemStack)
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        else
            return null;
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
    public BossBar createBossBar(final String title, final BarColor color, final BarStyle style,
                                 final BarFlag... flags) {
        return Bukkit.createBossBar(title, color, style, flags);
    }

    @Override
    public <I> I setPDCData(final I itemStack, final String key, final String value) {
        return this.setPDCData(itemStack, PersistentDataType.STRING, key, value);
    }

    @Override
    public <I> I setPDCData(final I itemStack, final String key, final Integer value) {
        return this.setPDCData(itemStack, PersistentDataType.INTEGER, key, value);
    }

    @Override
    public <I> I setPDCData(final I itemStack, final String key, final Long value) {
        return this.setPDCData(itemStack, PersistentDataType.LONG, key, value);
    }

    @Override
    public <I> I setPDCData(final I itemStack, final String key, final Boolean value) {
        return this.setPDCData(itemStack, PersistentDataType.BYTE, key, value != null ? (byte) (value ? 1 : 0) : null);
    }

    @Override
    public Boolean containsPDCData(final Object itemStack, final String key) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) itemStack);
        else if (itemStack instanceof ItemStack)
            bktItemStack = (ItemStack) itemStack;
        else
            return null;

        return bktItemStack.getItemMeta().getPersistentDataContainer().getKeys().contains(new NamespacedKey(this.plugin,
                key));
    }

    @Override
    public Integer getPDCDataInt(final Object itemStack, final String key) {
        return this.getPDCData(itemStack, PersistentDataType.INTEGER, key);
    }

    @Override
    public String getPDCDataString(final Object itemStack, final String key) {
        return this.getPDCData(itemStack, PersistentDataType.STRING, key);
    }

    @Override
    public Boolean getPDCDataBool(final Object itemStack, final String key) {
        return ObjectUtils.getBoolean(this.getPDCData(itemStack, PersistentDataType.BYTE, key));
    }

    @Override
    public Long getPDCDataLong(final Object itemStack, final String key) {
        return this.getPDCData(itemStack, PersistentDataType.LONG, key);
    }

    @Override
    public <I> I setPlData(final I itemStack, final String key, final String value) {
        return this.setPDCData(itemStack, key, value);
    }

    @Override
    public <I> I setPlData(final I itemStack, final String key, final Integer value) {
        return this.setPDCData(itemStack, key, value);
    }

    @Override
    public <I> I setPlData(final I itemStack, final String key, final Long value) {
        return this.setPDCData(itemStack, key, value);
    }

    @Override
    public String getPlDataString(final Object itemStack, final String key) {
        return this.getPDCDataString(itemStack, key);
    }

    @Override
    public Integer getPlDataInt(final Object itemStack, final String key) {
        return this.getPDCDataInt(itemStack, key);
    }

    @Override
    public Long getPlDataLong(final Object itemStack, final String key) {
        return this.getPDCDataLong(itemStack, key);
    }

    @Override
    public Boolean getPlDataBool(final Object itemStack, final String key) {
        return this.getPDCDataBool(itemStack, key);
    }

    @Override
    public <I> I setPlData(final I itemStack, final String key, final Boolean value) {
        return this.setPDCData(itemStack, key, value);
    }

    @Override
    public boolean containsPlData(final Object itemStack, final String key) {
        return this.containsPDCData(itemStack, key);
    }

    @Override
    public <I> I removePlData(final I itemStack, final String key) {
        return this.removePDCData(itemStack, key);
    }

    @Override
    public <I> I removePDCData(final I itemStack, final String key) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) itemStack);
        else if (itemStack instanceof ItemStack)
            bktItemStack = (ItemStack) itemStack;
        else
            return null;

        final ItemMeta itemMeta = bktItemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().remove(
                new NamespacedKey(this.plugin, key)
        );
        bktItemStack.setItemMeta(itemMeta);

        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            return (I) CraftItemStack.asNMSCopy(bktItemStack);
        else if (itemStack instanceof ItemStack)
            return (I) bktItemStack;
        else
            return null;
    }

    private <I, T> T getPDCData(final I itemStack, final PersistentDataType<T, T> type, final String id) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) itemStack);
        else if (itemStack instanceof ItemStack)
            bktItemStack = (ItemStack) itemStack;
        else
            return null;

        return bktItemStack.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(this.plugin, id), type
        );
    }

    private <I, T> I setPDCData(final I itemStack, final PersistentDataType<T, T> type, final String id,
                                final T value) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) itemStack);
        else if (itemStack instanceof ItemStack)
            bktItemStack = (ItemStack) itemStack;
        else
            return null;

        final ItemMeta itemMeta = bktItemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(
                new NamespacedKey(this.plugin, id),
                type, value
        );
        bktItemStack.setItemMeta(itemMeta);

        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            return (I) CraftItemStack.asNMSCopy(bktItemStack);
        else if (itemStack instanceof ItemStack)
            return (I) bktItemStack;
        else
            return null;
    }

    private <T> T setNBT(final T itemStack, final String key, final Object value) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        final NBTTagCompound nbt = nmsItemStack.getOrCreateTag();
        if (value instanceof Boolean)
            nbt.setBoolean(key, (Boolean) value);
        else if (value instanceof String)
            nbt.setString(key, (String) value);
        else if (value instanceof Integer)
            nbt.setInt(key, (Integer) value);
        else if (value instanceof Long)
            nbt.setLong(key, (Long) value);
        else if (value instanceof Byte)
            nbt.setByte(key, (Byte) value);
        else if (value instanceof Short)
            nbt.setShort(key, (Short) value);
        else if (value instanceof Double)
            nbt.setDouble(key, (Double) value);
        else if (value instanceof UUID)
            nbt.a(key, (UUID) value);

        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            return (T) nmsItemStack;
        else if (itemStack instanceof ItemStack)
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        else
            return null;
    }
}
