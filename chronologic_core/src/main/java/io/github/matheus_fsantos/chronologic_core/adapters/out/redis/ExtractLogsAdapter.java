package io.github.matheus_fsantos.chronologic_core.adapters.out.redis;

import io.github.matheus_fsantos.chronologic_core.application.ports.out.FetchBufferLogsOutputPort;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.PurgeBufferLogsOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExtractLogsAdapter implements FetchBufferLogsOutputPort, PurgeBufferLogsOutputPort {
    private final static String CLASS_NAME = ExtractLogsAdapter.class.getSimpleName();

    private final StringRedisTemplate redisTemplate;

    @Override
    public Set<String> findAllKeys() {
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match("logs:*").count(100).build();

        try(Cursor<String> cursor = this.redisTemplate.scan(options)) {
            while(cursor.hasNext()) {
                keys.add(cursor.next());
            }
        } catch (Exception e) {
            ExtractLogsAdapter.log.error("{} - findAllKeys - error: fail to scan keys in redis. message: {}", CLASS_NAME, e.getMessage());
        }

        return keys;
    }

    @Override
    public List<String> extractByKey(String key) {
        return this.redisTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public void deleteKey(String key) {
        this.redisTemplate.delete(key);
    }
}
