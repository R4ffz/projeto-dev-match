package com.devmatch.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    List<FieldErrorItem> fieldErrors
) {
    public record FieldErrorItem(String field, String message) {}

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, null);
    }

    public static ApiError validation(int status, String message, String path, List<FieldErrorItem> fieldErrors) {
        return new ApiError(Instant.now(), status, "Bad Request", message, path, fieldErrors);
    }
}
