package org.akazukin.library.compat.minecraft.v1_17_R1;

import lombok.AllArgsConstructor;
import org.akazukin.library.compat.minecraft.compats.Compat_v1_17_R1;
import org.akazukin.library.compat.minecraft.data.PacketProcessor;
import org.akazukin.library.compat.minecraft.data.packets.COpenSignEditorPacket;
import org.akazukin.library.compat.minecraft.data.packets.SUpdateSignPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;

@AllArgsConstructor
public class PacketProcessor_v1_17_R1 implements PacketProcessor<Packet<?>> {
    private final Compat_v1_17_R1 compat;

    @Override
    public Packet<?> processWrapper(final org.akazukin.library.compat.minecraft.data.packets.Packet packet) {
        //SPacket only supports
        if (packet instanceof COpenSignEditorPacket) {
            return new PacketPlayOutOpenSignEditor(
                    compat.getNMSBlockPos(((COpenSignEditorPacket) packet).getWrappedBlockPos())
            );
        }
        return null;
    }

    @Override
    public org.akazukin.library.compat.minecraft.data.packets.Packet processPacket(final Packet<?> packet) {
        //CPacket only supports
        if (packet instanceof PacketPlayInUpdateSign) {
            return new SUpdateSignPacket(
                    compat.getWrappedBlockPos(packet.a()),
                    ((PacketPlayInUpdateSign) packet).c()
            );
        }
        return null;
    }
}
