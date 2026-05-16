package io.github.matheus_fsantos.chronologic_core.application.ports.out;

import io.github.matheus_fsantos.chronologic_core.application.core.model.Log;

import java.util.List;

public interface CachePersistLogsOutputPort {
    void persist(List<Log> logs);
}
