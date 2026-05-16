package io.github.matheus_fsantos.chronologic_core.adapters.in.scheduler;

import io.github.matheus_fsantos.chronologic_core.application.ports.in.MoveLogsToHistoryInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersistLogsScheduler {
    private final MoveLogsToHistoryInputPort moveLogsToHistoryInputPort;
    private static final String CLASS_NAME = PersistLogsScheduler.class.getSimpleName();

    @Scheduled(cron = "0 0/10 * * * *")
    public void persist() {
        log.info("{} - persist - message: scheduler trigger fired <--- CRON/BEGIN", PersistLogsScheduler.CLASS_NAME);
        this.moveLogsToHistoryInputPort.move();

        log.info("{} - persist - message: scheduler workflow finished ---> CRON/END", PersistLogsScheduler.CLASS_NAME);
    }
}
