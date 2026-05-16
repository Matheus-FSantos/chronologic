package io.github.matheus_fsantos.chronologic_core.application.core.usecase;

import io.github.matheus_fsantos.chronologic_core.application.core.model.PersistResultContext;
import io.github.matheus_fsantos.chronologic_core.application.core.utils.SuffixUtils;
import io.github.matheus_fsantos.chronologic_core.application.ports.in.MoveLogsToHistoryInputPort;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.FetchBufferLogsOutputPort;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.HistoryPersistOutputPort;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.PurgeBufferLogsOutputPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoveLogsToHistoryUseCase implements MoveLogsToHistoryInputPort {
    private static final String CLASS_NAME =  MoveLogsToHistoryUseCase.class.getSimpleName();
    private final Logger logger = Logger.getLogger(MoveLogsToHistoryUseCase.CLASS_NAME);

    private final FetchBufferLogsOutputPort fetchBufferLogsOutputPort;
    private final PurgeBufferLogsOutputPort purgeBufferLogsOutputPort;

    private final HistoryPersistOutputPort historyPersistOutputPort;

    public MoveLogsToHistoryUseCase(
        FetchBufferLogsOutputPort fetchBufferLogsOutputPort,
        PurgeBufferLogsOutputPort purgeBufferLogsOutputPort,
        HistoryPersistOutputPort historyPersistOutputPort
    ) {
        this.fetchBufferLogsOutputPort = fetchBufferLogsOutputPort;
        this.purgeBufferLogsOutputPort = purgeBufferLogsOutputPort;
        this.historyPersistOutputPort = historyPersistOutputPort;
    }

    @Override
    public void move() {
        String suffix = SuffixUtils.getCurrentSuffix();
        String currentKeyLimit = "logs:" + suffix;

        this.logger.log(Level.INFO, "{0} - move - message: starting log extraction for suffix: {1}", new Object[] { MoveLogsToHistoryUseCase.CLASS_NAME, currentKeyLimit });
        Set<String> rawKeys = this.fetchBufferLogsOutputPort.findAllKeys();
        if(rawKeys == null || rawKeys.isEmpty()) {
            this.logger.log(Level.INFO, "{0} - move - message: no logs found in redis for suffix: {1}", new Object[] { MoveLogsToHistoryUseCase.CLASS_NAME, currentKeyLimit });
            return;
        }

        List<String> pastKeys = rawKeys.stream().filter(i -> i.compareTo(currentKeyLimit) < 0).sorted().toList();
        if (pastKeys.isEmpty()) {
            this.logger.log(Level.INFO, "{0} - move - message: keys exist, but none from past hours yet.", new Object[] { MoveLogsToHistoryUseCase.CLASS_NAME });
            return;
        }

        List<String> allLogs = new ArrayList<>();
        for(String key : pastKeys) {
            this.logger.log(Level.INFO, "{0} - move - message: extracting internal logs from key: {1}", new Object[] { MoveLogsToHistoryUseCase.CLASS_NAME, key });
            allLogs.addAll(this.fetchBufferLogsOutputPort.extractByKey(key));
        }

        PersistResultContext ctx = this.historyPersistOutputPort.persist(allLogs);

        if(Boolean.TRUE.equals(ctx.isSuccess())) {
            this.logger.log(Level.INFO, "{0} - move - message: history persist successful. context: {1}. clearing redis buffer...", new Object[] { MoveLogsToHistoryUseCase.CLASS_NAME, ctx.message() });
            pastKeys.forEach(this.purgeBufferLogsOutputPort::deleteKey);
        } else {
            this.logger.log(Level.SEVERE, "{0} - move - message: CRITICAL! fail to move logs. reason: {1}", new Object[] { MoveLogsToHistoryUseCase.CLASS_NAME, ctx.message() });
            if (ctx.exception() != null) this.logger.log(Level.SEVERE, "{0} - move - stacktrace: {1}", new Object[] { MoveLogsToHistoryUseCase.CLASS_NAME, ctx.exception() });

            return;
        }

        this.logger.log(Level.INFO, "{0} - move - message: total consolidated logs to save in CSV: {1}", new Object[] { MoveLogsToHistoryUseCase.CLASS_NAME, allLogs.size() });
    }
}
