package org.backendbrilliance.devopsintelligence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Metric(
        @JsonProperty("metric_name") String metricName,
        @JsonProperty("labels") java.util.Map<String, String> labels,
        @JsonProperty("values") List<MetricValue> values  // Time-series data
) {
    public record MetricValue(
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("value") double value
    ) {}
}