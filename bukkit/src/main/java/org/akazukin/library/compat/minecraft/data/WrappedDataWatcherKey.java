package org.akazukin.library.compat.minecraft.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WrappedDataWatcherKey<T> {
    public static final WrappedDataWatcherKey HEALTH = new WrappedDataWatcherKey(Float.class);

    Class<T> type;
}
