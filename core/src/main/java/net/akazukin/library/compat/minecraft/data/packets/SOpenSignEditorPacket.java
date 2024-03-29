package net.akazukin.library.compat.minecraft.data.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;

@AllArgsConstructor
@Getter
public class SOpenSignEditorPacket implements Packet {
    private final WrappedBlockPos wrappedBlockPos;
    private final boolean frontText;
}
