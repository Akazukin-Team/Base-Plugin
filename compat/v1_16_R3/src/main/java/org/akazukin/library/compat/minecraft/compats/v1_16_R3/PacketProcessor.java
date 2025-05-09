package org.akazukin.library.compat.minecraft.compats.v1_16_R3;

import lombok.AllArgsConstructor;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenSignEditor;
import org.akazukin.library.compat.minecraft.data.IPacketProcessor;
import org.akazukin.library.compat.minecraft.data.packets.COpenSignEditorPacket;
import org.akazukin.library.compat.minecraft.data.packets.SBlockChangePacket;
import org.akazukin.library.compat.minecraft.data.packets.SUpdateSignPacket;

@AllArgsConstructor
public class PacketProcessor implements IPacketProcessor<Packet<?>> {
    Compat compat;

    @Override
    public Packet<?> processWrapper(final org.akazukin.library.compat.minecraft.data.packets.Packet packet) {
        //SPacket only supports
        if (packet instanceof COpenSignEditorPacket) {
            return new PacketPlayOutOpenSignEditor(
                    this.compat.getNMSBlockPos(((COpenSignEditorPacket) packet).getWrappedBlockPos())
            );
        } else if (packet instanceof SBlockChangePacket) {
            return new PacketPlayOutBlockChange(
                    this.compat.getNMSBlockPos(((SBlockChangePacket) packet).getPos()),
                    this.compat.getBlockData(((SBlockChangePacket) packet).getBlockData())
            );
        }
        return null;
    }

    @Override
    public org.akazukin.library.compat.minecraft.data.packets.Packet processPacket(final Packet<?> packet) {
        //CPacket only supports
        if (packet instanceof PacketPlayInUpdateSign) {
            return new SUpdateSignPacket(
                    this.compat.getWrappedBlockPos(packet.a()),
                    ((PacketPlayInUpdateSign) packet).c()
            );
        }
        return null;
    }
}
