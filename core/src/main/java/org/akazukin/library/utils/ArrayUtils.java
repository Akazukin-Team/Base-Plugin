package org.akazukin.library.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public class ArrayUtils {
    public static Collector<?, ?, ?> SHUFFLE = Collectors.collectingAndThen(
            Collectors.toCollection(ArrayList::new),
            list -> {
                Collections.shuffle(list);
                return list;
            }
    );

    @SuppressWarnings("unchecked")
    public static <R> Collector<R, ?, List<R>> toShuffle() {
        return (Collector<R, ?, List<R>>) SHUFFLE;
    }

    @Nullable
    public static <T> T getIndex(final T[] arr, final int index) {
        if (arr == null || arr.length <= index) {
            return null;
        }
        return arr[index];
    }

    @Nullable
    public static <T> T getIndex(final List<T> list, final int index) {
        if (list.size() <= index) {
            return null;
        }
        return list.get(index);
    }
}
