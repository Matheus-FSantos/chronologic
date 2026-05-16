package io.github.matheus_fsantos.chronologic_core.configuration;

import io.github.matheus_fsantos.chronologic_core.application.core.usecase.MoveLogsToHistoryUseCase;
import io.github.matheus_fsantos.chronologic_core.application.ports.in.MoveLogsToHistoryInputPort;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.FetchBufferLogsOutputPort;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.HistoryPersistOutputPort;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.PurgeBufferLogsOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MoveLogsToHistoryInputPortConfiguration {
    @Bean
    public MoveLogsToHistoryInputPort moveLogsToHistoryUseCase(
        FetchBufferLogsOutputPort fetchBufferLogsOutputPort,
        PurgeBufferLogsOutputPort purgeBufferLogsOutputPort,
        HistoryPersistOutputPort historyPersistOutputPort
    ) {
        return new MoveLogsToHistoryUseCase(fetchBufferLogsOutputPort, purgeBufferLogsOutputPort, historyPersistOutputPort);
    }
}
