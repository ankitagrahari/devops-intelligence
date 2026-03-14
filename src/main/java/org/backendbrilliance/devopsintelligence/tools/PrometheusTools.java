package org.backendbrilliance.devopsintelligence.tools;

import org.backendbrilliance.devopsintelligence.clients.PrometheusClient;
import org.backendbrilliance.devopsintelligence.models.MetricResult;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PrometheusTools {

    private final PrometheusClient prometheusClient;

    public PrometheusTools(PrometheusClient prometheusClient) {
        this.prometheusClient = prometheusClient;
    }

    @McpTool(
            name = "prometheus_cluster_health",
            description = "Get overall cluster health metrics"
    )
    public String getClusterHealth() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Cluster Health Summary ===\n\n");

            // Query CPU usage
            List<MetricResult> cpuResults = prometheusClient.query(
                    "avg(rate(container_cpu_usage_seconds_total[5m]))"
            );

            if (!cpuResults.isEmpty()) {
                Double cpuValue = cpuResults.get(0).getValueAsDouble();
                sb.append("CPU Usage: ").append(String.format("%.4f", cpuValue)).append(" cores\n");
            }

            // Query memory usage
            List<MetricResult> memoryResults = prometheusClient.query(
                    "avg(container_memory_usage_bytes)"
            );

            if (!memoryResults.isEmpty()) {
                Double memoryBytes = memoryResults.get(0).getValueAsDouble();
                double memoryGB = memoryBytes / (1024 * 1024 * 1024);
                sb.append("Memory Usage: ").append(String.format("%.2f", memoryGB)).append(" GB\n");
            }

            // Query error rate
            List<MetricResult> errorResults = prometheusClient.query(
                    "rate(http_requests_total{status=~'5..'}[5m])"
            );

            if (!errorResults.isEmpty()) {
                Double errorRate = errorResults.get(0).getValueAsDouble();
                sb.append("Error Rate: ").append(String.format("%.2f", errorRate * 100)).append("%\n");
            }

            return sb.toString();
        } catch (IOException e) {
            return "Error querying Prometheus: " + e.getMessage();
        }
    }

    @McpTool(
            name = "prometheus_top_cpu_consumers",
            description = "Get top CPU consuming pods"
    )
    public String getTopCpuConsumers(
            @McpToolParam(description = "Number of results (default 5)", required = false)
            Integer limit) {

        try {
            int topN = limit != null ? limit : 5;

            List<MetricResult> results = prometheusClient.query(
                    "topk(" + topN + ", rate(container_cpu_usage_seconds_total[5m]))"
            );

            StringBuilder sb = new StringBuilder("Top " + topN + " CPU Consumers:\n\n");

            int rank = 1;
            for (MetricResult result : results) {
                String podName = result.getLabel("pod_name");
                String namespace = result.getLabel("namespace");
                Double cpuValue = result.getValueAsDouble();

                sb.append(rank).append(". Pod: ").append(podName)
                        .append(" (Namespace: ").append(namespace).append(")\n")
                        .append("   CPU: ").append(String.format("%.4f", cpuValue)).append(" cores\n\n");

                rank++;
            }

            return sb.toString();
        } catch (IOException e) {
            return "Error querying Prometheus: " + e.getMessage();
        }
    }

    @McpTool(
            name = "prometheus_memory_pressure",
            description = "Get pods with high memory usage"
    )
    public String getMemoryPressure(
            @McpToolParam(description = "Memory threshold in GB (default 1)", required = false)
            Double thresholdGB) {

        try {
            double thresholdBytes = (thresholdGB != null ? thresholdGB : 1.0) * 1024 * 1024 * 1024;

            List<MetricResult> results = prometheusClient.query(
                    "container_memory_usage_bytes > " + thresholdBytes
            );

            StringBuilder sb = new StringBuilder("Pods with High Memory Usage (> " + thresholdGB + " GB):\n\n");

            for (MetricResult result : results) {
                String podName = result.getLabel("pod_name");
                Double memoryBytes = result.getValueAsDouble();
                double memoryGB = memoryBytes / (1024 * 1024 * 1024);

                sb.append("Pod: ").append(podName).append("\n")
                        .append("Memory: ").append(String.format("%.2f", memoryGB)).append(" GB\n\n");
            }

            return sb.toString().isEmpty() ? "No pods exceeding memory threshold" : sb.toString();
        } catch (IOException e) {
            return "Error querying Prometheus: " + e.getMessage();
        }
    }
}