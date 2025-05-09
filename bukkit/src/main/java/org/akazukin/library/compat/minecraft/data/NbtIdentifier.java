package org.akazukin.library.compat.minecraft.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.util.utils.ArrayUtils;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class NbtIdentifier {
    String[] id;

    public NbtIdentifier(final String... id) {
        this.id = id;
    }

    public NbtIdentifier putAfter(final String... id) {
        return new NbtIdentifier(ArrayUtils.concat(this.id, id));
    }
}
