package net.akazukin.library.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum EventPriority {
    HIGHEST(5),
    HIGH(4),
    NORMAL(3),
    LOW(2),
    LOWEST(1),
    MONITOR(-1);

    int level;
}
