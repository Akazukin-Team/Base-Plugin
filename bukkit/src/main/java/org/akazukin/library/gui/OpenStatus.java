package org.akazukin.library.gui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum OpenStatus {
    SUCCESS(true, false),
    SUCCESS_ALWAYS_OPNE(true, true),
    FAILURE(false, false);

    boolean success;
    boolean alwaysOpen;

    OpenStatus(final boolean success, final boolean alwaysOpen) {
        this.success = success;
        this.alwaysOpen = alwaysOpen;
    }
}
