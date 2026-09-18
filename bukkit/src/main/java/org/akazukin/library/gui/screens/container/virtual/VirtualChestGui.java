package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.container.VirtualContainer;
import org.akazukin.library.container.VirtualContainerManager;
import org.akazukin.library.gui.IGui;
import org.akazukin.library.gui.IGuiManager;
import org.akazukin.library.gui.OpenStatus;
import org.akazukin.library.gui.screens.IPacketGui;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class VirtualChestGui implements IVirtualContainerGui, IPacketGui {
    @Getter
    VirtualContainer inv;
    @Getter
    IGui prevGui;
    IGuiManager guiMgr;

    public VirtualChestGui(final int rows, final IGui prevGui, final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        this(null, rows, prevGui, containerMgr, guiMgr);
    }

    public VirtualChestGui(final String title, final int rows, final IGui prevGui, final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        this.inv = containerMgr.createContainer(title, rows);
        this.prevGui = prevGui;
        this.guiMgr = guiMgr;
    }

    public ItemStack[] getContents() {
        return this.inv.getContents();
    }

    public void setContents(final ItemStack[] contents) {
        this.inv.setContents(contents);
    }

    @Override
    public OpenStatus open(final UUID player) {
        this.refreshContainer();

        this.inv.open(player);

        return OpenStatus.SUCCESS_ALWAYS_OPNE;
    }

    protected void refreshContainer() {
    }

    @Override
    public void close(final UUID player) {
        if (!this.inv.getViewers().contains(player)) {
            return;
        }

        this.inv.close(player);
    }

    @Override
    public void onClose(final UUID player) {
        this.inv.getViewers().remove(player);
    }

    @Override
    public void destroy() {
        this.inv.destroy();
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        // パケットのタイプを取得
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            // クリックパケットのラッパーを作成
            final WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
            final int windowId = packet.getWindowId();
            if (this.inv.getId() == windowId) {
                if (0 <= packet.getSlot() && packet.getSlot() < this.inv.getContents().length) {
                    this.onClick(packet, event.getUser(), this.inv.getContents()[packet.getSlot()]);
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            // インベントリ閉じパケットのラッパーを作成
            final WrapperPlayClientCloseWindow closePacket = new WrapperPlayClientCloseWindow(event);
            final int windowId = closePacket.getWindowId();

            if (this.inv.getId() == windowId) {
                this.guiMgr.handleCloseGui(event.getUser().getUUID(), this);
            }
        }
    }
}
