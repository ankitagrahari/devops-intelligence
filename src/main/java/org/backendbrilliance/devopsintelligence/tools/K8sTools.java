package org.backendbrilliance.devopsintelligence.tools;

import org.backendbrilliance.devopsintelligence.clients.mocks.K8sMockClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class K8sTools {

    private final K8sMockClient k8sClient;

    public K8sTools(K8sMockClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    @McpTool(
            name = "k8s_cluster_health",
            description = "Get overall Kubernetes cluster health including node and pod status"
    )
    public String describeClusterHealth() {
        var pods = k8sClient.listPods();
        var nodes = k8sClient.listNodes();

        long healthyPods = pods.stream()
                .filter(p -> "Running".equals(p.status()) && "1/1".equals(p.ready()))
                .count();

        long readyNodes = nodes.stream()
                .filter(n -> "Ready".equals(n.status()))
                .count();

        StringBuilder sb = new StringBuilder();
        sb.append("Cluster Health Summary:\n");
        sb.append(String.format("Nodes: %d/%d Ready\n", readyNodes, nodes.size()));
        sb.append(String.format("Pods: %d/%d Running\n", healthyPods, pods.size()));

        var unhealthyPods = pods.stream()
                .filter(p -> !"Running".equals(p.status()) || !"1/1".equals(p.ready()))
                .collect(Collectors.toList());

        if (!unhealthyPods.isEmpty()) {
            sb.append("\nUnhealthy Pods:\n");
            unhealthyPods.forEach(p ->
                    sb.append(String.format("  - %s: %s (%s)\n", p.name(), p.status(), p.ready()))
            );
        }

        return sb.toString();
    }

    @McpTool(
            name = "k8s_list_pods",
            description = "List all Kubernetes pods with their status"
    )
    public String listPods(
            @McpToolParam(description = "Filter by namespace (optional)", required = false)
            String namespace) {
        var pods = k8sClient.listPods();

        if (namespace != null && !namespace.isEmpty()) {
            pods = pods.stream()
                    .filter(p -> p.namespace().equals(namespace))
                    .collect(Collectors.toList());
        }

        StringBuilder sb = new StringBuilder("Kubernetes Pods:\n");
        pods.forEach(p ->
                sb.append(String.format(
                        "  %s/%s - Status: %s, Ready: %s, Restarts: %d, CPU: %s, Memory: %s\n",
                        p.namespace(), p.name(), p.status(), p.ready(), p.restarts(), p.cpu(), p.memory()
                ))
        );

        return sb.toString();
    }

    @McpTool(
            name = "k8s_recent_events",
            description = "Get recent Kubernetes events and status changes"
    )
    public String getRecentEvents() {
        var events = k8sClient.getRecentEvents(60);

        StringBuilder sb = new StringBuilder("Recent Kubernetes Events:\n");
        events.forEach(e -> sb.append("  • ").append(e).append("\n"));

        return sb.toString();
    }
}
