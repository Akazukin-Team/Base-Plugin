package net.akazukin.library.compat.minecraft.compats;

import io.netty.channel.Channel;
import java.lang.reflect.Method;
import java.util.List;
import net.akazukin.library.compat.minecraft.Compat;
import net.akazukin.library.compat.minecraft.data.WrappedAnvilInventory;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.compat.minecraft.v1_20_R4.PacketProcessor_v1_20_R4;
import net.akazukin.library.utils.ReflectionUtils;
import net.akazukin.library.utils.StringUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConnection;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_20_R4.CraftServer;
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
    public void sendPacket(final Player player, final Packet packet) {
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
    public <T> T setNBT(final T itemStack, final String id, final String value) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        final CustomData itemNBT = nmsItemStack.a(DataComponents.b);
        final NBTTagCompound tag = itemNBT != null ? itemNBT.c() : new NBTTagCompound();
        tag.a(id, value);
        nmsItemStack.b(DataComponents.b, CustomData.a(tag));

        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            return (T) nmsItemStack;
        else if (itemStack instanceof ItemStack)
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        else
            return null;
    }

    @Override
    public <T> T setNBT(final T itemStack, final String id, final long value) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        final CustomData itemNBT = nmsItemStack.a(DataComponents.b);
        final NBTTagCompound tag = itemNBT != null ? itemNBT.c() : new NBTTagCompound();
        tag.a(id, value);
        nmsItemStack.b(DataComponents.b, CustomData.a(tag));

        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            return (T) nmsItemStack;
        else if (itemStack instanceof ItemStack)
            return (T) CraftItemStack.asBukkitCopy(nmsItemStack);
        else
            return null;
    }

    @Override
    public <T> T setNBT(final T itemStack, final String id, final boolean value) {
        return this.setNBT(itemStack, id, String.valueOf(value));
    }

    @Override
    @SuppressWarnings("null")
    public String getNBTString(final Object itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        if (!this.hasNBT(nmsItemStack) || !this.containsNBT(nmsItemStack, id)) return null;

        final NBTTagCompound nbt = ((NBTTagCompound) nmsItemStack.b(VanillaRegistries.a()));
        return nbt.e("akazukin") ? nbt.p("akazukin").l(id) : null;
    }

    @Override
    @SuppressWarnings("null")
    public Long getNBTLong(final Object itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        if (!this.hasNBT(nmsItemStack) || !this.containsNBT(nmsItemStack, id)) return null;

        final NBTTagCompound nbt = ((NBTTagCompound) nmsItemStack.b(VanillaRegistries.a()));
        return nbt.e("akazukin") ? nbt.p("akazukin").i(id) : null;
    }

    @Override
    public Boolean getNBTBoolean(final Object itemStack, final String id) {
        return StringUtils.getBoolean(this.getNBTString(itemStack, id));
    }

    @Override
    public Boolean containsNBT(final Object itemStack, final String id) {
        final net.minecraft.world.item.ItemStack nmsItemStack;
        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            nmsItemStack = (net.minecraft.world.item.ItemStack) itemStack;
        else if (itemStack instanceof ItemStack)
            nmsItemStack = CraftItemStack.asNMSCopy((ItemStack) itemStack);
        else
            return null;

        final NBTTagCompound nbt = ((NBTTagCompound) nmsItemStack.b(VanillaRegistries.a()));
        return this.hasNBT(itemStack) && nbt.e("akazukin") && nbt.p("akazukin").e(id);
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
    public <I> I setPDCData(final I itemStack, final String id, final String value) {
        return this.setPDCData(itemStack, PersistentDataType.STRING, id, value);
    }

    @Override
    public <I> I setPDCData(final I itemStack, final String id, final Integer value) {
        return this.setPDCData(itemStack, PersistentDataType.INTEGER, id, value);
    }

    @Override
    public Integer getIntPDCData(final Object itemStack, final String id) {
        return this.getPDCData(itemStack, PersistentDataType.INTEGER, id);
    }

    @Override
    public String getStringPDCData(final Object itemStack, final String id) {
        return this.getPDCData(itemStack, PersistentDataType.STRING, id);
    }

    @Override
    public <I> I setPlData(I itemStack, String key, String value) {
        return setPDCData(itemStack, key, value);
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

        ItemMeta itemMeta = bktItemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(
                new NamespacedKey(this.plugin, id),
                type, value
        );
        bktItemStack.setItemMeta(itemMeta);

        if (itemStack instanceof net.minecraft.world.item.ItemStack)
            return (I) bktItemStack;
        else if (itemStack instanceof ItemStack)
            return (I) CraftItemStack.asNMSCopy(bktItemStack);
        else
            return null;
    }

    @Override
    public <I> I setPlData(I itemStack, String key, Integer value) {
        return setPDCData(itemStack, key, value);
    }

    @Override
    public String getStringPlData(Object itemStack, String key) {
        return getStringPDCData(itemStack, key);
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

    @Override
    public Integer getIntPlData(Object itemStack, String key) {
        return getIntPDCData(itemStack, key);
    }
}
