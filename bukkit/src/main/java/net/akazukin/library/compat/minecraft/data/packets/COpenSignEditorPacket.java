package net.akazukin.library.compat.minecraft.data.packets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.akazukin.library.worldedit.Vec3;

@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class COpenSignEditorPacket implements Packet {
    Vec3<Integer> wrappedBlockPos;
    boolean frontText;
}
