package org.akazukin.library.compat.minecraft.compats.v1_17_R1;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.ServerConnection;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import org.akazukin.library.compat.minecraft.CompatExceptions;
import org.akazukin.library.compat.minecraft.ICompat;
import org.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import org.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import org.akazukin.library.compat.minecraft.data.packets.Packet;
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
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Compat implements ICompat {
    private final PacketProcessor pktProcessor;
    private final JavaPlugin plugin;

    public Compat(final JavaPlugin plugin) {
        this.pktProcessor = new PacketProcessor(this);
        this.plugin = plugin;
    }

    @Override
    public float getAttackCooldown(final Player player) {
        return this.getNMSPlayer(player).getAttackCooldown(0.5f);
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
        if (!(inventory instanceof CraftInventory)) {
            return null;
        }
        if (!(((CraftInventory) inventory).getInventory() instanceof ContainerAnvil)) {
            return null;
        }

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
    public void sendPacket(@Nonnull final Player player, @Nonnull final Packet packet) {
        this.getNMSPlayer(player).b.sendPacket(this.getNMSPacket(packet));
    }

    @Override
    public Vec3i getWrappedBlockPos(final Object pos) {
        if (!(pos instanceof final BlockPosition pos2)) {
            throw new IllegalArgumentException("Invalid argument position must be a block position");
        }

        return new Vec3i(pos2.getX(), pos2.getY(), pos2.getZ());
    }

    @Override
    public BlockPosition getNMSBlockPos(final Vec3<Integer> pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public Channel getPlayerChannel(final Player player) {
        return this.getNMSPlayer(player).b.a.k;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Channel[] getServerChannels() {
        final ServerConnection connection = ((CraftServer) Bukkit.getServer()).getServer().getServerConnection();
        try {
            final List<NetworkManager> networks =
                    (List<NetworkManager>) ReflectionUtils.getField(connection, "g", List.class);
            return new ArrayList<>(networks).stream().filter(Objects::nonNull).map(network -> network.k).toArray(Channel[]::new);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    @Override
    public void sendSignUpdate(final Player player, final Location location, final String[] result) {
        player.sendSignChange(location, result);
    }

    @Override
    public boolean hasNBT(final Object itemStack) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        } else if (itemStack instanceof ItemStack) {
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        } else {
            throw new IllegalStateException(CompatExceptions.ITEM_STACK_IS_NOT_ALLOWED_CLASS);
        }

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
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        } else if (itemStack instanceof ItemStack) {
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        } else {
            return null;
        }

        return this.containsNBT(itemStack, key) ? nmsItemStack.getTag().getString(key) : null;
    }

    @Override
    public Long getNBTLong(final Object itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        } else if (itemStack instanceof ItemStack) {
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        } else {
            return null;
        }

        return this.containsNBT(nmsItemStack, key) ? nmsItemStack.getTag().getLong(key) : null;
    }

    @Override
    public Boolean getNBTBoolean(final Object itemStack, final String key) {
        return ObjectUtils.getAsBoolean(this.getNBTString(itemStack, key));
    }

    @Override
    public Boolean containsNBT(final Object itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        } else if (itemStack instanceof ItemStack) {
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        } else {
            return null;
        }

        return this.hasNBT(nmsItemStack) && nmsItemStack.getTag().hasKey(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T removeNBT(final T itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        } else if (itemStack instanceof ItemStack) {
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        } else {
            return null;
        }

        if (!this.containsNBT(itemStack, key)) {
            return itemStack;
        }

        @Nullable final NBTTagCompound tag = nmsItemStack.getTag();
        if (tag != null) {
            tag.remove(key);
        }

        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            return (T) nmsItemStack;
        } else {
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        }
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
            if (textures_.has("SKIN")) {
                profile_.setSkin(textures_
                        .getAsJsonObject("SKIN")
                        .get("url")
                        .getAsString());
            }
            if (textures_.has("SKIN") &&
                    textures_.getAsJsonObject("SKIN").has("metadata")) {
                profile_.setSkinModel(textures_
                        .getAsJsonObject("SKIN")
                        .getAsJsonObject("metadata")
                        .getAsJsonObject("model")
                        .getAsString()
                        .equals("slim") ? "SLIM" : "CLASSIC");
            }
            if (textures_.has("CAPE")) {
                profile_.setCape(textures_
                        .getAsJsonObject("CAPE")
                        .get("url")
                        .getAsString());
            }
            profile_.setTimestamp(textures__.get("timestamp").getAsLong());
            return profile_;
        }
        /*try {
            ReflectionUtils.getField(this.getNMSPlayer(player), EntityHuman.class, "cs", GameProfile.class);
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
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) itemStack);
        } else if (itemStack instanceof ItemStack) {
            bktItemStack = (ItemStack) itemStack;
        } else {
            throw new IllegalStateException(CompatExceptions.ITEM_STACK_IS_NOT_ALLOWED_CLASS);
        }

        final ItemMeta meta = bktItemStack.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().getKeys().contains(new NamespacedKey(this.plugin, key));
    }

    @Override
    public Integer getPDCDataAsInt(final Object itemStack, final String key) {
        return this.getPDCData(itemStack, PersistentDataType.INTEGER, key);
    }

    @Override
    public String getPDCDataAsString(final Object itemStack, final String key) {
        return this.getPDCData(itemStack, PersistentDataType.STRING, key);
    }

    @Override
    public Boolean getPDCDataAsBool(final Object itemStack, final String key) {
        return ObjectUtils.getAsBoolean(this.getPDCData(itemStack, PersistentDataType.BYTE, key));
    }

    @Override
    public Long getPDCDataAsLong(final Object itemStack, final String key) {
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
        return this.getPDCDataAsString(itemStack, key);
    }

    @Override
    public Integer getPlDataInt(final Object itemStack, final String key) {
        return this.getPDCDataAsInt(itemStack, key);
    }

    @Override
    public Long getPlDataLong(final Object itemStack, final String key) {
        return this.getPDCDataAsLong(itemStack, key);
    }

    @Override
    public Boolean getPlDataBool(final Object itemStack, final String key) {
        return this.getPDCDataAsBool(itemStack, key);
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
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) itemStack);
        } else if (itemStack instanceof ItemStack) {
            bktItemStack = (ItemStack) itemStack;
        } else {
            return null;
        }

        final ItemMeta itemMeta = bktItemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().remove(
                    new NamespacedKey(this.plugin, key)
            );
            bktItemStack.setItemMeta(itemMeta);
        }

        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            return (I) CraftItemStack.asNMSCopy(bktItemStack);
        } else {
            return (I) bktItemStack;
        }
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
        if (chunk instanceof CraftChunk) {
            return ((CraftChunk) chunk).getHandle();
        } else if (chunk instanceof Chunk) {
            return (Chunk) chunk;
        } else {
            return null;
        }
    }

    @Override
    public WorldServer getNMSWorld(final Object world) {
        if (world instanceof World) {
            return ((CraftWorld) world).getHandle();
        } else if (world instanceof WorldServer) {
            return (WorldServer) world;
        } else {
            return null;
        }
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
        c.i.getChunkProvider().getLightEngine().a(c, true);
    }

    @Override
    public void updateChunk(final Object world, final Vec2<Integer> chunkLoc) {
        this.getNMSWorld(world).getChunkProvider().addTicket(
                TicketType.PLUGIN,
                new ChunkCoordIntPair(chunkLoc.getX(), chunkLoc.getY()),
                1,
                Unit.a);
    }

    @Override
    public WrappedBlockData setWrappedBlockData(final Object chunkSection, final Vec3<Integer> vec3i,
                                                final WrappedBlockData blockData,
                                                final boolean applyPhysics) {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public WrappedBlockData getWrappedBlockData(final Object chunkSection, final Vec3<Integer> vec3i) {
        return new WrappedBlockData(this.getBlockData(chunkSection, vec3i));
    }

    @Override
    public IBlockData getBlockData(final Object blockData) {
        if (blockData instanceof IBlockData) {
            return (IBlockData) blockData;
        } else if (blockData instanceof CraftBlockState) {
            return ((CraftBlockState) blockData).getHandle();
        } else if (blockData instanceof CraftBlockData) {
            return ((CraftBlockData) blockData).getState();
        } else {
            throw new IllegalArgumentException("blockData must be an instance of IBlockData, CraftBlockData or CraftBlockState");
        }
    }

    @Override
    public IBlockData getBlockData(final Object chunkSection, final Vec3<Integer> vec3i) {
        return this.getBlockData2(chunkSection, new Vec3i(vec3i.getX() & 15, vec3i.getY() & 15, vec3i.getZ() & 15));
    }

    @Override
    public IBlockData getBlockData2(final Object chunkSection, final Vec3<Integer> fixedVec3i) {
        final ChunkSection cs = this.getNMSChunkSection(chunkSection);
        return cs.getType(fixedVec3i.getX(), fixedVec3i.getY(), fixedVec3i.getZ());
    }

    @Override
    public ChunkSection getNMSChunkSection(final Object chunkSection) {
        if (chunkSection instanceof ChunkSection) {
            return (ChunkSection) chunkSection;
        } else {
            throw new IllegalArgumentException("chunkSection is not a chunk section");
        }
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
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.getItem().getDestroySpeed(nmsItemStack, ((CraftBlockState) blockState).getHandle());
    }

    @Override
    public String getBlockName(final Object blockState) {
        final IBlockData iBlockData;
        if (blockState instanceof IBlockData) {
            iBlockData = (IBlockData) blockState;
        } else if (blockState instanceof CraftBlockState) {
            iBlockData = ((CraftBlockState) blockState).getHandle();
        } else {
            throw new IllegalArgumentException("Invalid block state: " + blockState);
        }
        return iBlockData.getBlock().h();
    }

    @Override
    public boolean isChunkViewing(final Object player, final Object world, final Vec2<Integer> chunkPos) {
        final WorldServer w = this.getNMSWorld(world);
        final EntityPlayer p = this.getNMSPlayer(player);

        if (!Objects.equals(p.getWorldServer().uuid, w.uuid)
                || !w.getChunkProvider().isChunkLoaded(chunkPos.getX(), chunkPos.getY())) {
            return false;
        }
        return w.getChunkProvider().a.a(new ChunkCoordIntPair(chunkPos.getX(), chunkPos.getY()), false)
                .anyMatch(eP -> Objects.equals(p, eP));
    }

    @Override
    public EntityPlayer getNMSPlayer(final Object player) {
        if (player instanceof CraftPlayer) {
            return ((CraftPlayer) player).getHandle();
        } else if (player instanceof EntityPlayer) {
            return (EntityPlayer) player;
        } else {
            throw new IllegalArgumentException("player is not a supported Player instance");
        }
    }

    private <I, T> T getPDCData(final I itemStack, final PersistentDataType<T, T> type, final String id) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) itemStack);
        } else if (itemStack instanceof ItemStack) {
            bktItemStack = (ItemStack) itemStack;
        } else {
            return null;
        }

        final ItemMeta meta = bktItemStack.getItemMeta();
        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer().get(
                new NamespacedKey(this.plugin, id), type
        );
    }

    @SuppressWarnings("unchecked")
    private <I, T> I setPDCData(final I itemStack, final PersistentDataType<T, T> type, final String id,
                                final T value) {
        final ItemStack bktItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            bktItemStack = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) itemStack);
        } else if (itemStack instanceof ItemStack) {
            bktItemStack = (ItemStack) itemStack;
        } else {
            return null;
        }

        final ItemMeta itemMeta = bktItemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(
                    new NamespacedKey(this.plugin, id),
                    type, value
            );
            bktItemStack.setItemMeta(itemMeta);
        }

        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            return (I) CraftItemStack.asNMSCopy(bktItemStack);
        } else {
            return (I) bktItemStack;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T setNBT(final T itemStack, final String key, final Object value) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        } else if (itemStack instanceof ItemStack) {
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        } else {
            return null;
        }

        final NBTTagCompound nbt = nmsItemStack.getOrCreateTag();
        if (value instanceof Boolean) {
            nbt.setBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            nbt.setString(key, (String) value);
        } else if (value instanceof Integer) {
            nbt.setInt(key, (Integer) value);
        } else if (value instanceof Long) {
            nbt.setLong(key, (Long) value);
        } else if (value instanceof Byte) {
            nbt.setByte(key, (Byte) value);
        } else if (value instanceof Short) {
            nbt.setShort(key, (Short) value);
        } else if (value instanceof Double) {
            nbt.setDouble(key, (Double) value);
        } else if (value instanceof UUID) {
            nbt.a(key, (UUID) value);
        }

        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            return (T) nmsItemStack;
        } else {
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        }
    }
}
