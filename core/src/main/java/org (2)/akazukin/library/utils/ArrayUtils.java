package org.akazukin.library.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import lombok.NonNull;
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


    @NonNull
    public static <T> String join(final String character, final Collection<T> list) {
        if (list == null) return "";
        return join(character, list.toArray());
    }

    @NonNull
    public static <T> String join(final String character, final T[] list) {
        if (list == null) return "";
        return Arrays.stream(list)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(character));
    }

    @Nullable
    public static <T> T getRandom(final T[] list) {
        return getIndex(list, (int) (Math.random() * list.length));
    }

    @Nullable
    public static <T> T getIndex(final T[] arr, final int index) {
        if (arr == null || arr.length <= index) return null;
        return arr[index];
    }

    @Nullable
    public static <T> T getRandom(final List<T> list) {
        return getIndex(list, (int) (Math.random() * list.size()));
    }

    @Nullable
    public static <T> T getIndex(final List<T> list, final int index) {
        if (list.size() <= index) return null;
        return list.get(index);
    }

    public static <T> List<T> copy(final List<T> array, final int index, final int length) {
        final List<T> list = new ArrayList<>();
        for (int i = index; list.size() <= length && i < array.size(); i++) {
            list.add(array.get(i));
        }
        return list;
    }
}
