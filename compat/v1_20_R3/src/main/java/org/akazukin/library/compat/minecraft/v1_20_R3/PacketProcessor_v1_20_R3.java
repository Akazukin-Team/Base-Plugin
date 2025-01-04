package org.akazukin.library.compat.minecraft.v1_20_R3;

import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import lombok.AllArgsConstructor;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutMultiBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.world.level.block.state.IBlockData;
import org.akazukin.library.compat.minecraft.compats.Compat_v1_20_R3;
import org.akazukin.library.compat.minecraft.data.PacketProcessor;
import org.akazukin.library.compat.minecraft.data.packets.CInitializeBorderPacket;
import org.akazukin.library.compat.minecraft.data.packets.COpenSignEditorPacket;
import org.akazukin.library.compat.minecraft.data.packets.SBlockChangePacket;
import org.akazukin.library.compat.minecraft.data.packets.SMultiBlockChangePacket;
import org.akazukin.library.compat.minecraft.data.packets.SUpdateSignPacket;
import org.akazukin.library.utils.ArrayUtils;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorldBorder;

import java.util.Arrays;

@AllArgsConstructor
public class PacketProcessor_v1_20_R3 implements PacketProcessor<Packet<?>> {
    private final Compat_v1_20_R3 compat;

    @Override
    public Packet<?> processWrapper(final org.akazukin.library.compat.minecraft.data.packets.Packet packet) {
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
        } else if (packet instanceof SMultiBlockChangePacket) {
            return new PacketPlayOutMultiBlockChange(
                    SectionPosition.a(
                            ((SMultiBlockChangePacket) packet).getSectionPos().getX(),
                            ((SMultiBlockChangePacket) packet).getSectionPos().getY(),
                            ((SMultiBlockChangePacket) packet).getSectionPos().getZ()),
                    new ShortArraySet(Arrays.stream(((SMultiBlockChangePacket) packet).getBlockInfo()).map(b ->
                            (short) ((b.getPos().getX() & 0xF) << 8 | (b.getPos().getZ() & 0xF) << 4 | b.getPos().getY() & 0xF)).toList()),
                    Arrays.stream(((SMultiBlockChangePacket) packet).getBlockInfo())
                            .map(b -> (IBlockData) b.getBlockData().getBlockData())
                            .toArray(IBlockData[]::new)
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
        if (packet instanceof PacketPlayOutOpenSignEditor) {
            return new COpenSignEditorPacket(
                    this.compat.getWrappedBlockPos(((PacketPlayOutOpenSignEditor) packet).a()),
                    ((PacketPlayOutOpenSignEditor) packet).d()
            );
        } else if (packet instanceof PacketPlayInUpdateSign) {
            return new SUpdateSignPacket(
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
