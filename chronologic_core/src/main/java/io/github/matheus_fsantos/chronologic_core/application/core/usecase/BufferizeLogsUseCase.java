package io.github.matheus_fsantos.chronologic_core.application.core.usecase;

import io.github.matheus_fsantos.chronologic_core.application.core.model.Log;
import io.github.matheus_fsantos.chronologic_core.application.ports.in.BufferizeLogsInputPort;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.CachePersistLogsOutputPort;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BufferizeLogsUseCase implements BufferizeLogsInputPort {
    private final static String CLASS_NAME =  BufferizeLogsUseCase.class.getSimpleName();
    private final Logger logger = Logger.getLogger(BufferizeLogsUseCase.CLASS_NAME);

    private final CachePersistLogsOutputPort cachePersistLogsOutputPort;

    public BufferizeLogsUseCase(CachePersistLogsOutputPort cachePersistLogsOutputPort) {
        this.cachePersistLogsOutputPort = cachePersistLogsOutputPort;
    }

    @Override
    public void bufferize(List<Log> logs) {
        this.cachePersistLogsOutputPort.persist(logs);
        this.logger.log(Level.INFO, "{0} - bufferize - message: workflow completed!",  new Object[]{ BufferizeLogsUseCase.CLASS_NAME });
    }
}
