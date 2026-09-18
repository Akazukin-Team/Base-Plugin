package org.akazukin.library.gui.screens;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import org.akazukin.library.gui.IGui;

public interface IPacketGui extends IGui {
    default void onPacketReceive(final PacketReceiveEvent event) {
    }

    default void onPacketSend(final PacketSendEvent event) {
    }
}
