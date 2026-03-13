package org.backendbrilliance.devopsintelligence.tools;

import org.backendbrilliance.devopsintelligence.clients.mocks.PrometheusMockClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class PrometheusTools {

    private final PrometheusMockClient prometheusClient;

    public PrometheusTools(PrometheusMockClient prometheusClient) {
        this.prometheusClient = prometheusClient;
    }

    @McpTool(
            name = "prometheus_query_cpu",
            description = "Query CPU usage metrics from Prometheus"
    )
    public String queryCpuMetrics(
            @McpToolParam(description = "Service or pod name to query", required = false)
            String service) {
        var metrics = prometheusClient.query("cpu");

        StringBuilder sb = new StringBuilder("CPU Metrics:\n");
        metrics.forEach(m -> {
            sb.append(String.format("  %s:\n", m.metricName()));
            m.values().stream().limit(3).forEach(v ->
                    sb.append(String.format("    %.2f at %d\n", v.value(), v.timestamp()))
            );
        });

        return sb.toString();
    }

    @McpTool(
            name = "prometheus_query_memory",
            description = "Query memory usage metrics from Prometheus"
    )
    public String queryMemoryMetrics() {
        var metrics = prometheusClient.query("memory");

        StringBuilder sb = new StringBuilder("Memory Metrics:\n");
        metrics.forEach(m -> {
            sb.append(String.format("  %s: %.0f bytes\n", m.metricName(),
                    m.values().get(m.values().size() - 1).value()));
        });

        return sb.toString();
    }
}
