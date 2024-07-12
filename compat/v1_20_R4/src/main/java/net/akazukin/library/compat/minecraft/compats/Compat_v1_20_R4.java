package net.akazukin.library.compat.minecraft.compats;

import io.netty.channel.Channel;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.akazukin.library.compat.minecraft.Compat;
import net.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.compat.minecraft.v1_20_R4.PacketProcessor_v1_20_R4;
import net.akazukin.library.exception.UnsupportedOperationYetException;
import net.akazukin.library.utils.ObjectUtils;
import net.akazukin.library.utils.ReflectionUtils;
import net.akazukin.library.world.WrappedBlockData;
import net.akazukin.library.worldedit.Vec2;
import net.akazukin.library.worldedit.Vec2i;
import net.akazukin.library.worldedit.Vec3;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConnection;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_20_R4.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R4.CraftServer;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;

public class Compat_v1_20_R4 implements Compat {
    private final JavaPlugin plugin;
    public PacketProcessor_v1_20_R4 pktProcessor;

    public Compat_v1_20_R4(final JavaPlugin plugin) {
        this.pktProcessor = new PacketProcessor_v1_20_R4(this);
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
        ((CraftPlayer) player).getHandle().c.b(this.getNMSPacket(packet));
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
            final NetworkManager network = ReflectionUtils.getField(connection, ServerCommonPacketListenerImpl.class,
                    "e", NetworkManager.class);
            return network.n;
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Channel> getServerChannels() {
        final ServerConnection connection = ((CraftServer) Bukkit.getServer()).getServer().ai();
        try {
            final List<NetworkManager> networks = (List<NetworkManager>) ReflectionUtils.getField(connection, "g",
                    List.class);
            return networks.stream().map(network -> network.n).toList();
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sendSignUpdate(final Player player, final Location location, final String[] lines) {
        ((CraftPlayer) player).sendSignChange(location, lines);
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

        return nmsItemStack.a(DataComponents.b) != null;
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
        return null;
    }

    @Override
    @SuppressWarnings("null")
    public String getNBTString(final Object itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        if (!this.hasNBT(nmsItemStack) || !this.containsNBT(nmsItemStack, key)) return null;

        final NBTTagCompound nbt = ((NBTTagCompound) nmsItemStack.b(VanillaRegistries.a()));
        return nbt.e("akazukin") ? nbt.p("akazukin").l(key) : null;
    }

    @Override
    @SuppressWarnings("null")
    public Long getNBTLong(final Object itemStack, final String key) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        if (!this.hasNBT(nmsItemStack) || !this.containsNBT(nmsItemStack, key)) return null;

        final NBTTagCompound nbt = ((NBTTagCompound) nmsItemStack.b(VanillaRegistries.a()));
        return nbt.e("akazukin") ? nbt.p("akazukin").i(key) : null;
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

        final NBTTagCompound nbt = ((NBTTagCompound) nmsItemStack.b(VanillaRegistries.a()));
        return this.hasNBT(itemStack) && nbt.e("akazukin") && nbt.p("akazukin").e(key);
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

        CraftItemStack.asCraftCopy(CraftItemStack.asBukkitCopy(nmsItemStack)).getItemMeta().getPersistentDataContainer();

        if (!this.hasNBT(nmsItemStack)) return itemStack;

        final CustomData itemNBT = nmsItemStack.a(DataComponents.b);
        final NBTTagCompound tag = itemNBT.c();
        tag.r(key);
        nmsItemStack.b(DataComponents.b, CustomData.a(tag));

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
            final PlayerProfile profile = player.getPlayerProfile();
            final WrappedPlayerProfile profile_ = new WrappedPlayerProfile();
            profile_.setUniqueId(profile.getUniqueId());
            profile_.setName(profile.getName());
            if (profile.getTextures().getSkin() != null)
                profile_.setSkin(profile.getTextures().getSkin().getPath());
            profile_.setSkinModel(profile.getTextures().getSkinModel().name());
            if (profile.getTextures().getCape() != null)
                profile_.setSkin(profile.getTextures().getCape().getPath());
            profile_.setTimestamp(profile.getTextures().getTimestamp());
            return profile_;
        }
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
        return this.setPDCData(itemStack, PersistentDataType.BOOLEAN, key, value);
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

        return bktItemStack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this.plugin, key));
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
        return this.getPDCData(itemStack, PersistentDataType.BOOLEAN, key);
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

    @Override
    public WorldServer getNMSChunk(final World world) {
        return ((CraftWorld) world).getHandle();
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
    public Object setBlockDate(final Object chunk, final Vec3<Integer> vec3i, final WrappedBlockData blockData,
                               final boolean applyPhysics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WrappedBlockData getNMSNewBlockDate(final Material material, final byte data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Chunk getNMSChunk(final Object chunk) {
        if (chunk instanceof CraftChunk)
            return (Chunk) ((CraftChunk) chunk).getHandle(ChunkStatus.n);
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
    public void updateChunk(final Object world, final Vec2<Integer> chunkLoc) {
        this.getNMSWorld(world).l().a(
                TicketType.PLUGIN,
                new ChunkCoordIntPair(chunkLoc.getX(), chunkLoc.getY()),
                1,
                Unit.a);
    }

    @Override
    public Object setBlockDate2(final Object chunkSection, final Vec3<Integer> vec3i,
                                final WrappedBlockData blockData,
                                final boolean applyPhysics) {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public WrappedBlockData getBlockDate2(final Object chunkSection, final Vec3<Integer> vec3i) {
        throw new UnsupportedOperationYetException();
    }

    @Override
    public Object getNMSChunkSection(final Object chunkSection) {
        throw new UnsupportedOperationYetException();
    }

    private <I, R, T> T getPDCData(final I itemStack, final PersistentDataType<R, T> type, final String id) {
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

    private <I, R, T> I setPDCData(final I itemStack, final PersistentDataType<R, T> type, final String id,
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


        final CustomData itemNBT = nmsItemStack.a(DataComponents.b);
        final NBTTagCompound nbt = itemNBT != null ? itemNBT.c() : new NBTTagCompound();

        if (value instanceof Boolean)
            nbt.a(key, (Boolean) value);
        else if (value instanceof String)
            nbt.a(key, (String) value);
        else if (value instanceof Integer)
            nbt.a(key, (Integer) value);
        else if (value instanceof Long)
            nbt.a(key, (Long) value);
        else if (value instanceof Byte)
            nbt.a(key, (Byte) value);
        else if (value instanceof Short)
            nbt.a(key, (Short) value);
        else if (value instanceof Double)
            nbt.a(key, (Double) value);
        else if (value instanceof UUID)
            nbt.a(key, (UUID) value);

        nmsItemStack.b(DataComponents.b, CustomData.a(nbt));


        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            return (T) nmsItemStack;
        else if (itemStack instanceof ItemStack)
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        else
            return null;
    }
}
