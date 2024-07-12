package net.akazukin.library.compat.minecraft.v1_20_R4;

import lombok.AllArgsConstructor;
import net.akazukin.library.compat.minecraft.compats.Compat_v1_20_R4;
import net.akazukin.library.compat.minecraft.data.PacketProcessor;
import net.akazukin.library.compat.minecraft.data.packets.CInitializeBorderPacket;
import net.akazukin.library.compat.minecraft.data.packets.COpenSignEditorPacket;
import net.akazukin.library.compat.minecraft.data.packets.SUpdateSignPacket;
import net.akazukin.library.utils.ArrayUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorldBorder;

@AllArgsConstructor
public class PacketProcessor_v1_20_R4 implements PacketProcessor<Packet<?>> {
    private final Compat_v1_20_R4 compat;

    @Override
    public Packet<?> processWrapper(final net.akazukin.library.compat.minecraft.data.packets.Packet packet) {
        //Both packers
        if (packet instanceof COpenSignEditorPacket) {
            return new PacketPlayOutOpenSignEditor(
                    this.compat.getNMSBlockPos(((COpenSignEditorPacket) packet).getWrappedBlockPos()),
                    ((COpenSignEditorPacket) packet).isFrontText()
            );
        } else if (packet instanceof SUpdateSignPacket) {
            return new PacketPlayInUpdateSign(
                    this.compat.getNMSBlockPos(((SUpdateSignPacket) packet).getPosition()),
                    ((SUpdateSignPacket) packet).isOutlined(),
                    ArrayUtils.getIndex(((SUpdateSignPacket) packet).getLines(), 0),
                    ArrayUtils.getIndex(((SUpdateSignPacket) packet).getLines(), 1),
                    ArrayUtils.getIndex(((SUpdateSignPacket) packet).getLines(), 2),
                    ArrayUtils.getIndex(((SUpdateSignPacket) packet).getLines(), 3)
            );
        } else if (packet instanceof CInitializeBorderPacket) {
            return new ClientboundInitializeBorderPacket(
                    ((CraftWorldBorder) ((CInitializeBorderPacket) packet).getWorldBorder())
                            .getHandle()
            );
        }
        return null;
    }

    @Override
    public net.akazukin.library.compat.minecraft.data.packets.Packet processPacket(final Packet<?> packet) {
        //CPacket only supports
        if (packet instanceof PacketPlayOutOpenSignEditor) {
            return new COpenSignEditorPacket(
                    this.compat.getWrappedBlockPos(((PacketPlayOutOpenSignEditor) packet).a()),
                    packet.d()
            );
        } else if (packet instanceof PacketPlayInUpdateSign) {
            return new SUpdateSignPacket(
                    this.compat.getWrappedBlockPos(((PacketPlayInUpdateSign) packet).a()),
                    ((PacketPlayInUpdateSign) packet).f(),
                    ((PacketPlayInUpdateSign) packet).e()
            );
        } else if (packet instanceof ClientboundInitializeBorderPacket) {
            /*final WorldBorder border = new WorldBorder();
            border.a();
            return new SInitializeBorderPacket(
                    (((ClientboundInitializeBorderPacket) packet).getWorldBorder())
            );*/
        }
        return null;
    }
}
