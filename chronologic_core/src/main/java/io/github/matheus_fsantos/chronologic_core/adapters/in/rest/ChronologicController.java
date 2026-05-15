package io.github.matheus_fsantos.chronologic_core.adapters.in.rest;

import io.github.matheus_fsantos.chronologic_core.adapters.in.rest.mapper.LogRequestMapper;
import io.github.matheus_fsantos.chronologic_core.adapters.in.rest.request.LogRequest;
import io.github.matheus_fsantos.chronologic_core.application.core.model.Log;
import io.github.matheus_fsantos.chronologic_core.application.ports.in.BufferizeLogsInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class ChronologicController {
    private static final String CLASS_NAME = ChronologicController.class.getName();
    private final BufferizeLogsInputPort bufferizeLogsInputPort;

    @PostMapping("/persist/logs")
    ResponseEntity<Void> persist(@RequestBody List<LogRequest> request) {
        ChronologicController.log.info("{} - persist - message: init workflow to persist generic logs in chronologic application, log.size: {} <--- HTTP/BEGIN (POST)", ChronologicController.CLASS_NAME, request.size());
        List<Log> logs = request.stream().map(LogRequestMapper::toDomain).toList();
        this.bufferizeLogsInputPort.bufferize(logs);

        ChronologicController.log.info("{} - persist - message: end workflow to persist generic logs in chronologic application ---> HTTP/BEGIN (POST)", ChronologicController.CLASS_NAME);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
