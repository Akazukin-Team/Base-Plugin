package net.akazukin.library.gui;

import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.minecraft.data.packets.CUpdateSignPacket;
import net.akazukin.library.compat.minecraft.data.packets.Packet;
import net.akazukin.library.compat.minecraft.data.packets.SOpenSignEditorPacket;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import net.akazukin.library.event.events.PacketReceiveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class GuiManager implements Listenable {
    private static final GuiManager SINGLETON = new GuiManager();
    private final HashMap<UUID, GuiBase> screens = new HashMap<>();

    public static GuiManager singleton() {
        return SINGLETON;
    }

    public void setScreen(final UUID player, final GuiBase gui) {
        final Player player_ = Bukkit.getPlayer(player);
        player_.closeInventory();
        screens.remove(player);
        gui.forceOpen();
        screens.put(player, gui);
    }

    public GuiBase getScreen(final UUID player) {
        return screens.get(player);
    }

    @EventTarget
    public void onInventoryClick(final InventoryClickEvent event) {
        final GuiBase gui = screens.get(event.getWhoClicked().getUniqueId());
        if (!(gui instanceof ContainerGuiBase) || !event.getView().getTitle().equals(((ContainerGuiBase) gui).getTitle()))
            return;

        ((ContainerGuiBase) gui).onInventoryClick(event);
    }

    @EventTarget
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final GuiBase gui = screens.get(event.getPlayer().getUniqueId());
        if (!(gui instanceof ContainerGuiBase) || !event.getView().getTitle().equals(((ContainerGuiBase) gui).getTitle()))
            return;

        ((ContainerGuiBase) gui).onInventoryOpen(event);
    }

    @EventTarget
    public void onInventoryClose(final InventoryCloseEvent event) {
        final GuiBase gui = screens.get(event.getPlayer().getUniqueId());
        if (!(gui instanceof ContainerGuiBase) || !event.getView().getTitle().equals(((ContainerGuiBase) gui).getTitle()))
            return;

        screens.remove(event.getPlayer().getUniqueId());
        ((ContainerGuiBase) gui).onInventoryClose(event);
    }

    @EventTarget
    public void onPlayerMove(final PlayerMoveEvent event) {
        final GuiBase prevGui = screens.get(event.getPlayer().getUniqueId());
        if (prevGui instanceof ChatGui) ((ChatGui) prevGui).onPlayerMove(event);
    }

    @EventTarget
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final GuiBase prevGui = screens.get(event.getPlayer().getUniqueId());
        if (prevGui instanceof ChatGui) ((ChatGui) prevGui).onChat(event);
    }

    @EventTarget
    public void onPlayerQuit(final PlayerQuitEvent event) {
        screens.remove(event.getPlayer().getUniqueId());
    }

    @EventTarget
    public void onPacketReceive(final PacketReceiveEvent event) {
        final GuiBase gui = screens.get(event.getClient().getPlayer().getUniqueId());
        if (gui instanceof SignGui) {
            final Packet pkt = LibraryPlugin.COMPAT.getWrappedPacket(event.getPacket());
            if (pkt instanceof SOpenSignEditorPacket) {
                ((SignGui) gui).onGuiOpen();
            } else if (pkt instanceof CUpdateSignPacket) {
                ((SignGui) gui).onGuiClose(event);
                if (gui.prevGui == null) {
                    screens.remove(event.getClient().getPlayer().getUniqueId());
                } else {
                    screens.put(event.getClient().getPlayer().getUniqueId(), gui.prevGui);
                    Bukkit.getScheduler().runTask(LibraryPlugin.getPlugin(), () ->
                            screens.get(event.getClient().getPlayer().getUniqueId()).forceOpen());
                }
            }
        }
    }

    /*@EventTarget
    public void onSignChange(final SignChangeEvent event) {
        final GuiBase gui = screens.get(event.getPlayer().getUniqueId());
        if (gui instanceof SignGui) {
            ((SignGui) gui).onSignChange(event);
            event.getPlayer().closeInventory();
            if (gui.getPrevGui() != null) {
                screens.put(event.getPlayer().getUniqueId(), gui.getPrevGui());
                screens.get(event.getPlayer().getUniqueId()).forceOpen();
            }
        }
    }*/

    @Override
    public boolean handleEvents() {
        return true;
    }
}
