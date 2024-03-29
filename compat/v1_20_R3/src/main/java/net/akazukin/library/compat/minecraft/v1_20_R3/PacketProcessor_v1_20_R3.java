package net.akazukin.library.compat.minecraft.v1_20_R3;

import lombok.AllArgsConstructor;
import net.akazukin.library.compat.minecraft.data.PacketProcessor;
import net.akazukin.library.compat.minecraft.data.packets.CUpdateSignPacket;
import net.akazukin.library.compat.minecraft.data.packets.SOpenSignEditorPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;

@AllArgsConstructor
public class PacketProcessor_v1_20_R3 implements PacketProcessor<Packet<?>> {
    private final Compat_v1_20_R3 compat;

    @Override
    public Packet<PacketListenerPlayOut> processWrapper(final net.akazukin.library.compat.minecraft.data.packets.Packet packet) {
        //SPacket only supports
        if (packet instanceof SOpenSignEditorPacket) {
            return new PacketPlayOutOpenSignEditor(
                    compat.getNMSBlockPos(((SOpenSignEditorPacket) packet).getWrappedBlockPos()),
                    ((SOpenSignEditorPacket) packet).isFrontText()
            );
        }
        return null;
    }

    @Override
    public net.akazukin.library.compat.minecraft.data.packets.Packet processPacket(final Packet<?> packet) {
        //CPacket only supports
        if (packet instanceof PacketPlayInUpdateSign) {
            return new CUpdateSignPacket(
                    compat.getWrappedBlockPos(((PacketPlayInUpdateSign) packet).a()),
                    ((PacketPlayInUpdateSign) packet).e(),
                    ((PacketPlayInUpdateSign) packet).d()
            );
        }
        return null;
    }
}
