package io.github.matheus_fsantos.chronologic_core.adapters.in.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

public record LogRequest(
    String timestamp,
    @JsonProperty("app_name")
    String appName,
    String environment,
    String level,
    @JsonProperty("trace_id")
    String traceId,
    String message,
    JsonNode metadata
) { }
