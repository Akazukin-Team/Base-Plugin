package net.akazukin.library.utils.http;

import lombok.Getter;

@Getter
public enum HttpContentType {
    JSON("application/json"),
    FORM_URL_ENCODED("application/x-www-form-urlencoded");

    private final String contentType;

    HttpContentType(final String contentType) {
        this.contentType = contentType;
    }
}
