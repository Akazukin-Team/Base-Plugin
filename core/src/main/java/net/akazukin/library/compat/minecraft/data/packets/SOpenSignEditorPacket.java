package net.akazukin.library.compat.minecraft.data.packets;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;

@AllArgsConstructor
@Data
public class SOpenSignEditorPacket implements Packet {
    WrappedBlockPos wrappedBlockPos;
    boolean frontText;
}
