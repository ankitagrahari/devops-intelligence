package org.backendbrilliance.devopsintelligence.clients.mocks;

import java.util.*;

import org.backendbrilliance.devopsintelligence.models.Metric;
import org.springframework.stereotype.Component;

@Component
public class PrometheusMockClient {

    public List<Metric> query(String promql) {
        // Depending on the query, return different metrics
        if (promql.contains("cpu")) {
            return getCpuMetrics();
        } else if (promql.contains("memory")) {
            return getMemoryMetrics();
        } else if (promql.contains("request_rate")) {
            return getRequestRateMetrics();
        }
        return List.of();
    }

    private List<Metric> getCpuMetrics() {
        long now = System.currentTimeMillis();
        return List.of(
                new Metric(
                        "container_cpu_usage_seconds_total",
                        Map.of("pod", "api-server-1", "namespace", "production"),
                        List.of(
                                new Metric.MetricValue(now - 300000, 45.2),
                                new Metric.MetricValue(now - 240000, 46.1),
                                new Metric.MetricValue(now - 180000, 47.5),
                                new Metric.MetricValue(now - 120000, 48.9),
                                new Metric.MetricValue(now - 60000, 50.2),
                                new Metric.MetricValue(now, 51.5)
                        )
                )
        );
    }

    private List<Metric> getMemoryMetrics() {
        long now = System.currentTimeMillis();
        return List.of(
                new Metric(
                        "container_memory_usage_bytes",
                        Map.of("pod", "high-memory-pod", "namespace", "production"),
                        List.of(
                                new Metric.MetricValue(now - 300000, 1200000000),  // 1200Mi
                                new Metric.MetricValue(now - 240000, 1250000000),
                                new Metric.MetricValue(now - 180000, 1320000000),
                                new Metric.MetricValue(now - 120000, 1400000000),
                                new Metric.MetricValue(now - 60000, 1480000000),
                                new Metric.MetricValue(now, 1500000000)  // 1500Mi (at limit)
                        )
                )
        );
    }

    private List<Metric> getRequestRateMetrics() {
        long now = System.currentTimeMillis();
        return List.of(
                new Metric(
                        "http_requests_total",
                        Map.of("service", "api-server", "endpoint", "/health"),
                        List.of(
                                new Metric.MetricValue(now - 300000, 1000),
                                new Metric.MetricValue(now - 240000, 1200),
                                new Metric.MetricValue(now - 180000, 1500),  // Spike starts
                                new Metric.MetricValue(now - 120000, 2500),  // Continues
                                new Metric.MetricValue(now - 60000, 3800),   // Peak
                                new Metric.MetricValue(now, 2000)            // Recovering
                        )
                )
        );
    }
}