package org.akazukin.library.utils;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

@UtilityClass
public class LambdaUtils2 {
    public <T> Collection<Callable<Void>> toCallable(final T[] arr, final Consumer<T> lambda) {
        @SuppressWarnings("unchecked") final Callable<Void>[] tasks = new Callable[arr.length];
        int i = 0;
        for (final T t1 : arr) {
            tasks[i++] = () -> {
                lambda.accept(t1);
                return null;
            };
        }
        return Arrays.asList(tasks);
    }

    public Collection<Callable<Void>> toCallable(final long[] arr, final LongConsumer lambda) {
        @SuppressWarnings("unchecked") final Callable<Void>[] tasks = new Callable[arr.length];
        int i = 0;
        for (final long t1 : arr) {
            tasks[i++] = () -> {
                lambda.accept(t1);
                return null;
            };
        }
        return Arrays.asList(tasks);
    }
}
