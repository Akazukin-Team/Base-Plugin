package org.akazukin.library.compat.minecraft.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WrappedDataWatcherObject<T> {
    final WrappedDataWatcherKey<T> key;
    T value;
}
