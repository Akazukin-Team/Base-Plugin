package net.akazukin.library.compat.minecraft.data.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;

@AllArgsConstructor
@Getter
public class CUpdateSignPacket implements Packet {
    private final WrappedBlockPos position;
    private final String[] lines;
    private final boolean d;

    public CUpdateSignPacket(final WrappedBlockPos position, final String[] lines) {
        this.position = position;
        this.lines = lines;
        this.d = false;
    }
}
