package net.akazukin.library.compat.minecraft.compats;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.akazukin.library.compat.minecraft.Compat;
import net.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.compat.minecraft.v1_16_R3.PacketProcessor_v1_16_R3;
import net.akazukin.library.utils.ObjectUtils;
import net.akazukin.library.utils.ReflectionUtils;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.ContainerAnvil;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NetworkManager;
import net.minecraft.server.v1_16_R3.ServerConnection;
import net.minecraft.server.v1_16_R3.SharedConstants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class Compat_v1_16_R3 implements Compat {
    private final PacketProcessor_v1_16_R3 pktProcessor;
    private final JavaPlugin plugin;

    public Compat_v1_16_R3(final JavaPlugin plugin) {
        this.pktProcessor = new PacketProcessor_v1_16_R3(this);
        this.plugin = plugin;
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
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).renameText =
                inventory.getRenameText();
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).levelCost.set(inventory.getRepairCost());
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).maximumRepairCost =
                inventory.getMaximumRepairCost();
        return inventory.getInventory();
    }

    @Override
    public net.minecraft.server.v1_16_R3.Packet<?> getNMSPacket(final Packet packet) {
        return this.pktProcessor.processWrapper(packet);
    }

    @Override
    public Packet getWrappedPacket(final Object packet) {
        return this.pktProcessor.processPacket((net.minecraft.server.v1_16_R3.Packet<?>) packet);
    }

    @Override
    public void sendPacket(final Player player, final Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(this.getNMSPacket(packet));
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
    @SuppressWarnings("unchecked")
    public List<Channel> getServerChannels() {
        final ServerConnection connection = ((CraftServer) Bukkit.getServer()).getServer().getServerConnection();
        try {
            final List<NetworkManager> networks = (List<NetworkManager>) ReflectionUtils.getField(connection,
                    "connectedChannels", List.class);
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
    public Boolean hasNBT(final Object itemStack) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            nmsItemStack = (net.minecraft.server.v1_16_R3.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        return nmsItemStack.hasTag();
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final String value) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            nmsItemStack = (net.minecraft.server.v1_16_R3.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        final NBTTagCompound nbt = nmsItemStack.getOrCreateTag();
        nbt.setString(key, value);

        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            return (T) nmsItemStack;
        else if (itemStack instanceof ItemStack)
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        else
            return null;
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final long value) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            nmsItemStack = (net.minecraft.server.v1_16_R3.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        final NBTTagCompound nbt = nmsItemStack.getOrCreateTag();
        nbt.setLong(key, value);

        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            return (T) nmsItemStack;
        else if (itemStack instanceof ItemStack)
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        else
            return null;
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final boolean value) {
        return this.setNBT(itemStack, key, String.valueOf(value));
    }

    @Override
    @SuppressWarnings("null")
    public String getNBTString(final Object itemStack, final String key) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            nmsItemStack = (net.minecraft.server.v1_16_R3.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        return this.containsNBT(nmsItemStack, key) ? nmsItemStack.getTag().getString(key) : null;
    }

    @Override
    @SuppressWarnings("null")
    public Long getNBTLong(final Object itemStack, final String key) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            nmsItemStack = (net.minecraft.server.v1_16_R3.ItemStack) itemStack;
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
    @SuppressWarnings("null")
    public Boolean containsNBT(final Object itemStack, final String key) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            nmsItemStack = (net.minecraft.server.v1_16_R3.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        return this.hasNBT(nmsItemStack) && nmsItemStack.getTag().hasKey(key);
    }

    @Override
    public <T> T removeNBT(final T itemStack, final String key) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            nmsItemStack = (net.minecraft.server.v1_16_R3.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        if (!this.containsNBT(nmsItemStack, key)) return itemStack;

        final NBTTagCompound nbt = nmsItemStack.getOrCreateTag();
        nbt.remove(key);


        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
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
            ReflectionUtils.getField(((CraftPlayer) player).getHandle(), EntityHuman.class, "bJ", GameProfile.class);
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
    public <I> I setPDCData(final I itemStack, final String key, final boolean value) {
        return this.setPDCData(itemStack, PersistentDataType.BYTE, key, (byte) (value ? 1 : 0));
    }

    @Override
    public Integer getIntPDCData(final Object itemStack, final String key) {
        return this.getPDCData(itemStack, PersistentDataType.INTEGER, key);
    }

    @Override
    public String getStringPDCData(final Object itemStack, final String key) {
        return this.getPDCData(itemStack, PersistentDataType.STRING, key);
    }

    @Override
    public Boolean getBoolPDCData(final Object itemStack, final String key) {
        return ObjectUtils.getBoolean(this.getPDCData(itemStack, PersistentDataType.BYTE, key));
    }

    @Override
    public <I> I setPlData(final I itemStack, final String key, final String value) {
        return this.setPDCData(itemStack, key, value);
    }

    private <I, T> I setPDCData(final I itemStack, final PersistentDataType<T, T> type, final String id,
                                final T value) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_16_R3.ItemStack) itemStack);
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

        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            return (I) bktItemStack;
        else if (itemStack instanceof ItemStack)
            return (I) CraftItemStack.asNMSCopy(bktItemStack);
        else
            return null;
    }

    @Override
    public <I> I setPlData(final I itemStack, final String key, final Integer value) {
        return this.setPDCData(itemStack, key, value);
    }

    @Override
    public String getStringPlData(final Object itemStack, final String key) {
        return this.getStringPDCData(itemStack, key);
    }

    @Override
    public Integer getIntPlData(final Object itemStack, final String key) {
        return this.getIntPDCData(itemStack, key);
    }

    @Override
    public Boolean getBoolPlData(final Object itemStack, final String key) {
        return this.getBoolPDCData(itemStack, key);
    }

    @Override
    public ItemStack setPlData(final ItemStack itemStack, final String key, final boolean value) {
        return this.setPDCData(itemStack, key, value);
    }

    private <I, T> T getPDCData(final I itemStack, final PersistentDataType<T, T> type, final String id) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_16_R3.ItemStack) itemStack);
        else if (itemStack instanceof ItemStack)
            bktItemStack = (ItemStack) itemStack;
        else
            return null;

        return bktItemStack.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(this.plugin, id), type
        );
    }
}
