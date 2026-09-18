package org.akazukin.library.gui;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.event.IListenable;
import org.akazukin.event.target.EventTarget;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.gui.screens.IPacketGui;
import org.akazukin.library.gui.screens.container.physhic.IPhysicalContainerGui;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public final class GuiManager implements IListenable, IGuiManager {
    HashMap<UUID, IGui> screens = new HashMap<>();
    LibraryPlugin plugin;

    public GuiManager(@NotNull final LibraryPlugin plugin) {
        this.plugin = plugin;
    }

    public IGui getScreen(final UUID player) {
        return this.screens.get(player);
    }

    @EventTarget(libraryPriority = 3, ignoreSuperClasses = false)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        final IGui gui = this.screens.get(event.getWhoClicked().getUniqueId());
        if (!(gui instanceof IPhysicalContainerGui)) {
            return;
        }
        final IPhysicalContainerGui containerGui = (IPhysicalContainerGui) gui;

        if (event.getInventory() != containerGui.getInv()) {
            return;
        }

        containerGui.onClick(event);
    }

    @Override
    public void openGui(final UUID player, final IGui gui) {
        Bukkit.getScheduler().runTask(LibraryPlugin.getPlugin(), () -> {
            this.closeGui(player);
            if (gui != null) {
                final OpenStatus status = gui.open(player);
                if (status.isSuccess()) {
                    this.screens.put(player, gui);

                    if (status.isAlwaysOpen()) {
                        gui.onOpen(player);
                    }
                }
            }
        });
    }

    @Override
    public void closeGui(final UUID player) {
        final IGui closedGui = this.screens.remove(player);
        if (closedGui == null) {
            return;
        }

        Bukkit.getScheduler().runTask(LibraryPlugin.getPlugin(),
                () -> closedGui.close(player));
    }

    @Override
    public void closeGui(final UUID player, final IGui gui) {
        if (!this.screens.remove(player, gui)) {
            return;
        }

        Bukkit.getScheduler().runTask(LibraryPlugin.getPlugin(),
                () -> gui.close(player));
    }

    @Override
    public void handleCloseGui(final UUID player, final IGui gui) {
        this.screens.remove(player, gui);

        Bukkit.getScheduler().runTask(LibraryPlugin.getPlugin(),
                () -> gui.onClose(player));
    }

    @Override
    public void handleOpenGui(final UUID player, final IGui gui) {
        this.screens.remove(player, gui);

        Bukkit.getScheduler().runTask(LibraryPlugin.getPlugin(),
                () -> gui.onClose(player));
    }

    @EventTarget(libraryPriority = 2)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final IGui gui = this.screens.get(event.getPlayer().getUniqueId());
        if (!(gui instanceof IPhysicalContainerGui)) {
            return;
        }
        final IPhysicalContainerGui containerGui = (IPhysicalContainerGui) gui;

        if (event.getInventory() != containerGui.getInv()) {
            return;
        }

        containerGui.onOpen(event.getPlayer().getUniqueId());
    }

    @EventTarget(libraryPriority = 2)
    public void onInventoryClose(final InventoryCloseEvent event) {
        final IGui gui = this.screens.get(event.getPlayer().getUniqueId());
        if (!(gui instanceof IPhysicalContainerGui)) {
            return;
        }
        final IPhysicalContainerGui containerGui = (IPhysicalContainerGui) gui;

        if (event.getInventory() != containerGui.getInv()) {
            return;
        }

        this.screens.remove(event.getPlayer().getUniqueId(), containerGui);
        containerGui.onClose(event.getPlayer().getUniqueId());
    }

    @EventTarget(libraryPriority = 2)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final IGui closed = this.screens.remove(event.getPlayer().getUniqueId());
        if (closed != null) {
            closed.onClose(event.getPlayer().getUniqueId());
        }
    }

    @EventTarget(libraryPriority = 2)
    public void onPacketReceive(final PacketReceiveEvent event) {
        final IGui gui = this.screens.get(event.getUser().getUUID());

        if (gui instanceof IPacketGui) {
            ((IPacketGui) gui).onPacketReceive(event);
        }
    }

    @EventTarget(libraryPriority = 2)
    public void onPacketSend(final PacketSendEvent event) {
        final IGui gui = this.screens.get(event.getUser().getUUID());

        if (gui instanceof IPacketGui) {
            ((IPacketGui) gui).onPacketSend(event);
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
