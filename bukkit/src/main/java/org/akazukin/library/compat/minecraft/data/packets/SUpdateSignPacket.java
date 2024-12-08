package org.akazukin.library.compat.minecraft.data.packets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.worldedit.Vec3;

@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SUpdateSignPacket implements Packet {
    Vec3<Integer> position;
    String[] lines;
    boolean outlined;

    public SUpdateSignPacket(final Vec3<Integer> position, final String[] lines) {
        this.position = position;
        this.lines = lines;
        this.outlined = false;
    }
}
