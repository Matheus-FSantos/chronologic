package io.github.matheus_fsantos.chronologic_core.adapters.in.rest.mapper;

import io.github.matheus_fsantos.chronologic_core.adapters.in.rest.request.LogRequest;
import io.github.matheus_fsantos.chronologic_core.application.core.model.Log;

public class LogRequestMapper {
    private LogRequestMapper() { }

    public static Log toDomain(LogRequest request) {
        return new Log(
            request.timestamp(),
            request.appName(),
            request.environment(),
            request.level(),
            request.traceId(),
            request.message(),
            request.metadata().toString()
        );
    }
}
