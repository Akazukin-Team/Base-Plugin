package net.akazukin.library.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class ConfigElement<T> {
    private final String key;
    private T value;
    private final T defaultValue;

    public ConfigElement(final String key) {
        this(key, null);
    }

    public void setValue(final Object value) {
        this.value = (T) value;
    }
}
