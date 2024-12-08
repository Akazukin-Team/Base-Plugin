package org.akazukin.library;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LibraryPluginProvider {
    @Setter
    @Getter
    static LibraryPluginAPI api;
}
