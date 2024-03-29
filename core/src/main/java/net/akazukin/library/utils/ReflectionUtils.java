package net.akazukin.library.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {
    public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... args) {
        try {
            return clazz.getConstructor(args);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(final Constructor<T> clazz, final Object... args) {
        try {
            return clazz.newInstance(args);
        } catch (final IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(final Object object, final String field, final Class<T> type) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final Field f = object.getClass().getDeclaredField(field);
        if (!f.isAccessible()) f.setAccessible(true);
        return (T) f.get(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(final Object object, final Class<?> superClass, final String field, final Class<T> type) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final Field f = superClass.getDeclaredField(field);
        if (!f.isAccessible()) f.setAccessible(true);
        return (T) f.get(object);
    }

    public static void setField(final Object obj, final String field, final Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final Field f = obj.getClass().getDeclaredField(field);
        if (!f.isAccessible()) f.setAccessible(true);
        f.set(obj, value);
    }

    public static Object callMethod(final Object obj, final String method, final Object... params) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final Class<?>[] paramClasses = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            paramClasses[i] = params.getClass();
        }
        final Method m = obj.getClass().getDeclaredMethod(method, paramClasses);
        if (!m.isAccessible()) m.setAccessible(true);

        return m.invoke(obj, params);
    }

    public static <T> T callMethod_(final Object obj, final String method, final Class<T> type, final Object... params) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final Class<?>[] paramClasses = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            paramClasses[i] = params.getClass();
        }
        final Method m = obj.getClass().getDeclaredMethod(method, paramClasses);
        if (!m.isAccessible()) m.setAccessible(true);
        return (T) m.invoke(obj, params);
    }
}
