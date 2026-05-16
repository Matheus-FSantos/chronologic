package io.github.matheus_fsantos.chronologic_core.application.ports.out;

import io.github.matheus_fsantos.chronologic_core.application.core.model.PersistResultContext;

import java.util.List;

public interface HistoryPersistOutputPort {
    PersistResultContext persist(List<String> logs);
}
