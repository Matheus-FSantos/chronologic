package io.github.matheus_fsantos.chronologic_core.configuration;

import io.github.matheus_fsantos.chronologic_core.application.core.usecase.BufferizeLogsUseCase;
import io.github.matheus_fsantos.chronologic_core.application.ports.in.BufferizeLogsInputPort;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.PersistLogsOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BufferizeLogsInputPortConfiguration {
    @Bean
    public BufferizeLogsInputPort bufferizeLogsUseCaseConfiguration(PersistLogsOutputPort persistLogsOutputPort) {
        return new BufferizeLogsUseCase(persistLogsOutputPort);
    }
}
