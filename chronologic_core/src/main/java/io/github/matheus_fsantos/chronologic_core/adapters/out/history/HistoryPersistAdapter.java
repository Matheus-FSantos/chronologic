package io.github.matheus_fsantos.chronologic_core.adapters.out.history;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import com.clickhouse.data.ClickHouseFormat;
import io.github.matheus_fsantos.chronologic_core.application.core.model.Log;
import io.github.matheus_fsantos.chronologic_core.application.core.model.PersistResultContext;
import io.github.matheus_fsantos.chronologic_core.application.ports.out.HistoryPersistOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryPersistAdapter implements HistoryPersistOutputPort {
    private static final String CLASS_NAME = HistoryPersistAdapter.class.getSimpleName();
    private final ObjectMapper objectMapper;

    @Override
    public PersistResultContext persist(List<String> logs) {
        log.info("{} - persist - message: starting virtualization to memory CSV. total: {}", CLASS_NAME, logs.size());
        StringWriter stringWriter = new StringWriter();

        try(BufferedWriter writer = new BufferedWriter(stringWriter)) {
            for(String current : logs) {
                Log log = this.objectMapper.readValue(current, Log.class);

                String cleanedTimestamp = log.timestamp();
                if (cleanedTimestamp != null) {
                    if (cleanedTimestamp.contains("Z")) {
                        cleanedTimestamp = cleanedTimestamp.replace("Z", "");
                    }
                    if (cleanedTimestamp.contains("-") && cleanedTimestamp.lastIndexOf("-") > 10) {
                        cleanedTimestamp = cleanedTimestamp.substring(0, cleanedTimestamp.lastIndexOf("-"));
                    }
                }

                String cleanedMetadata = log.metadata();
                if (cleanedMetadata != null) {
                    if (cleanedMetadata.startsWith("\"") && cleanedMetadata.endsWith("\"")) {
                        cleanedMetadata = cleanedMetadata.substring(1, cleanedMetadata.length() - 1);
                    }
                    cleanedMetadata = cleanedMetadata.replace("\\\"", "\"").replace("\\\\", "\\");
                }

                String line = String.format("%s|%s|%s|%s|%s|%s|%s",
                    sanitize(cleanedTimestamp),
                    sanitize(log.appName()),
                    sanitize(log.environment()),
                    sanitize(log.level()),
                    sanitize(log.traceId()),
                    sanitize(log.message()),
                    sanitize(cleanedMetadata)
                );

                writer.write(line);
                writer.newLine();
            }

            writer.flush();
            String csvInMemory =  stringWriter.toString();

            ClickHouseNode server = ClickHouseNode.builder()
                .host("127.0.0.1")
                .port(ClickHouseProtocol.HTTP, 8123)
                .database("default")
                .credentials(com.clickhouse.client.ClickHouseCredentials.fromUserAndPassword("default", "vivo123"))
                .build();

            try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol())) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(csvInMemory.getBytes(StandardCharsets.UTF_8));

                client.write(server)
                    .table("app_logs")
                    .format(ClickHouseFormat.CSV)
                    .set("format_csv_delimiter", "|")
                    .data(inputStream)
                    .execute()
                    .get();
            }

            return new PersistResultContext(true, "CSV generated in memory with success!", null);
        } catch (Exception e) {
            HistoryPersistAdapter.log.error("{} - persist - error: fail to generate in-memory batch. message: {}", CLASS_NAME, e.getMessage());
            return new PersistResultContext(false, "Fail to generate in-memory CSV: " + e.getMessage(), e);
        }
    }

    private String sanitize(String value) {
        if (value == null) return "";
        return value.replace("\n", " ").replace("\r", " ").replace("|", "-");
    }
}
