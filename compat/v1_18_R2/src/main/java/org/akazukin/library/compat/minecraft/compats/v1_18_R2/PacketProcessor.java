package org.akazukin.library.compat.minecraft.compats.v1_18_R2;

import lombok.AllArgsConstructor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import org.akazukin.library.compat.minecraft.data.IPacketProcessor;
import org.akazukin.library.compat.minecraft.data.packets.SBlockChangePacket;
import org.akazukin.library.compat.minecraft.data.packets.SUpdateSignPacket;
import org.akazukin.library.utils.ArrayUtils;

@AllArgsConstructor
public class PacketProcessor implements IPacketProcessor<Packet<?>> {
    private Compat compat;

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
