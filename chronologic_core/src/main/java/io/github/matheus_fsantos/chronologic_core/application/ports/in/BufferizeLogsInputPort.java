package io.github.matheus_fsantos.chronologic_core.application.ports.in;

import io.github.matheus_fsantos.chronologic_core.application.core.model.Log;

import java.util.List;

public interface BufferizeLogsInputPort {
    void bufferize(List<Log> logs);
}
