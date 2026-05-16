package io.github.matheus_fsantos.chronologic_core.adapters.out.redis;

import io.github.matheus_fsantos.chronologic_core.application.core.model.Log;
import io.github.matheus_fsantos.chronologic_core.application.core.utils.SuffixUtils;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.CachePersistLogsOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CachePersistPersistLogsAdapter implements CachePersistLogsOutputPort {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CLASS_NAME = CachePersistLogsOutputPort.class.getSimpleName();

    @Async
    @Override
    public void persist(List<Log> logs) {
        String suffix = SuffixUtils.getPastSuffix();
        String key = String.format("logs:%s", suffix);

        CachePersistPersistLogsAdapter.log.info("{} - persist - message: micro-batch ingest started for key: {}, size: {}", CLASS_NAME, key, logs.size());

        try {
            logs.forEach(log -> {
                String json = objectMapper.writeValueAsString(log);
                redisTemplate.opsForList().rightPush(key, json);
            });

            redisTemplate.expire(key, 20, TimeUnit.MINUTES);
            CachePersistPersistLogsAdapter.log.info("{} - persist - message: micro-batch successfully bufferized in redis", CLASS_NAME);
        } catch (Exception e) {
            CachePersistPersistLogsAdapter.log.error("{} - persist - error: fail to push micro-batch to redis. message: {}", CLASS_NAME, e.getMessage(), e);
        }
    }
}
