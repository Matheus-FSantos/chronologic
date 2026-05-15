package io.github.matheus_fsantos.chronologic_core.adapters;

import io.github.matheus_fsantos.chronologic_core.application.core.model.Log;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.PersistLogsOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersistLogsAdapter implements PersistLogsOutputPort {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CLASS_NAME = PersistLogsAdapter.class.getSimpleName();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd:HH");

    @Async
    @Override
    public void persist(List<Log> logs) {
        String suffix = LocalDateTime.now().format(PersistLogsAdapter.FORMATTER);
        String key = String.format("logs:%s", suffix);

        PersistLogsAdapter.log.info("{} - persist - message: micro-batch ingest started for key: {}, size: {}", CLASS_NAME, key, logs.size());

        try {
            logs.forEach(log -> {
                String json = objectMapper.writeValueAsString(log);
                redisTemplate.opsForList().rightPush(key, json);
            });

            redisTemplate.expire(key, 5, TimeUnit.MINUTES);
            PersistLogsAdapter.log.info("{} - persist - message: micro-batch successfully bufferized in redis", CLASS_NAME);
        } catch (Exception e) {
            PersistLogsAdapter.log.error("{} - persist - error: fail to push micro-batch to redis. message: {}", CLASS_NAME, e.getMessage(), e);
        }
    }
}
