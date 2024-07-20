package net.akazukin.library.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArrayUtils {
    public static <T> List<T> copy(final List<T> array, final int index, final int length) {
        final List<T> list = new ArrayList<>();
        for (int i = index; list.size() <= length && i < array.size(); i++) {
            list.add(array.get(i));
        }
        return list;
    }

    @Nonnull
    public static String join(final String character, final List<Object> list) {
        if (list == null) return "";
        return join(character, list.toArray());
    }

    @Nonnull
    public static String join(final String character, final Object[] list) {
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
}
