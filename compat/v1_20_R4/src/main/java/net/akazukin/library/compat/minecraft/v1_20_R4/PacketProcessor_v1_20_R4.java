package net.akazukin.library.compat.minecraft.v1_20_R4;

import lombok.AllArgsConstructor;
import net.akazukin.library.compat.minecraft.compats.Compat_v1_20_R4;
import net.akazukin.library.compat.minecraft.data.PacketProcessor;
import net.akazukin.library.compat.minecraft.data.packets.CUpdateSignPacket;
import net.akazukin.library.compat.minecraft.data.packets.SInitializeBorderPacket;
import net.akazukin.library.compat.minecraft.data.packets.SOpenSignEditorPacket;
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
        if (packet instanceof SOpenSignEditorPacket) {
            return new PacketPlayOutOpenSignEditor(
                    this.compat.getNMSBlockPos(((SOpenSignEditorPacket) packet).getWrappedBlockPos()),
                    ((SOpenSignEditorPacket) packet).isFrontText()
            );
        } else if (packet instanceof CUpdateSignPacket) {
            return new PacketPlayInUpdateSign(
                    this.compat.getNMSBlockPos(((CUpdateSignPacket) packet).getPosition()),
                    ((CUpdateSignPacket) packet).isD(),
                    ArrayUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 0),
                    ArrayUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 1),
                    ArrayUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 2),
                    ArrayUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 3)
            );
        } else if (packet instanceof SInitializeBorderPacket) {
            return new ClientboundInitializeBorderPacket(
                    ((CraftWorldBorder) ((SInitializeBorderPacket) packet).getWorldBorder())
                            .getHandle()
            );
        }
        return null;
    }

    @Override
    public net.akazukin.library.compat.minecraft.data.packets.Packet processPacket(final Packet<?> packet) {
        //CPacket only supports
        if (packet instanceof PacketPlayOutOpenSignEditor) {
            return new SOpenSignEditorPacket(
                    this.compat.getWrappedBlockPos(((PacketPlayOutOpenSignEditor) packet).a()),
                    packet.d()
            );
        } else if (packet instanceof PacketPlayInUpdateSign) {
            return new CUpdateSignPacket(
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
