package org.akazukin.library.container;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class VirtualContainer {
    final int id;
    final int rows;
    final String title;
    final Set<UUID> viewers = new HashSet<>();
    ItemStack[] contents;

    public VirtualContainer(final int id, @Nullable final String title, final int rows) {
        this.id = id;
        this.title = title;
        this.rows = rows;
        this.contents = new ItemStack[rows * 9];
    }

    public void setContent(final int slot, final ItemStack item) {
        this.contents[slot] = item;
        this.refreshAll();
    }

    private void refreshAll() {
        this.viewers.forEach(this::refresh);
    }

    private void refresh(final UUID player) {
        final User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user == null) {
            return;
        }

        final WrapperPlayServerWindowItems pkt = new WrapperPlayServerWindowItems(
                this.id,
                0,
                Arrays.asList(this.contents),
                ItemStack.EMPTY
        );
        user.sendPacket(pkt);
    }

    public void setContents(final ItemStack[] contents) {
        this.contents = contents;
        this.refreshAll();
    }

    public void open(final UUID player) {
        final User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user == null) {
            return;
        }

        this.viewers.add(player);

        final Component title;
        if (this.title != null) {
            title = Component.text(this.title);
        } else {
            title = Component.translatable("container.chest");
        }

        final WrapperPlayServerOpenWindow pkt = new WrapperPlayServerOpenWindow(
                this.id,
                this.rows,
                title
        );
        user.sendPacket(pkt);
    }

    public void destroy() {
        for (final UUID viewer : this.viewers.toArray(new UUID[0])) {
            this.close(viewer);
        }
    }

    public void close(final UUID player) {
        this.viewers.remove(player);

        final User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user == null) {
            return;
        }

        final WrapperPlayServerCloseWindow pkt = new WrapperPlayServerCloseWindow(this.id);
        user.sendPacket(pkt);
    }
}
