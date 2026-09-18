package org.akazukin.library.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdentityHashBiMap<K, V> implements Map<K, V> {
    final IdentityHashMap<K, V> keyMap;
    final IdentityHashMap<V, K> valueMap;

    private IdentityHashBiMap(final IdentityHashMap<K, V> keyMap, final IdentityHashMap<V, K> valueMap) {
        this.keyMap = keyMap;
        this.valueMap = valueMap;
    }

    public IdentityHashBiMap() {
        this.keyMap = new IdentityHashMap<>();
        this.valueMap = new IdentityHashMap<>();
    }

    public IdentityHashBiMap(final Map<? extends K, ? extends V> m) {
        this((int) ((1 + m.size()) * 1.1));
        this.putAll(m);
    }

    public IdentityHashBiMap(final int capacity) {
        this.keyMap = new IdentityHashMap<>(capacity);
        this.valueMap = new IdentityHashMap<>(capacity);
    }

    public IdentityHashBiMap<V, K> inverse() {
        return new IdentityHashBiMap<>(this.valueMap, this.keyMap);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.keyMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.valueMap.containsKey(value);
    }

    @Override
    public V get(final Object key) {
        return this.keyMap.get(key);
    }

    @Override
    public @Nullable V put(final K key, final V value) {
        final V replaced = this.keyMap.put(key, value);
        this.valueMap.remove(replaced);
        return replaced;
    }

    @Override
    public V remove(final Object key) {
        final V removed = this.keyMap.remove(key);
        this.valueMap.remove(removed);
        return removed;
    }

    @Override
    public void putAll(@NotNull final Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        this.keyMap.clear();
        this.valueMap.clear();
    }

    @Override
    public @NotNull Set<K> keySet() {
        return this.keyMap.keySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        return this.keyMap.values();
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return this.keyMap.entrySet();
    }

    @Override
    public int hashCode() {
        return this.keyMap.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IdentityHashBiMap)) {
            return false;
        }
        final IdentityHashBiMap<?, ?> other = (IdentityHashBiMap<?, ?>) o;
        return this.keyMap.equals(other.keyMap);
    }
}
