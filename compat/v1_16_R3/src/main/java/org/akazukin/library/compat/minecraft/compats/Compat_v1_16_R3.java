package org.akazukin.library.compat.minecraft.compats;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.Chunk;
import net.minecraft.server.v1_16_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_16_R3.ChunkSection;
import net.minecraft.server.v1_16_R3.ContainerAnvil;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NetworkManager;
import net.minecraft.server.v1_16_R3.ServerConnection;
import net.minecraft.server.v1_16_R3.SharedConstants;
import net.minecraft.server.v1_16_R3.TicketType;
import net.minecraft.server.v1_16_R3.Unit;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.akazukin.library.compat.minecraft.Compat;
import org.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import org.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import org.akazukin.library.compat.minecraft.data.packets.Packet;
import org.akazukin.library.compat.minecraft.v1_16_R3.PacketProcessor_v1_16_R3;
import org.akazukin.library.exception.UnsupportedOperationYetException;
import org.akazukin.library.utils.ReflectionUtils;
import org.akazukin.library.world.WrappedBlockData;
import org.akazukin.library.worldedit.Vec2;
import org.akazukin.library.worldedit.Vec2i;
import org.akazukin.library.worldedit.Vec3;
import org.akazukin.library.worldedit.Vec3i;
import org.akazukin.util.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlockState;
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
    public float getAttackCooldown(final Player player) {
        return ((CraftPlayer) player).getHandle().getAttackCooldown(0.5f);
    }

    @Override
    public int getProtocolVersion() {
        return SharedConstants.getGameVersion().getProtocolVersion();
    }

    @Override
    public int getMinHeight(final World world) {
        return 0;//return world.getMinHeight();
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
    public void sendPacket(@Nonnull final Player player, @Nonnull final Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(this.getNMSPacket(packet));
    }

    @Override
    public Vec3i getWrappedBlockPos(final Object pos) {
        if (!(pos instanceof BlockPosition))
            throw new IllegalArgumentException("Invalid argument position must be a block position");

        final BlockPosition pos2 = (BlockPosition) pos;
        return new Vec3i(pos2.getX(), pos2.getY(), pos2.getZ());
    }

    @Override
    public BlockPosition getNMSBlockPos(final Vec3<Integer> pos) {
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
            return networks.stream().filter(Objects::nonNull).map(network -> network.channel).collect(Collectors.toList());
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
    @SuppressWarnings("unchecked")
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
        else
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public WrappedPlayerProfile getGameProfile(final Player player) {
        if (player instanceof CraftPlayer) {
            final GameProfile profile = ((CraftPlayer) player).getProfile();
            final Property textures = new ArrayList<>(profile.getProperties().get("textures")).get(0);
            final JsonObject textures__ = JsonParser
                    .parseString(textures.getValue())
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
    public <I> I setPDCData(final I itemStack, final String key, final Long value) {
        return this.setPDCData(itemStack, PersistentDataType.LONG, key, value);
    }

    @Override
    public <I> I setPDCData(final I itemStack, final String key, final Boolean value) {
        return this.setPDCData(itemStack, PersistentDataType.BYTE, key, value != null ? (byte) (value ? 1 : 0) : null);
    }

    @Override
    public boolean containsPDCData(final Object itemStack, final String key) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_16_R3.ItemStack) itemStack);
        else if (itemStack instanceof ItemStack)
            bktItemStack = (ItemStack) itemStack;
        else
            throw new IllegalStateException("itemStack is not allowed class");

        final ItemMeta meta = bktItemStack.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().getKeys().contains(new NamespacedKey(this.plugin, key));
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
    @SuppressWarnings("unchecked")
    public <I> I removePDCData(final I itemStack, final String key) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_16_R3.ItemStack) itemStack);
        else if (itemStack instanceof ItemStack)
            bktItemStack = (ItemStack) itemStack;
        else
            return null;

        final ItemMeta itemMeta = bktItemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().remove(
                    new NamespacedKey(this.plugin, key)
            );
            bktItemStack.setItemMeta(itemMeta);
        }

        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            return (I) CraftItemStack.asNMSCopy(bktItemStack);
        else
            return (I) bktItemStack;
    }

    @Override
    public Chunk getNMSChunk(final Object world, final Vec2<Integer> chunkLoc) {
        return this.getNMSWorld(world).getChunkAt(chunkLoc.getX(), chunkLoc.getY());
    }

    @Override
    public Chunk getNMSChunk(final Object world, final Vec3<Integer> loc) {
        return this.getNMSChunk(world, new Vec2i(loc.getX() >> 4, loc.getZ() >> 4));
    }

    @Override
    public Object setBlockData(final Object chunk, final Vec3<Integer> vec3i, final WrappedBlockData blockData,
                               final boolean applyPhysics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WrappedBlockData getNMSNewBlockData(final Material material, final byte data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Chunk getNMSChunk(final Object chunk) {
        if (chunk instanceof CraftChunk)
            return ((CraftChunk) chunk).getHandle();
        else if (chunk instanceof Chunk)
            return (Chunk) chunk;
        else return null;
    }

    @Override
    public WorldServer getNMSWorld(final Object world) {
        if (world instanceof World)
            return ((CraftWorld) world).getHandle();
        else if (world instanceof WorldServer)
            return (WorldServer) world;
        else return null;
    }

    @Override
    public ChunkSection getNMSChunkSection(final Object chunk, final int y) {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public ChunkSection getNMSChunkSection(final Object world, final Vec3<Integer> chunkLoc) {
        return this.getNMSChunkSection(this.getNMSChunk(this.getNMSWorld(world), chunkLoc), chunkLoc.getY());
    }

    @Override
    public int getHeight(final World world) {
        return world.getMaxHeight() - this.getMinHeight(world);
    }

    @Override
    public void updateLightsAtChunk(final Object chunk) {
        final Chunk c = this.getNMSChunk(chunk);
        c.world.getChunkProvider().getLightEngine().a(c, true);
    }

    @Override
    public void updateChunk(final Object world, final Vec2<Integer> chunkLoc) {
        this.getNMSWorld(world).getChunkProvider().addTicket(
                TicketType.PLUGIN,
                new ChunkCoordIntPair(chunkLoc.getX(), chunkLoc.getY()),
                1,
                Unit.INSTANCE);
    }

    @Override
    public WrappedBlockData setBlockData2(final Object chunkSection, final Vec3<Integer> vec3i,
                                          final WrappedBlockData blockData,
                                          final boolean applyPhysics) {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public WrappedBlockData getBlockData2(final Object chunkSection, final Vec3<Integer> vec3i) {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public Object getNMSChunkSection(final Object chunkSection) {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public Object getNMSChunkSection2(final Object chunk, final int fixedY) {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public void updateLightsAtBlock(final Object world, final Vec3<Integer> pos) {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public void unloadChunk(final Object chunk, final boolean save) {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public Chunk loadChunk(final Object world, final Vec2<Integer> chunkLoc, final boolean generate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getDestroySpeed(final ItemStack itemStack, final BlockState blockState) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.getItem().getDestroySpeed(nmsItemStack, ((CraftBlockState) blockState).getHandle());
    }

    private <I, T> T getPDCData(final I itemStack, final PersistentDataType<T, T> type, final String id) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_16_R3.ItemStack) itemStack);
        else if (itemStack instanceof ItemStack)
            bktItemStack = (ItemStack) itemStack;
        else
            return null;

        final ItemMeta meta = bktItemStack.getItemMeta();
        if (meta == null) return null;

        return meta.getPersistentDataContainer().get(
                new NamespacedKey(this.plugin, id), type
        );
    }

    @SuppressWarnings("unchecked")
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
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(
                    new NamespacedKey(this.plugin, id),
                    type, value
            );
            bktItemStack.setItemMeta(itemMeta);
        }

        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            return (I) CraftItemStack.asNMSCopy(bktItemStack);
        else
            return (I) bktItemStack;
    }

    @SuppressWarnings("unchecked")
    public <T> T setNBT(final T itemStack, final String key, final Object value) {
        final net.minecraft.server.v1_16_R3.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            nmsItemStack = (net.minecraft.server.v1_16_R3.ItemStack) itemStack;
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

        if (itemStack instanceof net.minecraft.server.v1_16_R3.ItemStack)
            return (T) nmsItemStack;
        else
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
    }
}
