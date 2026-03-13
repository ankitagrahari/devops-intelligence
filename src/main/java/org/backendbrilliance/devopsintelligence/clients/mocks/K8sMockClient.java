package org.backendbrilliance.devopsintelligence.clients.mocks;

import java.util.List;

import org.backendbrilliance.devopsintelligence.models.Node;
import org.backendbrilliance.devopsintelligence.models.Pod;
import org.springframework.stereotype.Component;

@Component
public class K8sMockClient {

    public List<Pod> listPods() {
        return List.of(
                new Pod("api-server-1", "production", "Running", "1/1", 0, "2d", "250m", "512Mi", "gcr.io/api:1.2"),
                new Pod("api-server-2", "production", "Running", "1/1", 0, "2d", "200m", "480Mi", "gcr.io/api:1.2"),
                new Pod("api-server-3", "production", "Running", "1/1", 0, "1d", "300m", "600Mi", "gcr.io/api:1.2"),
                new Pod("worker-1", "production", "Running", "1/1", 0, "5d", "500m", "1Gi", "gcr.io/worker:3.1"),
                new Pod("worker-2", "production", "Running", "1/1", 0, "5d", "480m", "950Mi", "gcr.io/worker:3.1"),
                new Pod("worker-3", "production", "Running", "1/1", 0, "3d", "100m", "200Mi", "gcr.io/worker:3.1"),
                new Pod("cache-1", "production", "Running", "1/1", 0, "7d", "150m", "800Mi", "redis:7.0"),
                // Interesting pods for analysis:
                new Pod("problematic-service", "production", "CrashLoopBackOff", "0/1", 5, "10m", "0", "0", "gcr.io/bad-service:1.0"),
                new Pod("pending-pod", "staging", "Pending", "0/1", 0, "3m", "0", "0", "gcr.io/staging:2.0"),
                new Pod("high-memory-pod", "production", "Running", "1/1", 0, "1d", "50m", "1500Mi", "gcr.io/memhog:1.0")
        );
    }

    public List<Node> listNodes() {
        return List.of(
                new Node("node-1", "Ready", "4000m", "3900m", "2500m", "16Gi", "15Gi", "12Gi", 110, 87),
                new Node("node-2", "Ready", "4000m", "3900m", "2200m", "16Gi", "15Gi", "11Gi", 110, 82),
                new Node("node-3", "NotReady", "4000m", "3900m", "1800m", "16Gi", "15Gi", "9Gi", 110, 0)
        );
    }

    public Pod getPod(String name, String namespace) {
        return listPods().stream()
                .filter(p -> p.name().equals(name) && p.namespace().equals(namespace))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pod not found: " + name));
    }

    public List<String> getRecentEvents(int limitMinutes) {
        return List.of(
                "1m ago: Pod 'problematic-service' crashed (OOMKilled)",
                "5m ago: Node 'node-3' became NotReady",
                "12m ago: Deployment 'api-server' updated 3 replicas",
                "1h ago: Service 'cache-1' restarted",
                "2h ago: HPA scaled 'worker' to 3 replicas"
        );
    }
}
