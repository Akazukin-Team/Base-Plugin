package net.akazukin.library.gui;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.minecraft.data.packets.COpenSignEditorPacket;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.compat.minecraft.data.packets.SUpdateSignPacket;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import net.akazukin.library.event.events.PacketReceiveEvent;
import net.akazukin.library.event.events.PacketSendEvent;
import net.akazukin.library.gui.screens.chat.ChatGui;
import net.akazukin.library.gui.screens.chest.ContainerGuiBase;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.gui.screens.sign.SignStringSelectorGui;
import net.akazukin.library.utils.InventoryUtils;
import net.akazukin.library.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
public class GuiManager implements Listenable {
    private static final GuiManager SINGLETON = new GuiManager();
    private final HashMap<UUID, GuiBase> screens = new HashMap<>();

    public static GuiManager singleton() {
        return SINGLETON;
    }

    public GuiBase getScreen(final UUID player) {
        return this.screens.get(player);
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getCurrentItem() != null && ItemUtils.isGuiItem(event.getCurrentItem())) {
            event.setCancelled(true);
        }

        if (event.getCurrentItem() == null) return;

        final GuiBase gui = this.screens.get(event.getWhoClicked().getUniqueId());
        if (!(gui instanceof ContainerGuiBase) || !event.getView().getTitle().equals(((ContainerGuiBase) gui).getTitle()))
            return;

        if (event.getView().getType() == InventoryType.CHEST && event.getCurrentItem() != null && !ItemUtils.isGuiItem(event.getCurrentItem())) {
            LibraryPlugin.getLogManager().warning("Not cancelled  | Title: " + event.getView().getTitle() + "  | " +
                    "DisplayName: " + event.getCurrentItem().getItemMeta().getDisplayName());
        }

        if (InventoryUtils.isCloseItem(event.getCurrentItem())) {
            event.getWhoClicked().closeInventory();
        } else if (InventoryUtils.isBackItem(event.getCurrentItem()) && gui.getPrevGui() != null) {
            event.getWhoClicked().closeInventory();
            this.setScreen(event.getWhoClicked(), gui::getPrevGui);
        } else {
            ((ContainerGuiBase) gui).onInventoryClick(event);
        }
    }

    public void setScreen(final HumanEntity player, final Supplier<GuiBase> gui) {
        Bukkit.getScheduler().runTask(LibraryPlugin.getPlugin(), () -> {
            if (player == null) return;
            player.closeInventory();

            final GuiBase guI = gui.get();
            this.screens.remove(player);
            this.screens.put(player.getUniqueId(), guI);
            guI.forceOpen();
        });
    }

    @EventTarget
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final GuiBase gui = this.screens.get(event.getPlayer().getUniqueId());
        if (!(gui instanceof ContainerGuiBase) || !event.getView().getTitle().equals(((ContainerGuiBase) gui).getTitle()))
            return;

        ((ContainerGuiBase) gui).onInventoryOpen(event);
    }

    @EventTarget
    public void onInventoryClose(final InventoryCloseEvent event) {
        final GuiBase gui = this.screens.get(event.getPlayer().getUniqueId());
        if (!(gui instanceof ContainerGuiBase) || !event.getView().getTitle().equals(((ContainerGuiBase) gui).getTitle()))
            return;

        this.screens.remove(event.getPlayer().getUniqueId());
        ((ContainerGuiBase) gui).onInventoryClose(event);
    }

    @EventTarget
    public void onPlayerMove(final PlayerMoveEvent event) {
        final GuiBase prevGui = this.screens.get(event.getPlayer().getUniqueId());
        if (prevGui instanceof ChatGui) ((ChatGui) prevGui).onPlayerMove(event);
    }

    @EventTarget
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final GuiBase prevGui = this.screens.get(event.getPlayer().getUniqueId());
        if (prevGui instanceof ChatGui) ((ChatGui) prevGui).onChat(event);
    }

    @EventTarget
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.screens.remove(event.getPlayer().getUniqueId());
    }

    @EventTarget
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getClient().getPlayer() == null) return;

        final GuiBase gui = this.screens.get(event.getClient().getPlayer().getUniqueId());
        if (gui instanceof SignStringSelectorGui) {
            final Packet pkt = LibraryPlugin.COMPAT.getWrappedPacket(event.getPacket());
            if (pkt instanceof SUpdateSignPacket) {
                ((SignStringSelectorGui) gui).onGuiClose(event);
                if (gui.getPrevGui() == null) {
                    this.screens.remove(event.getClient().getPlayer().getUniqueId());
                } else {
                    this.screens.put(event.getClient().getPlayer().getUniqueId(), gui.getPrevGui());
                    Bukkit.getScheduler().runTask(LibraryPlugin.getPlugin(), () ->
                            this.screens.get(event.getClient().getPlayer().getUniqueId()).forceOpen());
                }
            }
        }
    }

    @EventTarget
    public void onPacketSend(final PacketSendEvent event) {
        if (event.getClient().getPlayer() == null) return;

        final GuiBase gui = this.screens.get(event.getClient().getPlayer().getUniqueId());
        if (gui instanceof SignStringSelectorGui) {
            final Packet pkt = LibraryPlugin.COMPAT.getWrappedPacket(event.getPacket());
            if (pkt instanceof COpenSignEditorPacket) {
                ((SignStringSelectorGui) gui).onGuiOpen();
            }
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
