package net.akazukin.library.compat.minecraft.v1_20_R3;

import lombok.AllArgsConstructor;
import net.akazukin.library.compat.minecraft.compats.Compat_v1_20_R3;
import net.akazukin.library.compat.minecraft.data.PacketProcessor;
import net.akazukin.library.compat.minecraft.data.packets.CUpdateSignPacket;
import net.akazukin.library.compat.minecraft.data.packets.SInitializeBorderPacket;
import net.akazukin.library.compat.minecraft.data.packets.SOpenSignEditorPacket;
import net.akazukin.library.utils.StringUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorldBorder;

@AllArgsConstructor
public class PacketProcessor_v1_20_R3 implements PacketProcessor<Packet<?>> {
    private final Compat_v1_20_R3 compat;

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
                    StringUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 0),
                    StringUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 1),
                    StringUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 2),
                    StringUtils.getIndex(((CUpdateSignPacket) packet).getLines(), 3)
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
                    ((PacketPlayOutOpenSignEditor) packet).d()
            );
        } else if (packet instanceof PacketPlayInUpdateSign) {
            return new CUpdateSignPacket(
                    this.compat.getWrappedBlockPos(((PacketPlayInUpdateSign) packet).a()),
                    ((PacketPlayInUpdateSign) packet).e(),
                    ((PacketPlayInUpdateSign) packet).d()
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
