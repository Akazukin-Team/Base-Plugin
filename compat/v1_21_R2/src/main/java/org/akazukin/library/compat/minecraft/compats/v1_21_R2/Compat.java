package org.akazukin.library.compat.minecraft.compats.v1_21_R2;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConnection;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunkExtension;
import net.minecraft.world.level.chunk.status.ChunkStatus;
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
import org.bukkit.craftbukkit.v1_21_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_21_R2.CraftServer;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R2.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_21_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class Compat implements ICompat {
    private final JavaPlugin plugin;
    public PacketProcessor pktProcessor;

    public Compat(final JavaPlugin plugin) {
        this.pktProcessor = new PacketProcessor(this);
        this.plugin = plugin;
    }

    @Override
    public float getAttackCooldown(final Player player) {
        return this.getNMSPlayer(player).H(0.5f);
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
        for (final Method method : inventory.getClass().getMethods()) {
            System.out.println(method.getName());
        }
        System.out.println(((CraftInventory) inventory).getInventory());
        if (!(((CraftInventory) inventory).getInventory() instanceof ContainerAnvil)) {
            return null;
        }
        System.out.println("create Instance");
        return new WrappedAnvilInventory(
                inventory,
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).x,
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).y.b(),
                ((ContainerAnvil) ((CraftInventory) inventory).getInventory()).maximumRepairCost
        );
    }

    @Override
    public Inventory getBukkitAnvil(final WrappedAnvilInventory inventory) {
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).x = inventory.getRenameText();
        ((ContainerAnvil) ((CraftInventory) inventory.getInventory()).getInventory()).y.a(inventory.getRepairCost());
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
    public void sendPacket(@Nonnull final Player player, @Nonnull final Packet packet) {
        this.getNMSPlayer(player).f.b(this.getNMSPacket(packet));
    }

    @Override
    public Vec3i getWrappedBlockPos(final Object pos) {
        if (!(pos instanceof final BlockPosition pos2)) {
            throw new IllegalArgumentException("Invalid argument position must be a block position");
        }

        return new Vec3i(pos2.u(), pos2.v(), pos2.w());
    }

    @Override
    public BlockPosition getNMSBlockPos(final Vec3<Integer> position) {
        return new BlockPosition(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Channel getPlayerChannel(final Player player) {
        final PlayerConnection connection = this.getNMSPlayer(player).f;
        try {
            final NetworkManager network = ReflectionUtils.getField(connection, ServerCommonPacketListenerImpl.class,
                    "e", NetworkManager.class);
            return network.n;
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Channel[] getServerChannels() {
        final ServerConnection connection = ((CraftServer) Bukkit.getServer()).getServer().ah();
        try {
            final List<NetworkManager> networks = (List<NetworkManager>) ReflectionUtils.getField(connection, "g",
                    List.class);
            return new ArrayList<>(networks).stream().filter(Objects::nonNull).map(network -> network.n).toArray(Channel[]::new);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sendSignUpdate(final Player player, final Location location, final String[] lines) {
        player.sendSignChange(location, lines);
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

        return nmsItemStack.a(DataComponents.b) != null;
    }

    @Override
    public <T> T setNBT(final T itemStack, final String key, final String value) {
        return this.setNBT(itemStack, key, (Object) value);
    }

    private <T> T setNBT(final T itemStack, final String key, final Object value) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        } else if (itemStack instanceof ItemStack) {
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        } else {
            return null;
        }


        final CustomData itemNBT = nmsItemStack.a(DataComponents.b);
        final NBTTagCompound nbt = itemNBT != null ? itemNBT.c() : new NBTTagCompound();

        if (value instanceof Boolean) {
            nbt.a(key, (Boolean) value);
        } else if (value instanceof String) {
            nbt.a(key, (String) value);
        } else if (value instanceof Integer) {
            nbt.a(key, (Integer) value);
        } else if (value instanceof Long) {
            nbt.a(key, (Long) value);
        } else if (value instanceof Byte) {
            nbt.a(key, (Byte) value);
        } else if (value instanceof Short) {
            nbt.a(key, (Short) value);
        } else if (value instanceof Double) {
            nbt.a(key, (Double) value);
        } else if (value instanceof UUID) {
            nbt.a(key, (UUID) value);
        }

        nmsItemStack.b(DataComponents.b, CustomData.a(nbt));


        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            return (T) nmsItemStack;
        } else if (itemStack instanceof ItemStack) {
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        } else {
            return null;
        }
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
        throw new UnsupportedOperationYetException();
    }

    @Override
    @SuppressWarnings("null")
    public String getNBTString(final Object itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        } else if (itemStack instanceof ItemStack) {
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        } else {
            return null;
        }

        if (!this.hasNBT(nmsItemStack) || !this.containsNBT(nmsItemStack, key)) {
            return null;
        }

        final NBTTagCompound nbt = ((NBTTagCompound) nmsItemStack.b(VanillaRegistries.a()));
        return nbt.e("akazukin") ? nbt.p("akazukin").l(key) : null;
    }

    @Override
    @SuppressWarnings("null")
    public Long getNBTLong(final Object itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        } else if (itemStack instanceof ItemStack) {
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        } else {
            return null;
        }

        if (!this.hasNBT(nmsItemStack) || !this.containsNBT(nmsItemStack, key)) {
            return null;
        }

        final NBTTagCompound nbt = ((NBTTagCompound) nmsItemStack.b(VanillaRegistries.a()));
        return nbt.e("akazukin") ? nbt.p("akazukin").i(key) : null;
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

        final NBTTagCompound nbt = ((NBTTagCompound) nmsItemStack.b(VanillaRegistries.a()));
        return this.hasNBT(itemStack) && nbt.e("akazukin") && nbt.p("akazukin").e(key);
    }

    @Override
    public <T> T removeNBT(final T itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        } else if (itemStack instanceof ItemStack) {
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        } else {
            return null;
        }

        CraftItemStack.asCraftCopy(CraftItemStack.asBukkitCopy(nmsItemStack)).getItemMeta().getPersistentDataContainer();

        if (!this.hasNBT(nmsItemStack)) {
            return itemStack;
        }

        final CustomData itemNBT = nmsItemStack.a(DataComponents.b);
        final NBTTagCompound tag = itemNBT.c();
        tag.r(key);
        nmsItemStack.b(DataComponents.b, CustomData.a(tag));

        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            return (T) nmsItemStack;
        } else if (itemStack instanceof ItemStack) {
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        } else {
            return null;
        }
    }

    @Override
    public WrappedPlayerProfile getGameProfile(final Player player) {
        final PlayerProfile profile = player.getPlayerProfile();
        final WrappedPlayerProfile profile_ = new WrappedPlayerProfile();
        profile_.setUniqueId(profile.getUniqueId());
        profile_.setName(profile.getName());
        if (profile.getTextures().getSkin() != null) {
            profile_.setSkin(profile.getTextures().getSkin().getPath());
        }
        profile_.setSkinModel(profile.getTextures().getSkinModel().name());
        if (profile.getTextures().getCape() != null) {
            profile_.setSkin(profile.getTextures().getCape().getPath());
        }
        profile_.setTimestamp(profile.getTextures().getTimestamp());
        return profile_;
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

    private <I, R, T> I setPDCData(final I itemStack, final PersistentDataType<R, T> type, final String id,
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
        itemMeta.getPersistentDataContainer().set(
                new NamespacedKey(this.plugin, id),
                type, value
        );
        bktItemStack.setItemMeta(itemMeta);

        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            return (I) CraftItemStack.asNMSCopy(bktItemStack);
        } else if (itemStack instanceof ItemStack) {
            return (I) bktItemStack;
        } else {
            return null;
        }
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
        return this.setPDCData(itemStack, PersistentDataType.BOOLEAN, key, value);
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

    private <I, R, T> T getPDCData(final I itemStack, final PersistentDataType<R, T> type, final String id) {
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

    @Override
    public Boolean getPDCDataAsBool(final Object itemStack, final String key) {
        return this.getPDCData(itemStack, PersistentDataType.BOOLEAN, key);
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
        itemMeta.getPersistentDataContainer().remove(
                new NamespacedKey(this.plugin, key)
        );
        bktItemStack.setItemMeta(itemMeta);

        if (itemStack instanceof net.minecraft.world.item.ItemStack) {
            return (I) CraftItemStack.asNMSCopy(bktItemStack);
        } else if (itemStack instanceof ItemStack) {
            return (I) bktItemStack;
        } else {
            return null;
        }
    }

    @Override
    public Chunk getNMSChunk(final Object world, final Vec2<Integer> chunkLoc) {
        return this.getNMSWorld(world).d(chunkLoc.getX(), chunkLoc.getY());
    }

    @Override
    public Chunk getNMSChunk(final Object world, final Vec3<Integer> loc) {
        return this.getNMSChunk(world, new Vec2i(loc.getX() >> 4, loc.getZ() >> 4));
    }

    @Override
    public Object setBlockData(final Object chunk, final Vec3<Integer> vec3i, final WrappedBlockData blockData,
                               final boolean applyPhysics) {
        final ChunkSection cs = this.getNMSChunkSection(chunk, vec3i.getY());
        return this.setWrappedBlockData(cs, vec3i, blockData, applyPhysics);
    }

    @Override
    public WrappedBlockData getNMSNewBlockData(final Material material, final byte data) {
        return new WrappedBlockData(CraftMagicNumbers.getBlock(material, data));
    }

    @Override
    public Chunk getNMSChunk(final Object chunk) {
        if (chunk instanceof CraftChunk) {
            return (Chunk) ((CraftChunk) chunk).getHandle(ChunkStatus.n);
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
        return this.getNMSChunkSection2(chunk, y >> 4);
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
        c.r.m().a().a(c, true);
    }

    @Override
    public void updateChunk(final Object world, final Vec2<Integer> chunkLoc) {
        this.getNMSWorld(world).m().a(
                TicketType.PLUGIN,
                new ChunkCoordIntPair(chunkLoc.getX(), chunkLoc.getY()),
                1,
                Unit.a);
    }

    @Override
    public WrappedBlockData setWrappedBlockData(final Object chunkSection, final Vec3<Integer> vec3i,
                                                final WrappedBlockData blockData,
                                                final boolean applyPhysics) {
        final ChunkSection cs = this.getNMSChunkSection(chunkSection);

        return new WrappedBlockData(cs.a(vec3i.getX() & 15, vec3i.getY() & 15, vec3i.getZ() & 15,
                ((IBlockData) blockData.getBlockData()),
                applyPhysics));
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
        return cs.a(fixedVec3i.getX(), fixedVec3i.getY(), fixedVec3i.getZ());
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
    public ChunkSection getNMSChunkSection2(final Object chunk, final int fixedY) {
        final Chunk c = this.getNMSChunk(chunk);
        return c.b(fixedY);
    }

    @Override
    public void updateLightsAtBlock(final Object world, final Vec3<Integer> pos) {
        final WorldServer c = this.getNMSWorld(world);
        c.C_().a(new BlockPosition(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Override
    public void unloadChunk(final Object chunk, final boolean save) {
        final Chunk c = this.getNMSChunk(chunk);
        if (!save || c.r.m().a.a(c)) {
            c.r.m().a.p.remove(c.f().a());
        }
    }

    @Override
    public Chunk loadChunk(final Object world, final Vec2<Integer> chunkLoc, final boolean generate) {
        final ChunkProviderServer provider = this.getNMSWorld(world).m();

        IChunkAccess chunk = provider.a(chunkLoc.getX(), chunkLoc.getY(),
                generate ? ChunkStatus.n : ChunkStatus.c, true);
        if (chunk instanceof ProtoChunkExtension) {
            chunk = provider.a(chunkLoc.getX(), chunkLoc.getY(), ChunkStatus.n, true);
        }

        if (chunk instanceof net.minecraft.world.level.chunk.Chunk) {
            provider.a(TicketType.PLUGIN, chunk.f(), 1, Unit.a);
        }
        return (Chunk) chunk;
    }

    @Override
    public float getDestroySpeed(final ItemStack itemStack, final BlockState blockState) {
        final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.h().a(nmsItemStack, ((CraftBlockState) blockState).getHandle());
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
        return iBlockData.b().toString();
    }

    @Override
    public boolean isChunkViewing(final Object player, final Object world, final Vec2<Integer> chunkPos) {
        final WorldServer w = this.getNMSWorld(world);
        final EntityPlayer p = this.getNMSPlayer(player);

        if (!Objects.equals(p.y().uuid, w.uuid)
                || !w.m().isChunkLoaded(chunkPos.getX(), chunkPos.getY())) {
            return false;
        }
        return w.m().a.a(new ChunkCoordIntPair(chunkPos.getX(), chunkPos.getY()), false)
                .stream()
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
}
