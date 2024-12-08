package org.akazukin.library.compat.minecraft.v1_18_R2;

import lombok.AllArgsConstructor;
import org.akazukin.library.compat.minecraft.compats.Compat_v1_18_R2;
import org.akazukin.library.compat.minecraft.data.PacketProcessor;
import org.akazukin.library.compat.minecraft.data.packets.SUpdateSignPacket;
import org.akazukin.library.utils.ArrayUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;

@AllArgsConstructor
public class PacketProcessor_v1_18_R2 implements PacketProcessor<Packet<?>> {
    private Compat_v1_18_R2 compat;

    @Override
    public Packet<?> processWrapper(final org.akazukin.library.compat.minecraft.data.packets.Packet packet) {
        //SPacket only supports
        if (packet instanceof SUpdateSignPacket) {
            return new PacketPlayInUpdateSign(
                    this.compat.getNMSBlockPos(((SUpdateSignPacket) packet).getPosition()),
                    ArrayUtils.getIndex(((SUpdateSignPacket) packet).getLines(), 0),
                    ArrayUtils.getIndex(((SUpdateSignPacket) packet).getLines(), 1),
                    ArrayUtils.getIndex(((SUpdateSignPacket) packet).getLines(), 2),
                    ArrayUtils.getIndex(((SUpdateSignPacket) packet).getLines(), 3)
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
