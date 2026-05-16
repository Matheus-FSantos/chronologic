package io.github.matheus_fsantos.chronologic_core.application.core.model;

public record PersistResultContext(
    Boolean isSuccess,
    String message,
    Throwable exception
) {
    public static PersistResultContext success(String message) {
        return new PersistResultContext(true, message, null);
    }

    public static PersistResultContext failure(String message, Throwable exception) {
        return new PersistResultContext(false, message, exception);
    }
}
