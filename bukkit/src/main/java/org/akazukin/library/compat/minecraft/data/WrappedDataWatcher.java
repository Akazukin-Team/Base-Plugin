package org.akazukin.library.compat.minecraft.data;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WrappedDataWatcher {
    List<WrappedDataWatcherObject<?>> dataWatchers = new ArrayList<>();

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> WrappedDataWatcherObject<T> getDataWatcherByKey(final WrappedDataWatcherKey<T> key) {
        return (WrappedDataWatcherObject<T>) this.dataWatchers.stream()
                .filter(dW -> dW.getKey() == key)
                .findFirst()
                .orElse(null);
    }
}
