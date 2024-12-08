package org.akazukin.library.world;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.util.utils.StringUtils;
import org.bukkit.World;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class WorldData {
    UUID uid;
    String name;

    public boolean equalsBkt(@Nullable final World world) {
        if (!this.isValid() || world == null) return false;
        if (Objects.equals(this.uid, world.getUID())) return true;
        return Objects.equals(this.name, world.getName());
    }

    public boolean isValid() {
        return this.uid != null || StringUtils.getLength(this.name) > 0;
    }
}
