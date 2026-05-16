package io.github.matheus_fsantos.chronologic_core.application.ports.out;

public interface PurgeBufferLogsOutputPort {
    void deleteKey(String key);
}
