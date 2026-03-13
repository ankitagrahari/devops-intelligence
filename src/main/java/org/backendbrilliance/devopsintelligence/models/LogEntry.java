package org.backendbrilliance.devopsintelligence.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LogEntry(
        @JsonProperty("timestamp") String timestamp,
        @JsonProperty("service") String service,
        @JsonProperty("level") String level,  // INFO, WARN, ERROR
        @JsonProperty("message") String message,
        @JsonProperty("pod_name") String podName,
        @JsonProperty("namespace") String namespace
) {}
