package net.akazukin.library.utils;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
    public static <T> List<T> copy(final List<T> array, final int index, final int length) {
        final List<T> list = new ArrayList<>();
        for (int i = index; list.size() <= length && i < array.size(); i++) {
            list.add(array.get(i));
        }
        return list;
    }
}
