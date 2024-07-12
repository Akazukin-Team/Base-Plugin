package net.akazukin.library.compat.minecraft.data.packets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;

@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SUpdateSignPacket implements Packet {
    WrappedBlockPos position;
    String[] lines;
    boolean outlined;

    public SUpdateSignPacket(final WrappedBlockPos position, final String[] lines) {
        this.position = position;
        this.lines = lines;
        this.outlined = false;
    }
}
