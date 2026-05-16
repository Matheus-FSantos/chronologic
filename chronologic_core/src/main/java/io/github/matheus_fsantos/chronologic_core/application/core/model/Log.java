package io.github.matheus_fsantos.chronologic_core.application.core.model;

public record Log(
        String timestamp,
        String appName,
        String environment,
        String level,
        String traceId,
        String message,
        String metadata
) { }

