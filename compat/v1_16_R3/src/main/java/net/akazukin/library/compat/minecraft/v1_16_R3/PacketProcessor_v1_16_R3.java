package net.akazukin.library.compat.minecraft.v1_16_R3;

import lombok.AllArgsConstructor;
import net.akazukin.library.compat.minecraft.compats.Compat_v1_16_R3;
import net.akazukin.library.compat.minecraft.data.PacketProcessor;
import net.akazukin.library.compat.minecraft.data.packets.CUpdateSignPacket;
import net.akazukin.library.compat.minecraft.data.packets.SOpenSignEditorPacket;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketListenerPlayOut;
import net.minecraft.server.v1_16_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenSignEditor;

@AllArgsConstructor
public class PacketProcessor_v1_16_R3 implements PacketProcessor<Packet<?>> {
    Compat_v1_16_R3 compat;

    @Override
    public Packet<PacketListenerPlayOut> processWrapper(final net.akazukin.library.compat.minecraft.data.packets.Packet packet) {
        //SPacket only supports
        if (packet instanceof SOpenSignEditorPacket) {
            return new PacketPlayOutOpenSignEditor(
                    compat.getNMSBlockPos(((SOpenSignEditorPacket) packet).getWrappedBlockPos())
            );
        }
        return null;
    }

    @Override
    public net.akazukin.library.compat.minecraft.data.packets.Packet processPacket(final Packet<?> packet) {
        //CPacket only supports
        if (packet instanceof PacketPlayInUpdateSign) {
            return new CUpdateSignPacket(
                    compat.getWrappedBlockPos(packet.a()),
                    ((PacketPlayInUpdateSign) packet).c()
            );
        }
        return null;
    }
}
