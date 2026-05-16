package io.github.matheus_fsantos.chronologic_core.application.ports.out;

import java.util.List;
import java.util.Set;

public interface FetchBufferLogsOutputPort {
    Set<String> findAllKeys();
    List<String> extractByKey(String key);
}
