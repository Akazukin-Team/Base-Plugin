package net.akazukin.library.compat.minecraft.v1_18_R2;

import lombok.AllArgsConstructor;
import net.akazukin.library.compat.minecraft.compats.Compat_v1_18_R2;
import net.akazukin.library.compat.minecraft.data.PacketProcessor;
import net.akazukin.library.compat.minecraft.data.packets.CUpdateSignPacket;
import net.akazukin.library.utils.StringUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;

@AllArgsConstructor
public class PacketProcessor_v1_18_R2 implements PacketProcessor<Packet<?>> {
    private Compat_v1_18_R2 compat;

    @Override
    public Packet<?> processWrapper(final net.akazukin.library.compat.minecraft.data.packets.Packet packet) {
        //SPacket only supports
        if (packet instanceof CUpdateSignPacket) {
            return new PacketPlayInUpdateSign(
                    compat.getNMSBlockPos(((CUpdateSignPacket) packet).getPosition()),
                    StringUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 0),
                    StringUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 1),
                    StringUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 2),
                    StringUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 3)
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
